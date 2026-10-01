"use client";
import { useCallback, useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import { dataBR } from "../../lib/datas";
import type { Autor } from "../../types/autor";

export default function Autores() {
  const [autores, setAutores] = useState<Autor[]>([]);
  const [busca, setBusca] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      setAutores(await api<Autor[]>("/autores"));
      setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar os autores");
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  const filtrados = useMemo(() => {
    const q = busca.trim().toLowerCase();
    if (!q) return autores;
    return autores.filter((a) => a.nome.toLowerCase().includes(q) || a.nacionalidade?.toLowerCase().includes(q));
  }, [autores, busca]);

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 rounded-lg bg-marrom/70 p-6">
        <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Os autores</h1>
        <p className="mt-1 text-lg italic text-verde-neon/90">
          {carregando ? "Consultando os escribas..." : `${autores.length} autor(es) registrado(s).`}
        </p>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      <input
        value={busca} onChange={(e) => setBusca(e.target.value)}
        placeholder="Buscar por nome ou nacionalidade" aria-label="Buscar autores"
        className="mb-6 w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
      />

      {!carregando && filtrados.length === 0 && (
        <p className="text-lg">{autores.length === 0 ? "Nenhum autor cadastrado ainda." : "Nenhum autor encontrado para essa busca."}</p>
      )}

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {filtrados.map((a) => (
          <article key={a.id} className="cartao borda-neon flex items-center gap-4 rounded-lg bg-marrom/70 p-4">
            <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full border border-verde-neon bg-musgo font-gotica text-2xl neon-verde">
              {a.nome[0]?.toUpperCase()}
            </div>
            <div className="min-w-0">
              <h3 className="font-gotica text-2xl leading-tight neon-ouro">{a.nome}</h3>
              <p className="text-lg">{a.nacionalidade}</p>
              <p className="text-sm text-ouro">Nasceu em {dataBR(a.dataNascimento)}</p>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
