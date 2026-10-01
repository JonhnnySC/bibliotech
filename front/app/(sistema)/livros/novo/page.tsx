"use client";
import Link from "next/link";
import LivroForm, { livroFormVazio } from "../components/LivroForm";

export default function NovoLivroPage() {
  return (
    <div className="mx-auto max-w-4xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/livros" className="hover:text-verde-neon">← Voltar ao acervo</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Novo livro</h1>
      <LivroForm modo="criar" inicial={livroFormVazio} />
    </div>
  );
}