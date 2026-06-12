import { auth } from "@/auth";
import { serverApiUrl } from "@/lib/api-url";
import { NextResponse } from "next/server";

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();

  if (!session?.accessToken) {
    return NextResponse.json({ message: "Please sign in to delete a review" }, { status: 401 });
  }

  const { id } = await params;
  const res = await fetch(serverApiUrl(`/reviews/${id}`), {
    method: "DELETE",
    headers: {
      authorization: `Bearer ${session.accessToken}`,
    },
  });

  if (res.status === 204) {
    return new Response(null, { status: 204 });
  }

  const text = await res.text();
  const contentType = res.headers.get("content-type") ?? "application/json";

  return new Response(text, {
    status: res.status,
    headers: {
      "content-type": contentType,
    },
  });
}
