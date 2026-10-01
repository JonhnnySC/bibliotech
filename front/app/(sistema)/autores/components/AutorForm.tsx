"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "../../../lib/api";
import type { Autor, AutorForm } from "../../../types/autor";
import type { Livro } from "../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

type Props = {
  modo: "criar" | "editar";
  inicial: AutorForm;
  autorId?: number;
};

export default function AutorForm({ modo, inicial, autorId }: Props) {
  const router = useRouter();
  const [form, setForm] = useState<AutorForm>(inicial);
  const [livros, setLivros] = useState<Livro[]>([]);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  // Carregar lista de livros para o multi-select
  useEffect(() => {
    api<Livro[]>("/livros")
      .then(setLivros)
      .catch(() => setErro("Erro ao carregar livros"));
  }, []);

  const mudar = (k: keyof AutorForm) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
      setForm({ ...form, [k]: e.target.value });

  const toggleLivro = (livroId: number) => {
    setForm({
      ...form,
      livroIds: form.livroIds.includes(livroId)
        ? form.livroIds.filter((id) => id !== livroId)
        : [...form.livroIds, livroId],
    });
  };

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSalvando(true);

    try {
      const corpo = {
        nome: form.nome,
        nacionalidade: form.nacionalidade || null,
        dataNascimento: form.dataNascimento || null,
        fotoUrl: form.fotoUrl || null,
        livroIds: form.livroIds,
      };

      if (modo === "criar") {
        await api<Autor>("/autores", { method: "POST", body: JSON.stringify(corpo) });
        router.push("/autores");
      } else {
        await api<Autor>(`/autores/${autorId}`, { method: "PUT", body: JSON.stringify(corpo) });
        router.push(`/autores/${autorId}`);
      }
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao salvar");
      setSalvando(false);
    }
  }

  return (
    <form onSubmit={salvar} className="borda-neon rounded-lg bg-marrom/70 p-6">
      <div className="grid grid-cols-1 gap-x-6 sm:grid-cols-2">
        <div>
          <label className="mb-1 block text-ouro-neon">Nome *</label>
          <input required value={form.nome} onChange={mudar("nome")} className={campo} />

          <label className="mb-1 block text-ouro-neon">Nacionalidade</label>
          <input value={form.nacionalidade} onChange={mudar("nacionalidade")} className={campo} placeholder="Brasileira" />

          <label className="mb-1 block text-ouro-neon">Data de Nascimento</label>
          <input type="date" value={form.dataNascimento} onChange={mudar("dataNascimento")} className={campo} />

          <label className="mb-1 block text-ouro-neon">URL da Foto</label>
          <input type="url" value={form.fotoUrl} onChange={mudar("fotoUrl")} className={campo} placeholder="https://..." />
          
          {form.fotoUrl && (
            <div className="mb-4 flex items-center gap-3">
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img src={form.fotoUrl} alt="Prévia" className="borda-neon h-24 w-24 rounded-full object-cover" />
              <p className="text-sm italic text-ouro/70">Prévia da foto</p>
            </div>
          )}
        </div>

        <div>
          <label className="mb-1 block text-ouro-neon">Livros deste autor</label>
          <div className="max-h-64 overflow-y-auto rounded border border-ouro bg-noite/80 p-2">
            {livros.length === 0 ? (
              <p className="text-sm text-ouro/60">Nenhum livro cadastrado</p>
            ) : (
              livros.map((livro) => (
                <label key={livro.id} className="flex items-center gap-2 p-1 hover:bg-musgo/30 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={form.livroIds.includes(livro.id)}
                    onChange={() => toggleLivro(livro.id)}
                  />
                  <span className="text-sm">{livro.volume}</span>
                </label>
              ))
            )}
          </div>
          <p className="mt-1 text-xs text-ouro/60">
            Selecione os livros que este autor escreveu
          </p>
        </div>
      </div>

      {erro && <p className="mb-3 text-red-400">{erro}</p>}

      <div className="flex justify-end gap-3">
        <button type="button" onClick={() => router.back()}
          className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
          Cancelar
        </button>
        <button disabled={salvando} className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
          {salvando ? "Salvando..." : modo === "criar" ? "Salvar autor" : "Salvar alterações"}
        </button>
      </div>
    </form>
  );
}