import Cookies from "js-cookie";

// O token fica em COOKIE (não em localStorage), como na Aula 19.10.
export const TOKEN_COOKIE = "token";
export const EMAIL_COOKIE = "email";

export function salvarSessao(token: string, email: string, expiraEm?: string) {
  const opcoes: Cookies.CookieAttributes = {
    sameSite: "strict",
    secure: typeof window !== "undefined" && window.location.protocol === "https:",
    // o cookie expira junto com o token (o back devolve "expiraEm")
    expires: expiraEm ? new Date(expiraEm) : undefined,
  };
  Cookies.set(TOKEN_COOKIE, token, opcoes);
  Cookies.set(EMAIL_COOKIE, email, opcoes);
}

export function limparSessao() {
  Cookies.remove(TOKEN_COOKIE);
  Cookies.remove(EMAIL_COOKIE);
}

export function lerSessao(): { token: string | null; email: string | null } {
  return {
    token: Cookies.get(TOKEN_COOKIE) ?? null,
    email: Cookies.get(EMAIL_COOKIE) ?? null,
  };
}

export function obterEmailLogado(): string | null {
  return Cookies.get(EMAIL_COOKIE) ?? null;
}
