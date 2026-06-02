import { UserRole } from "@/types/roles";
import NextAuth from "next-auth";
import type { JWT } from "next-auth/jwt";
import Keycloak from "next-auth/providers/keycloak";

async function refreshAccessToken(token: JWT): Promise<JWT> {
  const issuer = process.env.AUTH_KEYCLOAK_ISSUER;
  const clientId = process.env.AUTH_KEYCLOAK_ID;
  const clientSecret = process.env.AUTH_KEYCLOAK_SECRET;

  if (!issuer || !clientId || !token.refreshToken) {
    return { ...token, error: "RefreshAccessTokenError" };
  }

  const body = new URLSearchParams({
    grant_type: "refresh_token",
    refresh_token: token.refreshToken,
    client_id: clientId,
  });

  if (clientSecret) {
    body.set("client_secret", clientSecret);
  }

  try {
    const response = await fetch(`${issuer}/protocol/openid-connect/token`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body,
    });

    const refreshed = await response.json();

    if (!response.ok) {
      return { ...token, error: "RefreshAccessTokenError" };
    }

    return {
      ...token,
      accessToken: refreshed.access_token,
      accessTokenExpiresAt: Date.now() + refreshed.expires_in * 1000,
      refreshToken: refreshed.refresh_token ?? token.refreshToken,
      error: undefined,
    };
  } catch {
    return { ...token, error: "RefreshAccessTokenError" };
  }
}

export const { handlers, signIn, signOut, auth } = NextAuth({
  providers: [
    Keycloak({
      clientId: process.env.AUTH_KEYCLOAK_ID,
      clientSecret: process.env.AUTH_KEYCLOAK_SECRET,
      issuer: process.env.AUTH_KEYCLOAK_ISSUER,
      authorization: `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/auth?scope=openid+email+profile+address+phone+roles`,
    }),
  ],
  callbacks: {
    authorized({ auth }) {
      return !!auth?.user;
    },
    async jwt({ token, account, profile }) {
      token.userId = token.userId || profile?.sub || token.sub || "";

      if (account) {
        if (account.access_token) {
          token.accessToken = account.access_token;
        }
        token.refreshToken = account.refresh_token ?? token.refreshToken;
        token.accessTokenExpiresAt = account.expires_at ? account.expires_at * 1000 : undefined;
      }

      if (profile?.realm_access?.roles) {
        token.role = profile.realm_access.roles
          .map((role: string) => role.toUpperCase())
          .find((role) => Object.values(UserRole).includes(role as UserRole)) as UserRole | undefined ?? null;
      }

      if (!token.address && profile?.address) {
        token.address = {
          street_address: profile.address.street_address!,
          locality: profile.address.locality!,
        };
      }

      if (!token.phoneNumber && profile?.phone_number) {
        token.phoneNumber = profile.phone_number;
      }

      if (!token.accessTokenExpiresAt || Date.now() < token.accessTokenExpiresAt - 5_000) {
        return token;
      }

      return refreshAccessToken(token);
    },
    async session({ session, token }) {
      session.accessToken = token.accessToken;
      session.role = token.role;
      session.address = token.address;
      session.phoneNumber = token.phoneNumber;
      session.error = token.error;
      if (token.sub) {
        session.user.id = token.userId;
      }
      return session;
    },
  },
});
