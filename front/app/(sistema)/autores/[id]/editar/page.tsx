"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import AutorForm from "../../components/AutorForm";
import { api } from "../../../../lib/api";
import type { Autor, AutorForm as AutorFormType } from "../../../../types/autor";

export default function EditarAutorPage() {
  const params = useParams();
  const id = Number(params?.id);

  const [autor, setAutor] = useState<Autor | null>(null);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<Autor>(`/autores/${id}`)
      .then((d) => {
        if (vivo) setAutor(d);
      })
      .catch((e) => {
        if (vivo)
          setErro(e instanceof Error ? e.message : 'Erro ao carregar autor');
      })
      .finally(() => {
        if (vivo) setCarregando(false);
      });
    return () => {
      vivo = false;
    };
  }, [id]);

  if (carregando) return <p className="text-lg">Carregando...</p>;
  if (erro || !autor)
    return (
      <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">
        {erro || 'Autor não encontrado'}
      </p>
    );

  const inicial: AutorFormType = {
    nome: autor.nome,
    nacionalidade: autor.nacionalidade ?? '',
    dataNascimento: autor.dataNascimento ?? '',
    fotoUrl: autor.fotoUrl ?? '',
    livroIds: autor.livros.map((l) => l.id),
  };

  return (
    <div className="mx-auto max-w-4xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href={`/autores/${autor.id}`} className="hover:text-verde-neon">
          ← Voltar ao detalhe
        </Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">
        Editando: {autor.nome}
      </h1>
      <AutorForm modo="editar" inicial={inicial} autorId={autor.id} />
    </div>
  );
}
