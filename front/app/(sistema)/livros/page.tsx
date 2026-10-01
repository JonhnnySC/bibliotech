"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import Capa from "../../components/Capa";
import { api } from "../../lib/api";
import type { Livro as LivroT } from "../../types/usuario";

const anoLancamento = (iso?: string | null) => (iso ? iso.split("-")[0] : "");

export default function LivrosPage() {
  const [livros, setLivros] = useState<LivroT[]>([]);
  const [busca, setBusca] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<LivroT[]>("/livros")
      .then((d) => { if (vivo) { setLivros(d); setErro(""); } })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar os livros"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, []);

  const filtrados = livros.filter((l) => {
    const q = busca.trim().toLowerCase();
    if (!q) return true;
    return l.volume.toLowerCase().includes(q) || l.isbn?.toLowerCase().includes(q);
  });

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">O acervo</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Abrindo as estantes..." : `${livros.length} livro(s) guardado(s) no grimório.`}
          </p>
        </div>
        <Link href="/livros/novo" className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">
          Novo livro
        </Link>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      <input
        value={busca} onChange={(e) => setBusca(e.target.value)}
        placeholder="Buscar por título ou ISBN" aria-label="Buscar livros"
        className="mb-6 w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
      />

      {!carregando && filtrados.length === 0 && (
        <p className="text-lg">
          {livros.length === 0 ? "Nenhum livro cadastrado ainda. Use “Novo livro” para começar o acervo." : "Nenhum livro encontrado para essa busca."}
        </p>
      )}

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
        {filtrados.map((l) => (
          <Link key={l.id} href={`/livros/${l.id}`}
            className="cartao borda-neon overflow-hidden rounded-lg bg-marrom/70 text-left focus-visible:outline-2 focus-visible:outline-verde-neon hover:scale-[1.02] transition-transform">
            <Capa isbn={l.isbn} url={l.capaUrl} titulo={l.volume} className="aspect-[2/3]" />
            <div className="p-3">
              <h3 className="line-clamp-2 text-lg leading-tight">{l.volume}</h3>
              <p className="text-sm text-ouro">{l.tipo ?? "Físico"} · {l.paginas} págs.</p>
              {l.dataLancamento && <p className="text-xs text-ouro/60">{anoLancamento(l.dataLancamento)}</p>}
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}