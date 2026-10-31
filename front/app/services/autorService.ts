import api from "./api";
import type { Autor } from "../types/autor";

export async function listarAutores(): Promise<Autor[]> {
  return (await api.get<Autor[]>("/autores")).data;
}

export async function buscarAutor(id: number | string): Promise<Autor> {
  return (await api.get<Autor>(`/autores/${id}`)).data;
}

export async function criarAutor(corpo: unknown): Promise<Autor> {
  return (await api.post<Autor>("/autores", corpo)).data;
}

export async function atualizarAutor(id: number | string, corpo: unknown): Promise<Autor> {
  return (await api.put<Autor>(`/autores/${id}`, corpo)).data;
}

export async function excluirAutor(id: number | string): Promise<void> {
  await api.delete(`/autores/${id}`);
}
