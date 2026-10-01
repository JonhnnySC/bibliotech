"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Capa from "../../../components/Capa";
import { api } from "../../../lib/api";
import type { Livro as LivroT } from "../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export type LivroFormValues = {
  volume: string; isbn: string; descricao: string; edicao: string;
  tipo: string; paginas: string; dataLancamento: string; capaUrl: string;
};

export const livroFormVazio: LivroFormValues = {
  volume: "", isbn: "", descricao: "", edicao: "", tipo: "Físico",
  paginas: "", dataLancamento: "", capaUrl: "",
};

export function valoresDoLivro(l: LivroT): LivroFormValues {
  return {
    volume: l.volume ?? "", isbn: l.isbn ?? "", descricao: l.descricao ?? "",
    edicao: l.edicao ?? "", tipo: l.tipo ?? "Físico",
    paginas: l.paginas != null ? String(l.paginas) : "",
    dataLancamento: l.dataLancamento ?? "", capaUrl: l.capaUrl ?? "",
  };
}

type Props = { modo: "criar" | "editar"; inicial: LivroFormValues; livroId?: number };

export default function LivroForm({ modo, inicial, livroId }: Props) {
  const router = useRouter();
  const [f, setF] = useState<LivroFormValues>(inicial);
  const [salvando, setSalvando] = useState(false);
  const [erroForm, setErroForm] = useState("");

  // prévia da capa com debounce (não martela o Open Library a cada tecla)
  const [previa, setPrevia] = useState({ isbn: inicial.isbn, url: inicial.capaUrl });
  useEffect(() => {
    const t = setTimeout(() => setPrevia({ isbn: f.isbn, url: f.capaUrl }), 400);
    return () => clearTimeout(t);
  }, [f.isbn, f.capaUrl]);

  const mudar = (k: keyof LivroFormValues) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
      setF({ ...f, [k]: e.target.value });

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    const paginas = Number(f.paginas);
    if (!Number.isInteger(paginas) || paginas <= 0) {
      setErroForm("Informe um número de páginas válido.");
      return;
    }
    const corpo = {
      volume: f.volume, isbn: f.isbn, descricao: f.descricao, edicao: f.edicao,
      tipo: f.tipo, paginas, dataLancamento: f.dataLancamento || null, capaUrl: f.capaUrl || null,
    };
    setSalvando(true);
    try {
      if (modo === "criar") {
        await api<LivroT>("/livros", { method: "POST", body: JSON.stringify(corpo) });
        router.push("/livros");
      } else {
        await api<LivroT>(`/livros/${livroId}`, { method: "PUT", body: JSON.stringify(corpo) });
        router.push(`/livros/${livroId}`);
      }
    } catch (err) {
      setErroForm(err instanceof Error ? err.message : "Falha ao salvar");
      setSalvando(false);
    }
  }

  return (
    <form onSubmit={salvar} className="borda-neon rounded-lg bg-marrom/70 p-6">
      <div className="grid grid-cols-1 gap-x-6 sm:grid-cols-2">
        <div>
          <label className="mb-1 block text-ouro-neon">Título</label>
          <input required value={f.volume} onChange={mudar("volume")} className={campo} />
          <label className="mb-1 block text-ouro-neon">ISBN</label>
          <input required value={f.isbn} onChange={mudar("isbn")} placeholder="978-8535914843" className={campo} />
          <label className="mb-1 block text-ouro-neon">Edição</label>
          <input value={f.edicao} onChange={mudar("edicao")} placeholder="1ª" className={campo} />
          <label className="mb-1 block text-ouro-neon">Tipo</label>
          <select value={f.tipo} onChange={mudar("tipo")} className={campo}>
            <option>Físico</option><option>Ebook</option><option>Audiobook</option>
          </select>
          <label className="mb-1 block text-ouro-neon">Páginas</label>
          <input required type="number" min={1} value={f.paginas} onChange={mudar("paginas")} className={campo} />
          <label className="mb-1 block text-ouro-neon">Lançamento</label>
          <input type="date" value={f.dataLancamento} onChange={mudar("dataLancamento")} className={campo} />
        </div>
        <div>
          <label className="mb-1 block text-ouro-neon">URL da capa (opcional)</label>
          <input type="url" value={f.capaUrl} onChange={mudar("capaUrl")}
            placeholder="vazio = busca automática pelo ISBN" className={campo} />
          <div className="mb-4 flex items-start gap-3">
            <Capa isbn={previa.isbn || null} url={previa.url || null} titulo={f.volume || "Prévia"} className="aspect-[2/3] w-32 shrink-0 rounded" />
            <p className="text-sm italic text-ouro/70">Prévia da capa em tempo real.</p>
          </div>
          <label className="mb-1 block text-ouro-neon">Descrição</label>
          <textarea rows={6} value={f.descricao} onChange={mudar("descricao")} className={campo} />
        </div>
      </div>

      {erroForm && <p className="mb-3 text-red-400">{erroForm}</p>}

      <div className="flex justify-end gap-3">
        <button type="button" onClick={() => router.back()}
          className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
          Cancelar
        </button>
        <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
          {salvando ? "Salvando..." : modo === "criar" ? "Salvar livro" : "Salvar alterações"}
        </button>
      </div>
    </form>
  );
}