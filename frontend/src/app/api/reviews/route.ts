import { auth } from "@/auth";
import { serverApiUrl } from "@/lib/api-url";
import { NextResponse } from "next/server";

export async function POST(request: Request) {
  const session = await auth();

  if (!session?.accessToken) {
    return NextResponse.json({ message: "Please sign in to submit a review" }, { status: 401 });
  }

  const body = await request.json();
  const res = await fetch(serverApiUrl("/reviews"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      authorization: `Bearer ${session.accessToken}`,
    },
    body: JSON.stringify(body),
  });

  const text = await res.text();
  const contentType = res.headers.get("content-type") ?? "application/json";

  return new Response(text, {
    status: res.status,
    headers: {
      "content-type": contentType,
    },
  });
}
