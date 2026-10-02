"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { api } from "../../../lib/api";
import type { Autor } from "../../../types/autor";

export default function DetalheAutorPage() {
  const params = useParams();
  const router = useRouter();
  const id = Number(params?.id);

  const [autor, setAutor] = useState<Autor | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<Autor>(`/autores/${id}`)
      .then((d) => { if (vivo) setAutor(d); })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar autor"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, [id]);

  async function excluir() {
    if (!autor || !confirm(`Remover "${autor.nome}" do acervo?`)) return;
    try {
      await api(`/autores/${autor.id}`, { method: "DELETE" });
      router.push("/autores");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao remover autor");
    }
  }

  if (carregando) return <p className="text-lg">Carregando...</p>;
  if (erro || !autor) return <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro || "Autor não encontrado"}</p>;

  // fallback: se a API não mandar "livros", vira lista vazia
  const livros = autor.livros ?? [];

  return (
    <div className="mx-auto max-w-5xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/autores" className="hover:text-verde-neon">← Voltar aos autores</Link>
      </nav>

      <div className="borda-neon mb-8 rounded-lg bg-marrom/70 p-6">
        <div className="flex flex-col gap-6 sm:flex-row">
          <div className="flex aspect-square w-48 shrink-0 items-center justify-center overflow-hidden rounded-lg bg-noite/70">
            {autor.fotoUrl ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img src={autor.fotoUrl} alt={autor.nome} className="h-full w-full object-cover" />
            ) : (
              <div className="text-8xl text-ouro/30">{autor.nome.charAt(0)}</div>
            )}
          </div>

          <div className="flex-1">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <h1 className="font-gotica text-3xl neon-ouro sm:text-4xl">{autor.nome}</h1>
              <div className="flex gap-2">
                <Link href={`/autores/${autor.id}/editar`}
                  className="rounded border border-verde-neon px-3 py-1 text-verde-neon hover:bg-verde-neon hover:text-noite">
                  ✏️ Editar
                </Link>
                <button onClick={excluir}
                  className="rounded border border-red-400/70 px-3 py-1 text-red-300 hover:bg-red-950/50">
                  Remover
                </button>
              </div>
            </div>

            <dl className="mt-3 grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-lg">
              <dt className="text-ouro">Nacionalidade</dt><dd>{autor.nacionalidade ?? "—"}</dd>
              <dt className="text-ouro">Nascimento</dt><dd>{autor.dataNascimento ?? "—"}</dd>
            </dl>
          </div>
        </div>
      </div>

      <section>
        <h2 className="font-gotica mb-4 text-2xl neon-verde">
          Livros ({livros.length})
        </h2>
        {livros.length === 0 ? (
          <p className="text-lg text-ouro/60">Nenhum livro vinculado a este autor.</p>
        ) : (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
            {livros.map((livro) => (
              <Link key={livro.id} href={`/livros/${livro.id}`}
                className="borda-neon rounded-lg bg-marrom/70 p-4 hover:scale-[1.02] transition-transform">
                <h3 className="line-clamp-2 text-lg leading-tight">{livro.volume}</h3>
                {livro.isbn && <p className="text-sm text-ouro/60">ISBN: {livro.isbn}</p>}
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}