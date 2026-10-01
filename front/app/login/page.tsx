"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Livro from "../components/Livro";
import { api } from "../lib/api";
import type { LoginResponse } from "../types/auth";

export default function Login() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function entrar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setCarregando(true);
    try {
      const r = await api<LoginResponse>("/auth/login", { method: "POST", body: JSON.stringify({ email, senha }) });
      localStorage.setItem("token", r.token);
      localStorage.setItem("email", email);
      router.push("/home");
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao entrar");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center px-4">
      <form onSubmit={entrar} className="borda-neon w-full max-w-md rounded-lg bg-marrom/80 p-8">
        <div className="mb-6 flex flex-col items-center">
          <Livro className="h-16 w-16 pulsar" />
          <h1 className="font-gotica mt-2 text-4xl neon-ouro">Abrir o grimório</h1>
        </div>

        <label className="mb-1 block text-ouro-neon">E-mail</label>
        <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)}
          className="mb-4 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]" />

        <label className="mb-1 block text-ouro-neon">Senha</label>
        <input type="password" required value={senha} onChange={(e) => setSenha(e.target.value)}
          className="mb-4 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]" />

        {erro && <p className="mb-4 text-red-400">{erro}</p>}

        <button disabled={carregando} className="btn-neon font-gotica w-full rounded-full py-2 text-2xl disabled:opacity-50">
          {carregando ? "Invocando..." : "Entrar"}
        </button>
        <p className="mt-5 text-center">Ainda não tem conta? <Link href="/cadastro" className="neon-verde hover:underline">Criar conta</Link></p>
        <Link href="/" className="mt-2 block text-center text-ouro hover:text-ouro-neon">← Voltar</Link>
      </form>
    </main>
  );
}
