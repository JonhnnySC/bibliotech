"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { api } from "../../lib/api";
import type { Perfil, StatusUsuario, Usuario } from "../../types/usuario";

/**
 * Rota /usuarios — lista + ações de gestão (perfil, status, exclusão).
 * Criação em /usuarios/novo; edição em /usuarios/[id]/editar.
 */
export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [busca, setBusca] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<Usuario[]>("/usuarios")
      .then((d) => { if (vivo) { setUsuarios(d.filter((u) => u.status !== "EXCLUIDO")); setErro(""); } })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar usuários"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, []);

  async function recarregar() {
    try {
      const d = await api<Usuario[]>("/usuarios");
      setUsuarios(d.filter((u) => u.status !== "EXCLUIDO"));
      setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar usuários");
    } finally {
      setCarregando(false);
    }
  }

  // Só ADMIN vê os seletores de promoção (compara com quem está logado)
  const emailLogado = typeof window !== "undefined" ? localStorage.getItem("email") : null;
  const souAdmin = usuarios.some((u) => u.email === emailLogado && String(u.perfil) === "ADMINISTRADOR");

  const filtrados = usuarios.filter((u) =>
    u.nome.toLowerCase().includes(busca.toLowerCase()) ||
    u.email.toLowerCase().includes(busca.toLowerCase())
  );

  /** Promover/rebaixar — endpoint exclusivo de ADMIN. */
  async function mudarPerfil(id: number, perfil: Perfil) {
    try {
      await api(`/usuarios/${id}/perfil`, { method: "PATCH", body: JSON.stringify({ perfil }) });
      recarregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao alterar perfil (apenas ADMIN pode)");
    }
  }

  /** Bloquear/ativar — ADMIN. */
  async function toggleStatus(id: number, atual: StatusUsuario) {
    const status = atual === "ATIVO" ? "BLOQUEADO" : "ATIVO";
    try {
      await api(`/usuarios/${id}/status`, { method: "PATCH", body: JSON.stringify({ status }) });
      recarregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao alterar status");
    }
  }

  /** Soft delete. */
  async function excluir(id: number) {
    if (!confirm("Excluir este usuário? (soft delete)")) return;
    try {
      await api(`/usuarios/${id}`, { method: "DELETE" });
      recarregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao excluir usuário");
    }
  }

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Usuários</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Consultando os registros..." : `${filtrados.length} usuário(s).`}
          </p>
        </div>
        <Link href="/usuarios/novo" className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">
          Novo usuário
        </Link>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      <input
        value={busca} onChange={(e) => setBusca(e.target.value)}
        placeholder="Buscar por nome ou e-mail"
        className="mb-6 w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
      />

      <div className="overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
        <table className="w-full text-left">
          <thead className="bg-musgo text-ouro-neon">
            <tr>
              <th className="p-3">Nome</th><th className="p-3">E-mail</th>
              <th className="p-3">Perfil</th><th className="p-3">Status</th><th className="p-3 text-right">Ações</th>
            </tr>
          </thead>
          <tbody>
            {!carregando && filtrados.length === 0 && (
              <tr><td colSpan={5} className="p-6 text-center text-lg">Nenhum usuário encontrado.</td></tr>
            )}
            {filtrados.map((u) => (
              <tr key={u.id} className="border-t border-marrom-claro hover:bg-noite/30 transition-colors">
                <td className="p-3 font-medium">{u.nome}</td>
                <td className="p-3 text-ouro/80">{u.email}</td>
                <td className="p-3">
                  {souAdmin ? (
                    <select value={u.perfil} onChange={(e) => mudarPerfil(u.id, e.target.value as Perfil)}
                      aria-label={`Perfil de ${u.nome}`}
                      className="rounded border border-ouro bg-noite/80 px-2 py-1 text-xs text-ouro-neon">
                      <option value="LEITOR">LEITOR</option>
                      <option value="BIBLIOTECARIO">BIBLIOTECARIO</option>
                      <option value="ADMINISTRADOR">ADMINISTRADOR</option>
                    </select>
                  ) : (
                    <span className={`rounded px-2 py-1 text-xs font-bold ${
                      u.perfil === "ADMINISTRADOR" ? "bg-red-900/50 text-red-300" :
                      u.perfil === "BIBLIOTECARIO" ? "bg-amber-900/50 text-amber-300" :
                      "bg-verde-neon/20 text-verde-neon"}`}>{u.perfil}</span>
                  )}
                </td>
                <td className="p-3">
                  <span className={`rounded px-2 py-1 text-xs font-bold ${u.status === "ATIVO" ? "bg-verde-neon/20 text-verde-neon" : "bg-red-900/50 text-red-300"}`}>
                    {u.status}
                  </span>
                </td>
                <td className="p-3 text-right">
                  <div className="flex justify-end gap-2">
                    <Link href={`/usuarios/${u.id}/editar`}
                      className="rounded border border-verde-neon px-3 py-1 text-sm text-verde-neon hover:bg-verde-neon hover:text-noite">
                      Editar
                    </Link>
                    <button onClick={() => toggleStatus(u.id, u.status)}
                      className="rounded border border-ouro px-3 py-1 text-sm text-ouro-neon hover:bg-ouro hover:text-noite">
                      {u.status === "ATIVO" ? "Bloquear" : "Ativar"}
                    </button>
                    <button onClick={() => excluir(u.id)}
                      className="rounded border border-red-400 px-3 py-1 text-sm text-red-300 hover:bg-red-950/50">
                      Excluir
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}