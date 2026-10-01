"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "../../../lib/api";
import { cpfValido } from "../../../lib/validacoes";
import type { Usuario } from "../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export type UsuarioFormValues = { nome: string; email: string; cpf: string; senha: string };
export const usuarioFormVazio: UsuarioFormValues = { nome: "", email: "", cpf: "", senha: "" };

/** Hidrata o form a partir de um usuário existente (edição não traz senha). */
export function valoresDoUsuario(u: Usuario): UsuarioFormValues {
  return { nome: u.nome ?? "", email: u.email ?? "", cpf: u.cpf ?? "", senha: "" };
}

type Props = { modo: "criar" | "editar"; inicial: UsuarioFormValues; usuarioId?: number };

/**
 * Form compartilhado entre /usuarios/novo e /usuarios/[id]/editar.
 * Perfil NÃO entra aqui: promoção/rebaixamento é ação de admin na lista (PATCH /perfil).
 */
export default function UsuarioForm({ modo, inicial, usuarioId }: Props) {
  const router = useRouter();
  const [f, setF] = useState<UsuarioFormValues>(inicial);
  const [salvando, setSalvando] = useState(false);
  const [erroForm, setErroForm] = useState("");

  const mudar = (k: keyof UsuarioFormValues) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setF({ ...f, [k]: e.target.value });

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    if (!cpfValido(f.cpf)) {
      setErroForm("CPF inválido (dígito verificador). Para testar: 52998224725.");
      return;
    }
    setSalvando(true);
    try {
      if (modo === "criar") {
        // Nasce sempre LEITOR; promoção depois, pela tabela
        await api<Usuario>("/usuarios", { method: "POST", body: JSON.stringify(f) });
      } else {
        // Edição não mexe em senha/perfil (endpoints próprios)
        await api<Usuario>(`/usuarios/${usuarioId}`, {
          method: "PUT",
          body: JSON.stringify({ nome: f.nome, email: f.email, cpf: f.cpf }),
        });
      }
      router.push("/usuarios");
    } catch (err) {
      setErroForm(err instanceof Error ? err.message : "Falha ao salvar (e-mail/CPF já cadastrados?)");
      setSalvando(false);
    }
  }

  return (
    <form onSubmit={salvar} className="borda-neon rounded-lg bg-marrom/70 p-6">
      <label className="mb-1 block text-ouro-neon">Nome completo</label>
      <input required value={f.nome} onChange={mudar("nome")} className={campo} />

      <label className="mb-1 block text-ouro-neon">E-mail</label>
      <input required type="email" value={f.email} onChange={mudar("email")} className={campo} />

      <label className="mb-1 block text-ouro-neon">CPF</label>
      <input required value={f.cpf} onChange={mudar("cpf")} placeholder="11 dígitos válidos" className={campo} />

      {modo === "criar" && (
        <>
          <label className="mb-1 block text-ouro-neon">Senha inicial</label>
          <input required type="password" minLength={6} value={f.senha} onChange={mudar("senha")} className={campo} />
        </>
      )}

      {erroForm && <p className="mb-3 text-red-400">{erroForm}</p>}

      <div className="flex justify-end gap-3">
        <button type="button" onClick={() => router.back()}
          className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
          Cancelar
        </button>
        <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
          {salvando ? "Salvando..." : modo === "criar" ? "Criar" : "Salvar alterações"}
        </button>
      </div>
    </form>
  );
}