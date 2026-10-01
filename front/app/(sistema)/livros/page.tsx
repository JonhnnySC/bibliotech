"use client";
import { useCallback, useEffect, useMemo, useState } from "react";
import Livro from "../../components/Livro";
import { api } from "../../lib/api";
import type { Exemplar, Livro as LivroT, StatusExemplar } from "../../types/usuario";

const dataBR = (iso?: string) => (iso ? iso.split("-").reverse().join("/") : "—");
const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";
const vazio = { volume: "", isbn: "", descricao: "", edicao: "", tipo: "Físico", paginas: "", dataLancamento: "", capaUrl: "" };

const corStatus: Record<StatusExemplar, string> = {
  DISPONIVEL: "neon-verde",
  EMPRESTADO: "neon-ouro",
  DANIFICADO: "text-red-400",
  PERDIDO: "text-red-400",
};

// capa com fallback para o ícone quando a URL é nula ou quebrada
function Capa({ url, titulo, className = "" }: { url?: string | null; titulo: string; className?: string }) {
  const [falhou, setFalhou] = useState(false);
  return (
    <div className={`flex items-center justify-center bg-noite/70 ${className}`}>
      {url && !falhou
        // eslint-disable-next-line @next/next/no-img-element
        ? <img src={url} alt={titulo} onError={() => setFalhou(true)} className="h-full w-full object-cover" />
        : <Livro className="h-14 w-14 opacity-70" />}
    </div>
  );
}

export default function Livros() {
  const [livros, setLivros] = useState<LivroT[]>([]);
  const [busca, setBusca] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  // cadastro
  const [novo, setNovo] = useState(false);
  const [f, setF] = useState(vazio);
  const [salvando, setSalvando] = useState(false);
  const [erroForm, setErroForm] = useState("");

  // detalhe + exemplares
  const [aberto, setAberto] = useState<LivroT | null>(null);
  const [exemplares, setExemplares] = useState<Exemplar[]>([]);
  const [carregandoEx, setCarregandoEx] = useState(false);
  const [ex, setEx] = useState({ dataImpressao: "", dataCompra: "", capaDura: false });
  const [erroEx, setErroEx] = useState("");

  const carregar = useCallback(async () => {
    try {
      setLivros(await api<LivroT[]>("/livros"));
      setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar os livros");
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  const filtrados = useMemo(() => {
    const q = busca.trim().toLowerCase();
    if (!q) return livros;
    return livros.filter((l) => l.volume.toLowerCase().includes(q) || l.isbn?.toLowerCase().includes(q));
  }, [livros, busca]);

  const mudar = (k: keyof typeof f) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
    setF({ ...f, [k]: e.target.value });

  async function cadastrar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    const paginas = Number(f.paginas);
    if (!Number.isInteger(paginas) || paginas <= 0) return setErroForm("Informe um número de páginas válido.");
    setSalvando(true);
    try {
      await api<LivroT>("/livros", {
        method: "POST",
        body: JSON.stringify({
          volume: f.volume, isbn: f.isbn, descricao: f.descricao, edicao: f.edicao, tipo: f.tipo,
          paginas, dataLancamento: f.dataLancamento || null, capaUrl: f.capaUrl || null,
        }),
      });
      setF(vazio);
      setNovo(false);
      carregar();
    } catch (err) {
      setErroForm(err instanceof Error ? err.message : "Falha ao cadastrar");
    } finally {
      setSalvando(false);
    }
  }

  async function carregarExemplares(id: number) {
    setCarregandoEx(true);
    try {
      setExemplares(await api<Exemplar[]>(`/exemplares/livro/${id}`));
      setErroEx("");
    } catch (err) {
      setErroEx(err instanceof Error ? err.message : "Erro ao carregar exemplares");
    } finally {
      setCarregandoEx(false);
    }
  }

  function abrir(l: LivroT) {
    setAberto(l);
    setExemplares([]);
    setEx({ dataImpressao: "", dataCompra: "", capaDura: false });
    carregarExemplares(l.id);
  }

  async function addExemplar(e: React.FormEvent) {
    e.preventDefault();
    if (!aberto) return;
    setErroEx("");
    try {
      await api("/exemplares", {
        method: "POST",
        body: JSON.stringify({
          livroId: aberto.id,
          dataImpressao: ex.dataImpressao || null,
          dataCompra: ex.dataCompra || null,
          capaDura: ex.capaDura,
        }),
      });
      setEx({ dataImpressao: "", dataCompra: "", capaDura: false });
      carregarExemplares(aberto.id);
    } catch (err) {
      setErroEx(err instanceof Error ? err.message : "Falha ao adicionar exemplar");
    }
  }

  async function mudarStatus(id: number, status: StatusExemplar) {
    if (!aberto) return;
    try {
      await api(`/exemplares/${id}/status?statusExemplar=${status}`, { method: "PATCH" });
      carregarExemplares(aberto.id);
    } catch (err) {
      setErroEx(err instanceof Error ? err.message : "Falha ao mudar o status");
    }
  }

  async function excluir(l: LivroT) {
    if (!confirm(`Remover "${l.volume}" do acervo?`)) return;
    try {
      await api(`/livros/${l.id}`, { method: "DELETE" });
      setAberto(null);
      carregar();
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Erro ao remover o livro");
      setAberto(null);
    }
  }

  const disponiveis = exemplares.filter((x) => x.statusExemplar === "DISPONIVEL").length;

  return (
    <div className="mx-auto max-w-6xl">
      {/* Cabeçalho */}
      <section className="borda-neon mb-8 flex flex-wrap items-center justify-between gap-4 rounded-lg bg-marrom/70 p-6">
        <div>
          <h1 className="font-gotica text-4xl neon-ouro sm:text-5xl">O acervo</h1>
          <p className="mt-1 text-lg italic text-verde-neon/90">
            {carregando ? "Abrindo as estantes..." : `${livros.length} livro(s) guardado(s) no grimório.`}
          </p>
        </div>
        <button onClick={() => { setNovo(true); setErroForm(""); }} className="btn-neon font-gotica rounded-full px-5 py-2 text-xl">
          Novo livro
        </button>
      </section>

      {erro && <p className="mb-4 rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro}</p>}

      {/* Busca */}
      <input
        value={busca} onChange={(e) => setBusca(e.target.value)}
        placeholder="Buscar por título ou ISBN"
        aria-label="Buscar livros"
        className="mb-6 w-full max-w-md rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]"
      />

      {!carregando && filtrados.length === 0 && (
        <p className="text-lg">
          {livros.length === 0 ? "Nenhum livro cadastrado ainda. Use “Novo livro” para começar o acervo." : "Nenhum livro encontrado para essa busca."}
        </p>
      )}

      {/* Grade */}
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
        {filtrados.map((l) => (
          <button key={l.id} onClick={() => abrir(l)}
            className="cartao borda-neon overflow-hidden rounded-lg bg-marrom/70 text-left focus-visible:outline-2 focus-visible:outline-verde-neon">
            <Capa url={l.capaUrl} titulo={l.volume} className="aspect-[2/3]" />
            <div className="p-3">
              <h3 className="line-clamp-2 text-lg leading-tight">{l.volume}</h3>
              <p className="text-sm text-ouro">{l.tipo ?? "Físico"} · {l.paginas} págs.</p>
            </div>
          </button>
        ))}
      </div>

      {/* Modal: detalhe do livro */}
      {aberto && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-noite/85 p-4" onClick={() => setAberto(null)}>
          <div role="dialog" aria-modal="true" aria-label={aberto.volume} onClick={(e) => e.stopPropagation()}
            className="borda-neon max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-lg bg-marrom p-6">
            <div className="flex flex-col gap-5 sm:flex-row">
              <Capa url={aberto.capaUrl} titulo={aberto.volume} className="aspect-[2/3] w-40 shrink-0 self-center rounded sm:self-start" />
              <div className="flex-1">
                <h2 className="font-gotica text-3xl neon-ouro">{aberto.volume}</h2>
                <dl className="mt-2 grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-lg">
                  <dt className="text-ouro">ISBN</dt><dd>{aberto.isbn}</dd>
                  <dt className="text-ouro">Edição</dt><dd>{aberto.edicao ?? "—"}</dd>
                  <dt className="text-ouro">Tipo</dt><dd>{aberto.tipo ?? "—"}</dd>
                  <dt className="text-ouro">Páginas</dt><dd>{aberto.paginas}</dd>
                  <dt className="text-ouro">Lançamento</dt><dd>{dataBR(aberto.dataLancamento)}</dd>
                </dl>
                {aberto.descricao && <p className="mt-3 text-lg leading-snug">{aberto.descricao}</p>}
              </div>
            </div>

            {/* Exemplares */}
            <h3 className="font-gotica mb-2 mt-6 text-2xl neon-verde">
              Exemplares {!carregandoEx && exemplares.length > 0 && <span className="text-lg text-ouro-neon">({disponiveis} disponível(is))</span>}
            </h3>
            {erroEx && <p className="mb-3 text-red-400">{erroEx}</p>}
            <div className="mb-4 overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
              <table className="w-full text-left">
                <thead className="bg-musgo text-ouro-neon">
                  <tr><th className="p-2">Nº</th><th className="p-2">Capa</th><th className="p-2">Compra</th><th className="p-2">Status</th></tr>
                </thead>
                <tbody>
                  {!carregandoEx && exemplares.length === 0 && (
                    <tr><td colSpan={4} className="p-4 text-center">Nenhum exemplar. Adicione a primeira cópia abaixo.</td></tr>
                  )}
                  {exemplares.map((x) => (
                    <tr key={x.id} className="border-t border-marrom-claro">
                      <td className="p-2">#{x.id}</td>
                      <td className="p-2">{x.capaDura ? "Dura" : "Brochura"}</td>
                      <td className="p-2">{dataBR(x.dataCompra)}</td>
                      <td className="p-2">
                        {x.statusExemplar === "EMPRESTADO"
                          ? <span className={corStatus.EMPRESTADO}>Emprestado</span>
                          : (
                            <select value={x.statusExemplar} onChange={(e) => mudarStatus(x.id, e.target.value as StatusExemplar)}
                              aria-label={`Status do exemplar ${x.id}`}
                              className={`rounded border border-ouro bg-noite/80 px-2 py-1 ${corStatus[x.statusExemplar]}`}>
                              <option value="DISPONIVEL">Disponível</option>
                              <option value="DANIFICADO">Danificado</option>
                              <option value="PERDIDO">Perdido</option>
                            </select>
                          )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <form onSubmit={addExemplar} className="flex flex-wrap items-end gap-3">
              <label className="text-sm text-ouro-neon">Impressão
                <input type="date" value={ex.dataImpressao} onChange={(e) => setEx({ ...ex, dataImpressao: e.target.value })} className={campo + " mb-0 mt-1 block"} />
              </label>
              <label className="text-sm text-ouro-neon">Compra
                <input type="date" value={ex.dataCompra} onChange={(e) => setEx({ ...ex, dataCompra: e.target.value })} className={campo + " mb-0 mt-1 block"} />
              </label>
              <label className="flex items-center gap-2 pb-2 text-lg">
                <input type="checkbox" checked={ex.capaDura} onChange={(e) => setEx({ ...ex, capaDura: e.target.checked })} /> Capa dura
              </label>
              <button className="rounded border border-verde-neon px-4 py-2 text-verde-neon hover:bg-verde-neon hover:text-noite">Adicionar exemplar</button>
            </form>

            <div className="mt-6 flex justify-between border-t border-marrom-claro pt-4">
              <button onClick={() => excluir(aberto)} className="rounded border border-red-400/70 px-4 py-1 text-red-300 hover:bg-red-950/50">Remover livro</button>
              <button onClick={() => setAberto(null)} className="rounded border border-ouro px-4 py-1 text-ouro-neon hover:bg-ouro hover:text-noite">Fechar</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: novo livro */}
      {novo && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-noite/85 p-4" onClick={() => setNovo(false)}>
          <form onSubmit={cadastrar} onClick={(e) => e.stopPropagation()}
            className="borda-neon max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-lg bg-marrom p-6">
            <h2 className="font-gotica mb-4 text-3xl neon-ouro">Novo livro</h2>

            <label className="mb-1 block text-ouro-neon">Título</label>
            <input required value={f.volume} onChange={mudar("volume")} className={campo} />
            <label className="mb-1 block text-ouro-neon">ISBN</label>
            <input required value={f.isbn} onChange={mudar("isbn")} placeholder="978-8535914843" className={campo} />
            <label className="mb-1 block text-ouro-neon">Descrição</label>
            <textarea rows={3} value={f.descricao} onChange={mudar("descricao")} className={campo} />

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="mb-1 block text-ouro-neon">Edição</label>
                <input value={f.edicao} onChange={mudar("edicao")} placeholder="1ª" className={campo} />
              </div>
              <div>
                <label className="mb-1 block text-ouro-neon">Tipo</label>
                <select value={f.tipo} onChange={mudar("tipo")} className={campo}>
                  <option>Físico</option><option>Ebook</option><option>Audiobook</option>
                </select>
              </div>
              <div>
                <label className="mb-1 block text-ouro-neon">Páginas</label>
                <input required type="number" min={1} value={f.paginas} onChange={mudar("paginas")} className={campo} />
              </div>
              <div>
                <label className="mb-1 block text-ouro-neon">Lançamento</label>
                <input type="date" value={f.dataLancamento} onChange={mudar("dataLancamento")} className={campo} />
              </div>
            </div>

            <label className="mb-1 block text-ouro-neon">URL da capa</label>
            <input type="url" value={f.capaUrl} onChange={mudar("capaUrl")} placeholder="https://covers.openlibrary.org/b/isbn/…-M.jpg" className={campo} />

            {erroForm && <p className="mb-3 text-red-400">{erroForm}</p>}

            <div className="flex justify-end gap-3">
              <button type="button" onClick={() => setNovo(false)} className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">Cancelar</button>
              <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
                {salvando ? "Salvando..." : "Salvar livro"}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
