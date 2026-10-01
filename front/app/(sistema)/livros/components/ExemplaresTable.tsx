"use client";
import { useEffect, useState } from "react";
import { api } from "../../../lib/api";
import type { Exemplar, StatusExemplar } from "../../../types/usuario";

const dataBR = (iso?: string | null) => (iso ? iso.split("-").reverse().join("/") : "—");
const campoData = "mb-0 mt-1 block rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

const corStatus: Record<StatusExemplar, string> = {
  DISPONIVEL: "neon-verde", EMPRESTADO: "neon-ouro", DANIFICADO: "text-red-400", PERDIDO: "text-red-400",
};

type Props = { livroId: number; podeGerenciar: boolean };

export default function ExemplaresTable({ livroId, podeGerenciar }: Props) {
  const [exemplares, setExemplares] = useState<Exemplar[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [form, setForm] = useState({ dataImpressao: "", dataCompra: "", capaDura: false });

  useEffect(() => {
    let vivo = true;
    api<Exemplar[]>(`/exemplares/livro/${livroId}`)
      .then((d) => { if (vivo) { setExemplares(d); setErro(""); } })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar exemplares"); })
      .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, [livroId]);

  async function recarregar() {
    try {
      setExemplares(await api<Exemplar[]>(`/exemplares/livro/${livroId}`));
      setErro("");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar exemplares");
    } finally {
      setCarregando(false);
    }
  }

  async function adicionar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    try {
      await api("/exemplares", {
        method: "POST",
        body: JSON.stringify({
          livroId,
          dataImpressao: form.dataImpressao || null,
          dataCompra: form.dataCompra || null,
          capaDura: form.capaDura,
        }),
      });
      setForm({ dataImpressao: "", dataCompra: "", capaDura: false });
      recarregar();
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao adicionar exemplar");
    }
  }

  async function mudarStatus(id: number, status: StatusExemplar) {
    try {
      await api(`/exemplares/${id}/status?statusExemplar=${status}`, { method: "PATCH" });
      recarregar();
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao mudar o status");
    }
  }

  const disponiveis = exemplares.filter((x) => x.statusExemplar === "DISPONIVEL").length;

  return (
    <section>
      <h3 className="font-gotica mb-2 text-2xl neon-verde">
        Exemplares {!carregando && exemplares.length > 0 && <span className="text-lg text-ouro-neon">({disponiveis} disponível(is))</span>}
      </h3>
      {erro && <p className="mb-3 text-red-400">{erro}</p>}

      <div className="mb-4 overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
        <table className="w-full text-left">
          <thead className="bg-musgo text-ouro-neon">
            <tr><th className="p-2">Nº</th><th className="p-2">Capa</th><th className="p-2">Compra</th><th className="p-2">Status</th></tr>
          </thead>
          <tbody>
            {!carregando && exemplares.length === 0 && (
              <tr><td colSpan={4} className="p-4 text-center">Nenhum exemplar. Adicione a primeira cópia abaixo.</td></tr>
            )}
            {exemplares.map((x) => (
              <tr key={x.id} className="border-t border-marrom-claro">
                <td className="p-2">#{x.id}</td>
                <td className="p-2">{x.capaDura ? "Dura" : "Brochura"}</td>
                <td className="p-2">{dataBR(x.dataCompra)}</td>
                <td className="p-2">
                  {podeGerenciar && x.statusExemplar !== "EMPRESTADO" ? (
                    <select value={x.statusExemplar} onChange={(e) => mudarStatus(x.id, e.target.value as StatusExemplar)}
                      aria-label={`Status do exemplar ${x.id}`}
                      className={`rounded border border-ouro bg-noite/80 px-2 py-1 ${corStatus[x.statusExemplar]}`}>
                      <option value="DISPONIVEL">Disponível</option>
                      <option value="DANIFICADO">Danificado</option>
                      <option value="PERDIDO">Perdido</option>
                    </select>
                  ) : (
                    <span className={corStatus[x.statusExemplar]}>{x.statusExemplar}</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {podeGerenciar && (
        <form onSubmit={adicionar} className="flex flex-wrap items-end gap-3">
          <label className="text-sm text-ouro-neon">Impressão
            <input type="date" value={form.dataImpressao} onChange={(e) => setForm({ ...form, dataImpressao: e.target.value })} className={campoData} />
          </label>
          <label className="text-sm text-ouro-neon">Compra
            <input type="date" value={form.dataCompra} onChange={(e) => setForm({ ...form, dataCompra: e.target.value })} className={campoData} />
          </label>
          <label className="flex items-center gap-2 pb-2 text-lg">
            <input type="checkbox" checked={form.capaDura} onChange={(e) => setForm({ ...form, capaDura: e.target.checked })} /> Capa dura
          </label>
          <button className="rounded border border-verde-neon px-4 py-2 text-verde-neon hover:bg-verde-neon hover:text-noite">Adicionar exemplar</button>
        </form>
      )}
    </section>
  );
}