"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import EmprestimosTable from "./components/EmprestimosTable";
import { api } from "../../lib/api";
import { diasRestantes } from "../../lib/datas";
import type { Emprestimo } from "../../types/usuario";

/**
 * Rota /emprestimos — lista de empréstimos ativos + filtros.
 * O cadastro agora mora em /emprestimos/novo e a tabela em components/.
 */
export default function EmprestimosPage() {
  const [ativos, setAtivos] = useState<Emprestimo[]>([]);
  const [busca, setBusca] = useState("");
  const [soAtrasados, setSoAtrasados] = useState(false);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  // Busca inicial dos empréstimos ativos
  useEffect(() => {
    let vivo = true;
    api<Emprestimo[]>("/emprestimos/ativos")
      .then((d) => { if (vivo) { setAtivos(d); setErro(""); } })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar empréstimos"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, []);

  /** Recarga usada depois de uma devolução. */
  async function recarregar() {
    try {
      setAtivos(await api<Emprestimo[]>("/emprestimos/ativos"));
      setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar empréstimos");
    } finally {
      setCarregando(false);
    }
  }

  /** Devolução: PATCH no backend + recarga da lista. */
  async function devolver(id: number) {
    if (!confirm("Confirmar a devolução deste exemplar?")) return;
    try {
      await api(`/emprestimos/${id}/devolver`, { method: "PATCH" });
      recarregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao devolver");
    }
  }

  // Atrasado = status marcado OU prazo estourado
  const estaAtrasado = (e: Emprestimo) =>
    e.statusEmprestimo === "ATRASADO" || diasRestantes(e.dataPrevistaDevolucao) < 0;

  // Filtros de busca (leitor ou título) + checkbox "só atrasados"
  const filtrados = ativos.filter((e) => {
    if (soAtrasados && !estaAtrasado(e)) return false;
    const q = busca.trim().toLowerCase();
    if (!q) return true;
    const livro = e.exemplar.livro?.volume.toLowerCase() ?? "";
    return e.leitor.nome.toLowerCase().includes(q) || livro.includes(q);
  });

  const atrasados = ativos.filter(estaAtrasado).length;

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Empréstimos</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Folheando o caderno do guardião..." : `${ativos.length} em aberto, ${atrasados} atrasado(s).`}
          </p>
        </div>
        {/* Cadastro virou ROTA, não modal */}
        <Link href="/emprestimos/novo" className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">
          Novo empréstimo
        </Link>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      <div className="mb-6 flex flex-wrap items-center gap-4">
        <input
          value={busca} onChange={(e) => setBusca(e.target.value)}
          placeholder="Buscar por livro ou leitor" aria-label="Buscar empréstimos"
          className="w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
        />
        <label className="flex items-center gap-2 text-lg">
          <input type="checkbox" checked={soAtrasados} onChange={(e) => setSoAtrasados(e.target.checked)} /> Só atrasados
        </label>
      </div>

      <EmprestimosTable emprestimos={filtrados} carregando={carregando} onDevolver={devolver} />
    </div>
  );
}