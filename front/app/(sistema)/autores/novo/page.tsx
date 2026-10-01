"use client";
import Link from "next/link";
import AutorForm from "../components/AutorForm";
import type { AutorForm as AutorFormType } from "../../../types/autor";

const vazio: AutorFormType = {
  nome: "",
  nacionalidade: "",
  dataNascimento: "",
  fotoUrl: "",
  livroIds: [],
};

export default function NovoAutorPage() {
  return (
    <div className="mx-auto max-w-4xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/autores" className="hover:text-verde-neon">← Voltar aos autores</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Novo autor</h1>
      <AutorForm modo="criar" inicial={vazio} />
    </div>
  );
}