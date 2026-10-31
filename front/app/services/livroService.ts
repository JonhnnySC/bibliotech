import api from "./api";
import type { Livro } from "../types/usuario";

export async function listarLivros(): Promise<Livro[]> {
  return (await api.get<Livro[]>("/livros")).data;
}

export async function buscarLivro(id: number | string): Promise<Livro> {
  return (await api.get<Livro>(`/livros/${id}`)).data;
}

export async function criarLivro(corpo: unknown): Promise<Livro> {
  return (await api.post<Livro>("/livros", corpo)).data;
}

export async function atualizarLivro(id: number | string, corpo: unknown): Promise<Livro> {
  return (await api.put<Livro>(`/livros/${id}`, corpo)).data;
}

export async function excluirLivro(id: number | string): Promise<void> {
  await api.delete(`/livros/${id}`);
}
