import api from "./api";
import type { Emprestimo } from "../types/usuario";

export async function listarEmprestimosAtivos(): Promise<Emprestimo[]> {
  return (await api.get<Emprestimo[]>("/emprestimos/ativos")).data;
}

export async function registrarEmprestimo(corpo: { leitorId: number; exemplarId: number }): Promise<void> {
  await api.post("/emprestimos", corpo);
}

export async function devolverEmprestimo(id: number): Promise<void> {
  await api.patch(`/emprestimos/${id}/devolver`);
}
