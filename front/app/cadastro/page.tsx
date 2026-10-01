"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Livro from "../components/Livro";
import { api } from "../lib/api";
import type { LoginResponse } from "../types/auth";

const campo = "mb-4 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export default function Cadastro() {
  const router = useRouter();
  const [f, setF] = useState({ nome: "", email: "", cpf: "", senha: "", confirmar: "" });
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  const mudar = (k: keyof typeof f) => (e: React.ChangeEvent<HTMLInputElement>) => setF({ ...f, [k]: e.target.value });

  async function cadastrar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    const cpf = f.cpf.replace(/\D/g, ""); // só números
    if (cpf.length !== 11) return setErro("O CPF precisa ter 11 dígitos.");
    if (f.senha.length < 6) return setErro("A senha precisa ter ao menos 6 caracteres.");
    if (f.senha !== f.confirmar) return setErro("As senhas não coincidem.");

    setCarregando(true);
    try {
      // 1) cria o usuário em /auth/registro (público; o back define perfil LEITOR e status ATIVO)
      await api("/auth/registro", { method: "POST", body: JSON.stringify({ nome: f.nome, email: f.email, cpf, senha: f.senha }) });
      // 2) já faz o login para pegar o token
      const r = await api<LoginResponse>("/auth/login", { method: "POST", body: JSON.stringify({ email: f.email, senha: f.senha }) });
      localStorage.setItem("token", r.token);
      localStorage.setItem("email", f.email);
      router.push("/home");
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao cadastrar");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-8">
      <form onSubmit={cadastrar} className="borda-neon w-full max-w-md rounded-lg bg-marrom/80 p-8">
        <div className="mb-6 flex flex-col items-center">
          <Livro className="h-16 w-16 pulsar" />
          <h1 className="font-gotica mt-2 text-4xl neon-ouro">Inscrever-se</h1>
        </div>

        <label className="mb-1 block text-ouro-neon">Nome</label>
        <input required value={f.nome} onChange={mudar("nome")} className={campo} />
        <label className="mb-1 block text-ouro-neon">E-mail</label>
        <input type="email" required value={f.email} onChange={mudar("email")} className={campo} />
        <label className="mb-1 block text-ouro-neon">CPF</label>
        <input required inputMode="numeric" placeholder="000.000.000-00" value={f.cpf} onChange={mudar("cpf")} className={campo} />
        <label className="mb-1 block text-ouro-neon">Senha</label>
        <input type="password" required value={f.senha} onChange={mudar("senha")} className={campo} />
        <label className="mb-1 block text-ouro-neon">Confirmar senha</label>
        <input type="password" required value={f.confirmar} onChange={mudar("confirmar")} className={campo} />

        {erro && <p className="mb-4 text-red-400">{erro}</p>}

        <button disabled={carregando} className="btn-neon font-gotica w-full rounded-full py-2 text-2xl disabled:opacity-50">
          {carregando ? "Escrevendo seu nome..." : "Criar conta"}
        </button>
        <p className="mt-5 text-center">Já tem conta? <Link href="/login" className="neon-verde hover:underline">Entrar</Link></p>
      </form>
    </main>
  );
}
