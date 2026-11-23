"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import Capa from "@/app/components/Capa";
import ExemplaresTable from "../components/ExemplaresTable";
import { buscarLivro, excluirLivro } from "@/app/services/livroService";
import { useAppSelector } from "@/app/store/hooks";
import type { Livro as LivroT } from "@/app/types/usuario";

const anoLancamento = (iso?: string | null) => (iso ? iso.split("-")[0] : "");

export default function DetalheLivroPage() {
  const params = useParams();
  const router = useRouter();
  const raw = params?.id;
  const id = Number(Array.isArray(raw) ? raw[0] : raw);

  // o perfil já vem no login e mora na store: não precisa listar usuários
  const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
  const souAdmin = perfil === "ADMINISTRADOR";

  const [livro, setLivro] = useState<LivroT | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    if (!Number.isFinite(id)) {
      setErro("Identificador de livro inválido.");
      setCarregando(false);
      return;
    }
    let vivo = true;
    setCarregando(true);
    setErro("");
    buscarLivro(id)
        .then((d) => { if (vivo) setLivro(d); })
        .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar o livro"); })
        .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
  }, [id]);

  async function excluir() {
    if (!livro) return;
    if (!confirm(`Remover "${livro.volume}" do acervo?`)) return;
    try {
      await excluirLivro(livro.id);
      router.push("/livros");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao remover o livro");
    }
  }

  if (carregando) return <p className="text-lg">Abrindo o grimório...</p>;
  if (erro || !livro) return <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro || "Livro não encontrado."}</p>;

  return (
      <div className="mx-auto max-w-5xl">
        <nav className="mb-4 text-sm text-ouro/70">
          <Link href="/livros" className="hover:text-verde-neon">← Voltar ao acervo</Link>
        </nav>

        <div className="borda-neon mb-8 rounded-lg bg-marrom/70 p-6">
          <div className="flex flex-col gap-6 sm:flex-row">
            <Capa isbn={livro.isbn} url={livro.capaUrl} titulo={livro.volume} className="aspect-[2/3] w-44 shrink-0 self-center rounded sm:self-start" />
            <div className="flex-1">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <h1 className="font-gotica text-3xl neon-ouro sm:text-4xl">{livro.volume}</h1>
                {souAdmin && (
                    <div className="flex gap-2">
                      <Link href={`/livros/${livro.id}/editar`}
                            className="rounded border border-verde-neon px-3 py-1 text-verde-neon hover:bg-verde-neon hover:text-noite">
                        ✏️ Editar
                      </Link>
                      <button onClick={excluir}
                              className="rounded border border-red-400/70 px-3 py-1 text-red-300 hover:bg-red-950/50">
                        Remover
                      </button>
                    </div>
                )}
              </div>
              <dl className="mt-3 grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-lg">
                <dt className="text-ouro">ISBN</dt><dd>{livro.isbn}</dd>
                <dt className="text-ouro">Edição</dt><dd>{livro.edicao ?? "—"}</dd>
                <dt className="text-ouro">Tipo</dt><dd>{livro.tipo ?? "—"}</dd>
                <dt className="text-ouro">Páginas</dt><dd>{livro.paginas}</dd>
                <dt className="text-ouro">Lançamento</dt><dd>{anoLancamento(livro.dataLancamento) || "—"}</dd>
              </dl>
              {livro.descricao && <p className="mt-3 text-lg leading-snug">{livro.descricao}</p>}
            </div>
          </div>
        </div>

        <ExemplaresTable livroId={livro.id} podeGerenciar={souAdmin} />
      </div>
  );
}