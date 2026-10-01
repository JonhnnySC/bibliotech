"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import LivroForm, { valoresDoLivro } from "../../components/LivroForm";
import { api } from "../../../../lib/api";
import type { Livro as LivroT } from "../../../../types/usuario";

export default function EditarLivroPage() {
  const params = useParams();
  const raw = params?.id;
  const id = Number(Array.isArray(raw) ? raw[0] : raw);

  const [livro, setLivro] = useState<LivroT | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<LivroT>(`/livros/${id}`)
      .then((d) => { if (vivo) setLivro(d); })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar o livro"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, [id]);

  if (carregando) return <p className="text-lg">Abrindo o grimório...</p>;
  if (erro || !livro) return <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro || "Livro não encontrado."}</p>;

  return (
    <div className="mx-auto max-w-4xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href={`/livros/${livro.id}`} className="hover:text-verde-neon">← Voltar ao detalhe</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Editando: {livro.volume}</h1>
      <LivroForm modo="editar" inicial={valoresDoLivro(livro)} livroId={livro.id} />
    </div>
  );
}