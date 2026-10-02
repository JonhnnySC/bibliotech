"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
// Corrija os caminhos aqui:
import { api } from "../../lib/api";
import type { Autor } from "../../types/autor";

// ... resto do seu código

export default function AutoresPage() {
  const [autores, setAutores] = useState<Autor[]>([]);
  const [busca, setBusca] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let vivo = true;
    api<Autor[]>("/autores")
      .then((d) => { if (vivo) { setAutores(d); setErro(""); } })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar autores"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, []);

  const filtrados = autores.filter((a) => {
    const q = busca.trim().toLowerCase();
    if (!q) return true;
    return a.nome.toLowerCase().includes(q) || a.nacionalidade?.toLowerCase().includes(q);
  });

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Autores</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Carregando..." : `${autores.length} autor(es) no acervo.`}
          </p>
        </div>
        <Link href="/autores/novo" className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">
          Novo autor
        </Link>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      <input
        value={busca} onChange={(e) => setBusca(e.target.value)}
        placeholder="Buscar por nome ou nacionalidade"
        className="mb-6 w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
      />

      {!carregando && filtrados.length === 0 && (
        <p className="text-lg">
          {autores.length === 0 ? "Nenhum autor cadastrado ainda." : "Nenhum autor encontrado para essa busca."}
        </p>
      )}

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
        {filtrados.map((a) => (
          <Link key={a.id} href={`/autores/${a.id}`}
            className="cartao borda-neon overflow-hidden rounded-lg bg-marrom/70 text-left hover:scale-[1.02] transition-transform">
            <div className="flex aspect-square items-center justify-center bg-noite/70">
              {a.fotoUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={a.fotoUrl} alt={a.nome} className="h-full w-full object-cover" />
              ) : (
                <div className="flex h-full w-full items-center justify-center text-6xl text-ouro/30">
                  {a.nome.charAt(0)}
                </div>
              )}
            </div>
            <div className="p-3">
              <h3 className="line-clamp-2 text-lg leading-tight">{a.nome}</h3>
              {a.nacionalidade && <p className="text-sm text-ouro">{a.nacionalidade}</p>}
             <p className="text-xs text-ouro/60">{(a.livros ?? []).length} livro(s)</p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}