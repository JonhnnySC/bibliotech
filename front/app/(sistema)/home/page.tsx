"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import Capa from "../../components/Capa";
import { api } from "../../lib/api";
import type { Emprestimo, Livro as LivroT, Usuario } from "../../types/usuario";

// Formata data para DD/MM/AAAA — protege contra null/undefined
const dataBR = (iso: string | null | undefined) => {
  if (!iso) return "—";
  return iso.split("-").reverse().join("/");
};

// Extrai apenas o ano da data de lançamento
const anoLancamento = (iso: string | null | undefined) => {
  if (!iso) return "";
  return iso.split("-")[0];
};

// Dias até a devolução (negativo = atrasado) — protege contra null/undefined
function diasRestantes(iso: string | null | undefined) {
  if (!iso) return 0;
  const [a, m, d] = iso.split("-").map(Number);
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  return Math.round((new Date(a, m - 1, d).getTime() - hoje.getTime()) / 86400000);
}

// helpers puros (fora do componente) -> não setam estado
const buscarTudo = () =>
  Promise.all([
    api<LivroT[]>("/livros"),
    api<Usuario[]>("/usuarios"),
    api<Emprestimo[]>("/emprestimos/ativos"),
  ]);

export default function Home() {
  const [livros, setLivros] = useState<LivroT[]>([]);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [ativos, setAtivos] = useState<Emprestimo[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [email, setEmail] = useState("");

  // fetch inicial no padrão sem setState síncrono no effect (callbacks .then)
  useEffect(() => {
    setEmail(localStorage.getItem("email") ?? "");
    let vivo = true;
    buscarTudo()
      .then(([l, u, e]) => {
        if (!vivo) return;
        setLivros(l); setUsuarios(u); setAtivos(e); setErro("");
      })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, []);

  // usado pelos handlers (devolver) -> setState em handler é sempre ok
  async function recarregar() {
    try {
      const [l, u, e] = await buscarTudo();
      setLivros(l); setUsuarios(u); setAtivos(e); setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar");
    } finally {
      setCarregando(false);
    }
  }

  async function devolver(id: number) {
    if (!confirm("Confirmar a devolução deste exemplar?")) return;
    try {
      await api(`/emprestimos/${id}/devolver`, { method: "PATCH" });
      recarregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao devolver");
    }
  }

  // lê o livro aninhado que o backend manda (exemplar.livro.volume), com fallback
  const tituloLivro = (e: Emprestimo) =>
    e.exemplar.livro?.volume ??
    livros.find((l) => l.id === e.exemplar.livroId)?.volume ??
    `Livro #${e.exemplar.livro?.id ?? e.exemplar.livroId ?? "?"}`;

  const atrasados = ativos.filter((e) =>
    e.statusEmprestimo === "ATRASADO" || diasRestantes(e.dataPrevistaDevolucao) < 0
  ).length;
  const leitores = usuarios.filter((u) => u.perfil === "LEITOR" && u.status === "ATIVO").length;
  const nome = email.split("@")[0];

  const cards = [
    { titulo: "Livros no acervo", valor: livros.length, icone: "📖", cor: "neon-ouro" },
    { titulo: "Leitores ativos", valor: leitores, icone: "👥", cor: "neon-verde" },
    { titulo: "Empréstimos ativos", valor: ativos.length, icone: "📜", cor: "neon-ouro" },
    { titulo: "Atrasados", valor: atrasados, icone: "⏳", cor: atrasados > 0 ? "text-red-400" : "neon-verde" },
  ];

  return (
    <div className="mx-auto max-w-6xl">
      {/* Boas-vindas */}
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Salve, {nome || "viajante"}</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {atrasados > 0 ? `${atrasados} livro(s) passaram do prazo de devolução.` : "Todos os livros estão em suas órbitas."}
          </p>
        </div>
        <div className="flex gap-3">
          <Link href="/usuarios" className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">Gerenciar Usuários</Link>
          <Link href="/emprestimos" className="rounded-full border border-verde-neon/60 px-5 py-2 text-lg text-verde-neon hover:bg-musgo">Novo Empréstimo</Link>
        </div>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      {/* Números */}
      <section className="mb-10 grid grid-cols-2 gap-4 lg:grid-cols-4">
        {cards.map((c) => (
          <div
            key={c.titulo}
            className={`borda-neon rounded-lg bg-marrom/70 p-5 text-center transition-all ${
              c.valor > 0 && c.titulo === "Atrasados" ? "animate-pulse border-red-400/50" : ""
            }`}
          >
            <div className="text-3xl">{c.icone}</div>
            <div className={`font-gotica text-5xl ${c.cor}`}>{carregando ? "…" : c.valor}</div>
            <div className="text-ouro-neon">{c.titulo}</div>
          </div>
        ))}
      </section>

      {/* Empréstimos */}
      <section className="mb-10">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="font-gotica text-3xl neon-ouro">Empréstimos em aberto</h2>
          <Link href="/emprestimos" className="text-sm text-verde-neon hover:underline">Ver todos →</Link>
        </div>
        <div className="overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
          <table className="w-full text-left">
            <thead className="bg-musgo text-ouro-neon">
              <tr>
                <th className="p-3">Livro</th><th className="p-3">Leitor</th>
                <th className="p-3">Devolver até</th><th className="p-3">Prazo</th><th className="p-3" />
              </tr>
            </thead>
            <tbody>
              {!carregando && ativos.length === 0 && (
                <tr><td colSpan={5} className="p-6 text-center text-lg">Nenhum empréstimo em aberto. Os livros estão todos nas estantes.</td></tr>
              )}
              {ativos.slice(0, 5).map((e) => {
                const dias = diasRestantes(e.dataPrevistaDevolucao);
                return (
                  <tr key={e.id} className="border-t border-marrom-claro hover:bg-noite/30 transition-colors">
                    <td className="p-3">{tituloLivro(e)} <span className="text-sm text-ouro">(ex. #{e.exemplar.id})</span></td>
                    <td className="p-3">{e.leitor.nome}</td>
                    <td className="p-3">{dataBR(e.dataPrevistaDevolucao)}</td>
                    <td className={`p-3 ${dias < 0 ? "text-red-400" : dias <= 3 ? "neon-ouro" : "neon-verde"}`}>
                      {dias < 0 ? `${-dias} dia(s) de atraso` : dias === 0 ? "Vence hoje" : `${dias} dia(s)`}
                    </td>
                    <td className="p-3 text-right">
                      <button onClick={() => devolver(e.id)} className="rounded border border-verde-neon px-3 py-1 text-verde-neon hover:bg-verde-neon hover:text-noite">
                        Devolver
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </section>

      {/* Acervo */}
      <section>
        <h2 className="font-gotica mb-3 text-3xl neon-ouro">Do acervo</h2>
        {!carregando && livros.length === 0 && <p className="text-lg">Nenhum livro cadastrado ainda.</p>}
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
          {livros.slice(0, 6).map((l) => (
            <article
              key={l.id}
              className="borda-neon overflow-hidden rounded-lg bg-marrom/70 transition-transform hover:scale-105"
            >
              <Capa isbn={l.isbn} url={l.capaUrl} titulo={l.volume} className="aspect-[2/3]" />
              <div className="p-2">
                <h3 className="line-clamp-2 text-base leading-tight">{l.volume}</h3>
                <p className="text-sm text-ouro">{l.paginas} págs.</p>
                {l.dataLancamento && <p className="text-xs text-ouro/60">{anoLancamento(l.dataLancamento)}</p>}
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}