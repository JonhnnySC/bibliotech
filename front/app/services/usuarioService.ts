import api from "./api";
import type { Perfil, StatusUsuario, Usuario } from "../types/usuario";

export async function listarUsuarios(): Promise<Usuario[]> {
  return (await api.get<Usuario[]>("/usuarios")).data;
}

export async function buscarUsuario(id: number | string): Promise<Usuario> {
  return (await api.get<Usuario>(`/usuarios/${id}`)).data;
}

export async function criarUsuario(corpo: unknown): Promise<Usuario> {
  return (await api.post<Usuario>("/usuarios", corpo)).data;
}

export async function atualizarUsuario(id: number | string, corpo: unknown): Promise<Usuario> {
  return (await api.put<Usuario>(`/usuarios/${id}`, corpo)).data;
}

export async function atualizarPerfilUsuario(id: number | string, perfil: Perfil): Promise<void> {
  await api.patch(`/usuarios/${id}/perfil`, { perfil });
}

export async function atualizarStatusUsuario(id: number | string, status: StatusUsuario): Promise<void> {
  await api.patch(`/usuarios/${id}/status`, { status });
}

export async function excluirUsuario(id: number | string): Promise<void> {
  await api.delete(`/usuarios/${id}`);
}
