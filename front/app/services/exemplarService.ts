import api from "./api";
import type { Exemplar, StatusExemplar } from "../types/usuario";

export async function listarExemplaresPorLivro(livroId: number | string): Promise<Exemplar[]> {
  return (await api.get<Exemplar[]>(`/exemplares/livro/${livroId}`)).data;
}

export async function criarExemplar(corpo: unknown): Promise<void> {
  await api.post("/exemplares", corpo);
}

export async function atualizarStatusExemplar(id: number, status: StatusExemplar): Promise<void> {
  await api.patch(`/exemplares/${id}/status`, null, { params: { statusExemplar: status } });
}
