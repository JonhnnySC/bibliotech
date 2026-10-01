"use client";
import { useCallback, useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import { dataBR, diasRestantes } from "../../lib/datas";
import type { Emprestimo, Exemplar, Livro as LivroT, Usuario } from "../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export default function Emprestimos() {
  const [ativos, setAtivos] = useState<Emprestimo[]>([]);
  const [livros, setLivros] = useState<LivroT[]>([]);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [busca, setBusca] = useState("");
  const [soAtrasados, setSoAtrasados] = useState(false);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  // novo empréstimo
  const [novo, setNovo] = useState(false);
  const [livroId, setLivroId] = useState("");
  const [disponiveis, setDisponiveis] = useState<Exemplar[]>([]);
  const [carregandoEx, setCarregandoEx] = useState(false);
  const [exemplarId, setExemplarId] = useState("");
  const [leitorId, setLeitorId] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [erroForm, setErroForm] = useState("");

  const carregar = useCallback(async () => {
    try {
      const [e, l, u] = await Promise.all([
        api<Emprestimo[]>("/emprestimos/ativos"),
        api<LivroT[]>("/livros"),
        api<Usuario[]>("/usuarios"),
      ]);
      setAtivos(e); setLivros(l); setUsuarios(u); setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar os empréstimos");
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  const tituloLivro = (id: number) => livros.find((l) => l.id === id)?.volume ?? `Livro #${id}`;
  const leitores = usuarios.filter((u) => u.perfil === "LEITOR" && u.status === "ATIVO");
  const estaAtrasado = (e: Emprestimo) => e.statusEmprestimo === "ATRASADO" || diasRestantes(e.dataPrevistaDevolucao) < 0;
  const atrasados = ativos.filter(estaAtrasado).length;

  const filtrados = useMemo(() => {
    const q = busca.trim().toLowerCase();
    return ativos.filter((e) => {
      if (soAtrasados && !estaAtrasado(e)) return false;
      if (!q) return true;
      return e.leitor.nome.toLowerCase().includes(q) || tituloLivro(e.exemplar.livroId).toLowerCase().includes(q);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ativos, livros, busca, soAtrasados]);

  async function devolver(id: number) {
    try {
      await api(`/emprestimos/${id}/devolver`, { method: "PATCH" });
      carregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao devolver");
    }
  }

  function abrirNovo() {
    setNovo(true); setErroForm("");
    setLivroId(""); setExemplarId(""); setLeitorId(""); setDisponiveis([]);
  }

  async function escolherLivro(id: string) {
    setLivroId(id); setExemplarId(""); setDisponiveis([]); setErroForm("");
    if (!id) return;
    setCarregandoEx(true);
    try {
      const todos = await api<Exemplar[]>(`/exemplares/livro/${id}`);
      setDisponiveis(todos.filter((x) => x.statusExemplar === "DISPONIVEL"));
    } catch (e) {
      setErroForm(e instanceof Error ? e.message : "Erro ao buscar exemplares");
    } finally {
      setCarregandoEx(false);
    }
  }

  async function emprestar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    setSalvando(true);
    try {
      await api("/emprestimos", {
        method: "POST",
        body: JSON.stringify({ leitorId: Number(leitorId), exemplarId: Number(exemplarId) }),
      });
      setNovo(false);
      carregar();
    } catch (err) {
      setErroForm(err instanceof Error ? err.message : "Falha ao registrar o empréstimo");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="mx-auto max-w-6xl">
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">Empréstimos</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Folheando o caderno do guardião..." : `${ativos.length} em aberto, ${atrasados} atrasado(s).`}
          </p>
        </div>
        <button onClick={abrirNovo} className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">Novo empréstimo</button>
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

      <div className="overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
        <table className="w-full text-left">
          <thead className="bg-musgo text-ouro-neon">
            <tr>
              <th className="p-3">Livro</th><th className="p-3">Leitor</th><th className="p-3">Saída</th>
              <th className="p-3">Devolver até</th><th className="p-3">Prazo</th><th className="p-3" />
            </tr>
          </thead>
          <tbody>
            {!carregando && filtrados.length === 0 && (
              <tr><td colSpan={6} className="p-6 text-center text-lg">
                {ativos.length === 0 ? "Nenhum empréstimo em aberto. Use “Novo empréstimo” para registrar uma saída." : "Nenhum empréstimo encontrado para esse filtro."}
              </td></tr>
            )}
            {filtrados.map((e) => {
              const dias = diasRestantes(e.dataPrevistaDevolucao);
              return (
                <tr key={e.id} className="border-t border-marrom-claro">
                  <td className="p-3">{tituloLivro(e.exemplar.livroId)} <span className="text-sm text-ouro">(ex. #{e.exemplar.id})</span></td>
                  <td className="p-3">{e.leitor.nome}</td>
                  <td className="p-3">{dataBR(e.dataEmprestimo)}</td>
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

      {novo && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-noite/85 p-4" onClick={() => setNovo(false)}>
          <form onSubmit={emprestar} onClick={(e) => e.stopPropagation()}
            className="borda-neon max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-lg bg-marrom p-6">
            <h2 className="font-gotica mb-4 text-3xl neon-ouro">Novo empréstimo</h2>

            <label className="mb-1 block text-ouro-neon">Livro</label>
            <select required value={livroId} onChange={(e) => escolherLivro(e.target.value)} className={campo}>
              <option value="">Escolha um livro</option>
              {livros.map((l) => <option key={l.id} value={l.id}>{l.volume}</option>)}
            </select>

            <label className="mb-1 block text-ouro-neon">Exemplar</label>
            <select required value={exemplarId} onChange={(e) => setExemplarId(e.target.value)} disabled={!livroId || carregandoEx} className={campo + " disabled:opacity-50"}>
              <option value="">
                {!livroId ? "Escolha o livro primeiro" : carregandoEx ? "Buscando..." : disponiveis.length === 0 ? "Nenhum exemplar disponível" : "Escolha um exemplar"}
              </option>
              {disponiveis.map((x) => (
                <option key={x.id} value={x.id}>Exemplar #{x.id} · {x.capaDura ? "capa dura" : "brochura"}</option>
              ))}
            </select>

            <label className="mb-1 block text-ouro-neon">Leitor</label>
            <select required value={leitorId} onChange={(e) => setLeitorId(e.target.value)} className={campo}>
              <option value="">{leitores.length === 0 ? "Nenhum leitor ativo" : "Escolha um leitor"}</option>
              {leitores.map((u) => <option key={u.id} value={u.id}>{u.nome} ({u.email})</option>)}
            </select>

            <p className="mb-3 text-sm text-ouro">O prazo de devolução é de 14 dias a partir de hoje.</p>
            {erroForm && <p className="mb-3 text-red-400">{erroForm}</p>}

            <div className="flex justify-end gap-3">
              <button type="button" onClick={() => setNovo(false)} className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">Cancelar</button>
              <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
                {salvando ? "Registrando..." : "Registrar saída"}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
