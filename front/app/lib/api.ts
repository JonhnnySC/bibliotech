const BASE = "http://localhost:8080";

// Lê o corpo do erro tentando vários formatos que o backend pode mandar
async function extrairMensagemErro(res: Response): Promise<string> {
  const body = await res.json().catch(() => ({}));
  return (
    body.detail ??      // ProblemDetail (Spring Boot 3/4) -> mensagem específica
    body.error ??       // seu formato antigo de auth
    body.message ??     // fallback genérico
    body.erro ??        // variação em pt
    `Erro ${res.status}`
  );
}

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

  if (!res.ok) {
    throw new Error(await extrairMensagemErro(res));
  }

  return res.status === 204 ? (undefined as T) : res.json();
}