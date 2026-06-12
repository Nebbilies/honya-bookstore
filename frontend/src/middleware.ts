import { auth } from "@/auth";
import { NextResponse } from "next/server";
import { UserRole } from "@/types/roles";

function extractRolesFromAccessToken(accessToken?: string | null): string[] {
  if (!accessToken) return [];

  const parts = accessToken.split(".");
  if (parts.length < 2) return [];

  try {
    const payloadBase64Url = parts[1];
    const payloadBase64 = payloadBase64Url
      .replace(/-/g, "+")
      .replace(/_/g, "/")
      .padEnd(Math.ceil(payloadBase64Url.length / 4) * 4, "=");

    const payload = JSON.parse(atob(payloadBase64)) as { realm_access?: { roles?: unknown } };
    const roles = payload.realm_access?.roles;

    if (!Array.isArray(roles)) return [];

    return roles
      .filter((value): value is string => typeof value === "string")
      .map((value) => value.toUpperCase());
  } catch {
    return [];
  }
}

export default auth((req) => {
  const { nextUrl } = req;
  const { pathname } = req.nextUrl;
  const reqHeaders = new Headers(req.headers);
    reqHeaders.set("x-url", req.nextUrl.pathname);

  const refreshFailed =
    (req.auth as { error?: string } | null | undefined)?.error === "RefreshAccessTokenError";

  if (!req.auth?.user || refreshFailed) {
    const loginUrl = new URL("/api/auth/signin", nextUrl);
    const callbackOrigin = process.env.NEXTAUTH_URL ?? process.env.AUTH_URL;
    const protocol = req.headers.get("x-forwarded-proto") ?? nextUrl.protocol.replace(":", "");
    const host = req.headers.get("x-forwarded-host") ?? req.headers.get("host") ?? nextUrl.host;
    const callbackUrl = callbackOrigin
      ? new URL(`${nextUrl.pathname}${nextUrl.search}`, callbackOrigin).toString()
      : `${protocol}://${host}${nextUrl.pathname}${nextUrl.search}`;
    loginUrl.searchParams.set("callbackUrl", callbackUrl);

    return NextResponse.redirect(loginUrl);
  }

  if (pathname.startsWith("/admin")) {
    type AuthWithRole = {
      role?: string | null;
      roles?: string[] | null;
      accessToken?: string | null;
      realm_access?: { roles?: string[] | null } | null;
      token?: {
        role?: string | null;
        roles?: string[] | null;
        realm_access?: { roles?: string[] | null } | null;
      } | null;
      user?: {
        role?: string | null;
        roles?: string[] | null;
        realm_access?: { roles?: string[] | null } | null;
      } | null;
    };

    const authWithRole = req.auth as AuthWithRole | null | undefined;

    const accessTokenRoles = extractRolesFromAccessToken(authWithRole?.accessToken ?? null);

    const claimRoles = [
      ...accessTokenRoles,
      ...(authWithRole?.realm_access?.roles ?? []),
      ...(authWithRole?.token?.realm_access?.roles ?? []),
      ...(authWithRole?.user?.realm_access?.roles ?? []),
      ...(authWithRole?.roles ?? []),
      ...(authWithRole?.token?.roles ?? []),
      ...(authWithRole?.user?.roles ?? []),
    ]
      .filter((value): value is string => typeof value === "string")
      .map((value) => value.toUpperCase());

    const roleSet = new Set(claimRoles);
    const rawRole = authWithRole?.role ?? authWithRole?.token?.role ?? authWithRole?.user?.role ?? null;
    const role = typeof rawRole === "string" ? rawRole.toUpperCase() : null;

    const isAllowed =
      roleSet.has(UserRole.ADMIN) ||
      roleSet.has(UserRole.STAFF) ||
      role === UserRole.ADMIN ||
      role === UserRole.STAFF;

    console.log("[middleware-admin-debug]", {
      pathname,
      isAllowed,
      role,
      accessTokenRoles,
      claimRoles,
      authKeys: authWithRole ? Object.keys(authWithRole) : [],
      userKeys: authWithRole?.user ? Object.keys(authWithRole.user) : [],
      tokenKeys: authWithRole?.token ? Object.keys(authWithRole.token) : [],
    });

    if (!isAllowed) {
      return NextResponse.redirect(new URL("/", req.url));
    }
  }

  return NextResponse.next({
    request: {
        headers: reqHeaders,
    },
  });
});

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|assets|favicon.svg|favicon.png|sitemap.xml|robots.txt).*)",
  ],
};