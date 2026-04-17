import { type NextRequest, NextResponse } from "next/server";

const protectedPrefixes = ["/admin"];

export function proxy(request: NextRequest) {
  const hasToken = Boolean(request.cookies.get("auth_token")?.value);
  const isProtected = protectedPrefixes.some((prefix) =>
    request.nextUrl.pathname.startsWith(prefix),
  );

  if (isProtected && !hasToken) {
    const loginUrl = new URL("/login", request.url);
    loginUrl.searchParams.set("redirect", request.nextUrl.pathname);
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/admin/:path*"],
};
