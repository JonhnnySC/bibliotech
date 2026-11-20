import api from "./api";
import type { LoginResponse } from "../types/auth";

export async function login(email: string, senha: string): Promise<LoginResponse> {
  const resposta = await api.post<LoginResponse>("/auth/login", { email, senha });
  return resposta.data;
}

export async function registrar(corpo: { nome: string; email: string; cpf: string; senha: string }): Promise<void> {
  await api.post("/auth/registro", corpo);
}

export async function logoutNoServidor(): Promise<void> {
  try {
    await api.post("/auth/logout");
  } catch {
    /* token ja invalido: tudo bem */
    
  }
}
