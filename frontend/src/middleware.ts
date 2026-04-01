import { auth } from "@/auth";
import { NextResponse } from "next/server";
import { UserRole } from "@/types/roles";

export default auth((req) => {
  const { nextUrl } = req;
  const { pathname } = req.nextUrl;
  const reqHeaders = new Headers(req.headers);
    reqHeaders.set("x-url", req.nextUrl.pathname);

  if (!req.auth?.user) {
    const loginUrl = new URL("/api/auth/signin", nextUrl);
    loginUrl.searchParams.set("callbackUrl", nextUrl.href);

    return NextResponse.redirect(loginUrl);
  }

  if (pathname.startsWith("/admin")) {
    if (req.auth.role !== UserRole.ADMIN && req.auth.role !== UserRole.STAFF) {
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