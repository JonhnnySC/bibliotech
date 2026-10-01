"use client";
import { dataBR, diasRestantes } from "../../../lib/datas";
import type { Emprestimo } from "../../../types/usuario";

type Props = {
  emprestimos: Emprestimo[];
  carregando?: boolean;
  onDevolver: (id: number) => void;
};

/**
 * Tabela de empréstimos em aberto.
 * Recebe os dados prontos + o callback de devolução (quem decide é a página).
 */
export default function EmprestimosTable({ emprestimos, carregando, onDevolver }: Props) {
  // Título vem aninhado (exemplar.livro); fallback defensivo
  const tituloLivro = (e: Emprestimo) =>
    e.exemplar.livro?.volume ?? `Livro #${e.exemplar.livro?.id ?? e.exemplar.id}`;

  return (
    <div className="overflow-x-auto rounded-lg border border-ouro bg-marrom/60">
      <table className="w-full text-left">
        <thead className="bg-musgo text-ouro-neon">
          <tr>
            <th className="p-3">Livro</th><th className="p-3">Leitor</th><th className="p-3">Saída</th>
            <th className="p-3">Devolver até</th><th className="p-3">Prazo</th><th className="p-3" />
          </tr>
        </thead>
        <tbody>
          {!carregando && emprestimos.length === 0 && (
            <tr>
              <td colSpan={6} className="p-6 text-center text-lg">
                Nenhum empréstimo em aberto. Use “Novo empréstimo” para registrar uma saída.
              </td>
            </tr>
          )}
          {emprestimos.map((e) => {
            const dias = diasRestantes(e.dataPrevistaDevolucao);
            return (
              <tr key={e.id} className="border-t border-marrom-claro hover:bg-noite/30 transition-colors">
                <td className="p-3">{tituloLivro(e)} <span className="text-sm text-ouro">(ex. #{e.exemplar.id})</span></td>
                <td className="p-3">{e.leitor.nome}</td>
                <td className="p-3">{dataBR(e.dataEmprestimo)}</td>
                <td className="p-3">{dataBR(e.dataPrevistaDevolucao)}</td>
                <td className={`p-3 ${dias < 0 ? "text-red-400" : dias <= 3 ? "neon-ouro" : "neon-verde"}`}>
                  {dias < 0 ? `${-dias} dia(s) de atraso` : dias === 0 ? "Vence hoje" : `${dias} dia(s)`}
                </td>
                <td className="p-3 text-right">
                  <button onClick={() => onDevolver(e.id)}
                    className="rounded border border-verde-neon px-3 py-1 text-verde-neon hover:bg-verde-neon hover:text-noite">
                    Devolver
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}