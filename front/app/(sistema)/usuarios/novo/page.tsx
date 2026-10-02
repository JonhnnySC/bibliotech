"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { api } from "../../../lib/api";
import type { Usuario } from "../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export default function NovoUsuarioPage() {
  const router = useRouter();
  const [form, setForm] = useState({ nome: "", email: "", cpf: "", senha: "" });
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  const mudar = (k: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value });

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSalvando(true);
    try {
      await api<Usuario>("/usuarios", {
        method: "POST",
        body: JSON.stringify({
          nome: form.nome,
          email: form.email,
          cpf: form.cpf,
          senha: form.senha,
        }),
      });
      router.push("/usuarios");
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao cadastrar");
      setSalvando(false);
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/usuarios" className="hover:text-verde-neon">← Voltar aos usuários</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Novo usuário</h1>

      <form onSubmit={salvar} className="borda-neon rounded-lg bg-marrom/70 p-6">
        <label className="mb-1 block text-ouro-neon">Nome *</label>
        <input required value={form.nome} onChange={mudar("nome")} className={campo} />

        <label className="mb-1 block text-ouro-neon">E-mail *</label>
        <input required type="email" value={form.email} onChange={mudar("email")} className={campo} />

        <label className="mb-1 block text-ouro-neon">CPF *</label>
        <input required value={form.cpf} onChange={mudar("cpf")} className={campo} placeholder="000.000.000-00" />

        <label className="mb-1 block text-ouro-neon">Senha *</label>
        <input required type="password" value={form.senha} onChange={mudar("senha")} className={campo} autoComplete="new-password" />

        <p className="mb-4 text-xs text-ouro/60">
          O usuário é criado como LEITOR e ATIVO. Para promover, use o seletor de perfil na lista.
        </p>

        {erro && <p className="mb-3 text-red-400">{erro}</p>}

        <div className="flex justify-end gap-3">
          <button type="button" onClick={() => router.back()}
            className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
            Cancelar
          </button>
          <button disabled={salvando}
            className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
            {salvando ? "Salvando..." : "Cadastrar usuário"}
          </button>
        </div>
      </form>
    </div>
  );
}