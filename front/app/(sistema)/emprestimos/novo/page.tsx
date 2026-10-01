"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { api } from "../../../lib/api";
import type { Exemplar, Livro as LivroT, Usuario } from "../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

/**
 * Rota /emprestimos/novo — registrar saída.
 * Fluxo cascata: Livro -> Exemplares DISPONÍVEIS -> Leitor ativo.
 */
export default function NovoEmprestimoPage() {
  const router = useRouter();
  const [livros, setLivros] = useState<LivroT[]>([]);
  const [leitores, setLeitores] = useState<Usuario[]>([]);
  const [livroId, setLivroId] = useState("");
  const [disponiveis, setDisponiveis] = useState<Exemplar[]>([]);
  const [carregandoEx, setCarregandoEx] = useState(false);
  const [exemplarId, setExemplarId] = useState("");
  const [leitorId, setLeitorId] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [erroForm, setErroForm] = useState("");

  // Popula os dois selects fixos (livros e leitores ativos)
  useEffect(() => {
    let vivo = true;
    Promise.all([api<LivroT[]>("/livros"), api<Usuario[]>("/usuarios")])
      .then(([ls, us]) => {
        if (!vivo) return;
        setLivros(ls);
        setLeitores(us.filter((u) => u.perfil === "LEITOR" && u.status === "ATIVO"));
      })
      .catch(() => { if (vivo) setErroForm("Erro ao carregar livros/leitores"); });
    return () => { vivo = false; };
  }, []);

  /** Ao trocar o livro, busca só os exemplares DISPONÍVEIS dele. */
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

  /** POST /emprestimos -> volta para a lista. */
  async function registrar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    setSalvando(true);
    try {
      await api("/emprestimos", {
        method: "POST",
        body: JSON.stringify({ leitorId: Number(leitorId), exemplarId: Number(exemplarId) }),
      });
      router.push("/emprestimos");
    } catch (err) {
      setErroForm(err instanceof Error ? err.message : "Falha ao registrar o empréstimo");
      setSalvando(false);
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/emprestimos" className="hover:text-verde-neon">← Voltar aos empréstimos</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Novo empréstimo</h1>

      <form onSubmit={registrar} className="borda-neon rounded-lg bg-marrom/70 p-6">
        <label className="mb-1 block text-ouro-neon">Livro</label>
        <select required value={livroId} onChange={(e) => escolherLivro(e.target.value)} className={campo}>
          <option value="">Escolha um livro</option>
          {livros.map((l) => <option key={l.id} value={l.id}>{l.volume}</option>)}
        </select>

        <label className="mb-1 block text-ouro-neon">Exemplar</label>
        <select required value={exemplarId} onChange={(e) => setExemplarId(e.target.value)}
          disabled={!livroId || carregandoEx} className={campo + " disabled:opacity-50"}>
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
          <button type="button" onClick={() => router.back()}
            className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
            Cancelar
          </button>
          <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
            {salvando ? "Registrando..." : "Registrar saída"}
          </button>
        </div>
      </form>
    </div>
  );
}