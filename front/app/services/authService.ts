import api from "./api";
import type { LoginResponse } from "../types/auth";

export async function login(email: string, senha: string): Promise<LoginResponse> {
  const resposta = await api.post<LoginResponse>("/auth/login", { email, senha });
  return resposta.data;
}

// Revoga o token no servidor. Erro e ignorado de proposito: se o token ja
// venceu, o objetivo (nao valer mais) ja foi alcancado.
export async function logoutNoServidor(): Promise<void> {
  try {
    await api.post("/auth/logout");
  } catch {
    /* token ja invalido: tudo bem */
  }
}