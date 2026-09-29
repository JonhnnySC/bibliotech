"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import axios from "axios";

interface Usuario {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  status: string;
}

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      const dados = await axios.get<Usuario[]>("http://localhost:8080/usuarios");
      setUsuarios(dados.data);
    } catch (error) {
      alert("Erro ao carregar dados!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-3xl font-['Cinzel'] text-[#f5e6d3] tracking-wide">
          Gestão de Leitores
        </h1>
        <Link
          href="/usuarios/novo"
          className="inline-flex items-center justify-center px-6 py-3 bg-gradient-to-r from-[#d4a574] to-[#e8c97a] text-[#0f1f16] font-['Cinzel'] text-sm font-bold rounded-lg border-2 border-[#e8c97a] shadow-sm transition-all duration-300 hover:shadow-[0_0_20px_rgba(232,201,122,0.4)]"
        >
          + Novo Leitor
        </Link>
      </div>

      <div className="bg-[#0f1f16]/60 backdrop-blur-sm border border-[#d4a574]/30 rounded-lg shadow-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-[#1a2e1f]/80 border-b border-[#d4a574]/40">
                {["Código", "Nome", "CPF", "E-mail", "Status"].map((header) => (
                  <th
                    key={header}
                    className="px-6 py-4 text-xs font-['Cinzel'] font-bold text-[#e8c97a] uppercase tracking-widest"
                  >
                    {header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-[#d4a574]/20">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-16 text-center text-[#f5e6d3]/60">
                    Carregando...
                  </td>
                </tr>
              ) : usuarios.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-16 text-center text-[#f5e6d3]/60 font-['Cormorant_Garamond'] italic text-lg">
                    Nenhum leitor encontrado.
                  </td>
                </tr>
              ) : (
                usuarios.map((usuario) => (
                  <tr key={usuario.id} className="hover:bg-[#1a3d2e]/20 transition-colors duration-300">
                    <td className="px-6 py-4 text-sm font-medium text-[#f5e6d3]/80">{usuario.id}</td>
                    <td className="px-6 py-4 text-sm font-medium text-[#f5e6d3]">{usuario.nome}</td>
                    <td className="px-6 py-4 text-sm font-medium text-[#f5e6d3]/60 font-mono">{usuario.cpf}</td>
                    <td className="px-6 py-4 text-sm font-medium text-[#f5e6d3]/60">{usuario.email}</td>
                    <td className="px-6 py-4 text-sm font-medium">
                      <span
                        className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-['Cinzel'] font-bold border ${
                          usuario.status === "ATIVO"
                            ? "bg-[#1a3d2e]/50 text-[#e8c97a] border-[#d4a574]/50"
                            : "bg-[#2a1f14]/50 text-[#f5e6d3]/40 border-[#d4a574]/20"
                        }`}
                      >
                        <span
                          className={`w-1.5 h-1.5 rounded-full ${
                            usuario.status === "ATIVO" ? "bg-[#e8c97a] animate-pulse-gold" : "bg-[#f5e6d3]/40"
                          }`}
                        ></span>
                        {usuario.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}