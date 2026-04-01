import { UserRole } from "@/types/roles";
import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

export const { handlers, signIn, signOut, auth } = NextAuth({
  providers: [
    Keycloak({
      authorization: `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/auth?scope=openid+email+profile+address+phone+roles`,
    }),
  ],
  callbacks: {
    authorized({ auth }) {
      return !!auth?.user;
    },
    async jwt({ token, account, profile }) {
      token.userId = token.userId || profile?.sub || "";
      if (account?.access_token) {
        token.accessToken = account.access_token;
      }
      if (profile?.realm_access) {
        token.role = profile.realm_access.roles.filter((role: string) =>
            Object.values(UserRole).includes(role as UserRole),
        )[0] as UserRole | null;
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
      return token;
    },
    async session({ session, token }) {
      session.accessToken = token.accessToken;
      session.role = token.role;
      session.address = token.address;
      session.phoneNumber = token.phoneNumber;
      if (token.sub) {
        session.user.id = token.userId;
      }
      return session;
    },
  },
});
