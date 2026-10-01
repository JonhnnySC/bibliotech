const BASE = "http://localhost:8080";

// fetch com o token JWT; se o servidor responder 401, volta pro login
export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;
  const res = await fetch(BASE + path, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init.headers,
    },
  });
  if (res.status === 401 && token && typeof window !== "undefined") {
    localStorage.removeItem("token");
    window.location.href = "/login";
  }
  if (!res.ok) throw new Error((await res.json().catch(() => ({}))).error ?? `Erro ${res.status}`);
  return res.status === 204 ? (undefined as T) : res.json();
}
