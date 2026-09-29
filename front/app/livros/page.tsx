"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import axios from "axios";
import { Livro } from "@/app/types/livro";

export default function Livros() {
  const [livros, setLivros] = useState<Livro[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      const dados = await axios.get<Livro[]>("http://localhost:8080/livros");
      setLivros(dados.data);
    } catch (error) {
      alert("Erro ao carregar grimórios!");
    } finally {
      setLoading(false);
    }
  };

  const getGeneroLabel = (genero: string) => {
    const labels: Record<string, string> = {
      'FICCAO': 'Ficção',
      'NAO_FICCAO': 'Não-Ficção',
      'TECNICO': 'Técnico',
      'LITERATURA': 'Literatura',
      'OUTRO': 'Outro',
    };
    return labels[genero] || genero;
  };

  return (
    <div className="w-full space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-3xl font-['Cinzel'] text-amber-100 tracking-wide flex items-center gap-3">
          <span className="text-emerald-500">📚</span> Grimório da Biblioteca
        </h1>
        <Link
          href="/livros/novo"
          className="inline-flex items-center justify-center px-6 py-3 bg-emerald-900 hover:bg-emerald-800 text-amber-50 font-['Cinzel'] text-sm rounded-tl-xl rounded-br-xl border border-emerald-600/50 shadow-sm transition-all duration-300 hover:shadow-[0_0_20px_rgba(16,185,129,0.3)]"
        >
          + Novo Grimório
        </Link>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full text-center py-16 text-stone-500 animate-pulse">
            Carregando grimórios...
          </div>
        ) : livros.length === 0 ? (
          <div className="col-span-full text-center py-16 text-stone-500 font-['Cormorant_Garamond'] italic text-lg">
            Nenhum grimório encontrado nos arquivos da biblioteca.
          </div>
        ) : (
          livros.map((livro) => (
            <div
              key={livro.id}
              className="p-6 bg-stone-950/50 rounded-tl-2xl rounded-br-2xl border border-amber-700/20 hover:border-amber-500/40 transition-all duration-300 group"
            >
              <div className="flex items-start justify-between mb-4">
                <h3 className="text-xl font-['Cinzel'] text-amber-100 group-hover:text-amber-50 transition-colors">
                  {livro.titulo}
                </h3>
                <span className="text-2xl">📖</span>
              </div>
              <p className="text-stone-400 text-sm mb-2">
                <span className="text-amber-600">Autor:</span> {livro.autor?.nome || 'Desconhecido'}
              </p>
              <p className="text-stone-400 text-sm mb-2">
                <span className="text-amber-600">Editora:</span> {livro.editora?.nome || 'Desconhecida'}
              </p>
              <p className="text-stone-400 text-sm mb-4">
                <span className="text-amber-600">Ano:</span> {livro.anoPublicacao}
              </p>
              <div className="flex items-center justify-between">
                <span className="px-3 py-1 bg-emerald-950/50 text-emerald-400 border border-emerald-700/50 rounded-full text-xs font-['Cinzel']">
                  {getGeneroLabel(livro.genero)}
                </span>
                <span className="text-stone-500 text-xs">
                  {livro.exemplares?.length || 0} exemplar(es)
                </span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}