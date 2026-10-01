"use client";
import Link from "next/link";
import UsuarioForm, { usuarioFormVazio } from "../components/UsuarioForm";

/** Rota /usuarios/novo — criação (nasce LEITOR). */
export default function NovoUsuarioPage() {
  return (
    <div className="mx-auto max-w-2xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/usuarios" className="hover:text-verde-neon">← Voltar aos usuários</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Novo usuário</h1>
      <UsuarioForm modo="criar" inicial={usuarioFormVazio} />
    </div>
  );
}