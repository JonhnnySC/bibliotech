"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { api } from "../../../../lib/api";
import type { Perfil, Usuario } from "../../../../types/usuario";

const campo = "mb-3 w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";

export default function EditarUsuarioPage() {
  const params = useParams();
  const router = useRouter();
  const id = Number(params?.id);

  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [form, setForm] = useState({ nome: "", email: "", cpf: "" });
  const [perfil, setPerfil] = useState<Perfil>("LEITOR");
  const [souAdmin, setSouAdmin] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    let vivo = true;
    const emailLogado = localStorage.getItem("email");

    Promise.all([
      api<Usuario>(`/usuarios/${id}`),
      api<Usuario[]>("/usuarios"),
    ])
      .then(([u, todos]) => {
        if (!vivo) return;
        setUsuario(u);
        setForm({ nome: u.nome ?? "", email: u.email ?? "", cpf: u.cpf ?? "" });
        setPerfil(u.perfil);
        setSouAdmin(
          todos.some((x) => x.email === emailLogado && String(x.perfil) === "ADMINISTRADOR")
        );
      })
      .catch((e) => { if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar usuário"); })
      .finally(() => { if (vivo) setCarregando(false); });

    return () => { vivo = false; };
  }, [id]);

  const mudar = (k: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value });

  async function salvar(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSalvando(true);
    try {
      // 1) dados cadastrais
      await api(`/usuarios/${id}`, {
        method: "PUT",
        body: JSON.stringify({ nome: form.nome, email: form.email, cpf: form.cpf }),
      });

      // 2) perfil (só se for ADMIN e se mudou) — endpoint exclusivo de ADMIN
      if (souAdmin && usuario && perfil !== usuario.perfil) {
        await api(`/usuarios/${id}/perfil`, {
          method: "PATCH",
          body: JSON.stringify({ perfil }),
        });
      }

      router.push("/usuarios");
    } catch (err) {
      setErro(err instanceof Error ? err.message : "Falha ao salvar");
      setSalvando(false);
    }
  }

  if (carregando) return <p className="text-lg">Carregando...</p>;
  if (!usuario) {
    return (
      <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">
        {erro || "Usuário não encontrado"}
      </p>
    );
  }

  return (
    <div className="mx-auto max-w-2xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/usuarios" className="hover:text-verde-neon">← Voltar aos usuários</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Editando: {usuario.nome}</h1>

      <form onSubmit={salvar} className="borda-neon rounded-lg bg-marrom/70 p-6">
        <label className="mb-1 block text-ouro-neon">Nome *</label>
        <input required value={form.nome} onChange={mudar("nome")} className={campo} />

        <label className="mb-1 block text-ouro-neon">E-mail *</label>
        <input required type="email" value={form.email} onChange={mudar("email")} className={campo} />

        <label className="mb-1 block text-ouro-neon">CPF</label>
        <input value={form.cpf} onChange={mudar("cpf")} className={campo} placeholder="000.000.000-00" />

        {souAdmin && (
          <>
            <label className="mb-1 block text-ouro-neon">Perfil</label>
            <select
              value={perfil}
              onChange={(e) => setPerfil(e.target.value as Perfil)}
              className={campo}
            >
              <option value="LEITOR">LEITOR</option>
              <option value="BIBLIOTECARIO">BIBLIOTECARIO</option>
              <option value="ADMINISTRADOR">ADMINISTRADOR</option>
            </select>
            <p className="mb-4 text-xs text-ouro/60">
              A mudança de perfil só vale para o usuário depois que ele sair e entrar de novo.
            </p>
          </>
        )}

        {erro && <p className="mb-3 text-red-400">{erro}</p>}

        <div className="flex justify-end gap-3">
          <button type="button" onClick={() => router.back()}
            className="rounded border border-ouro px-4 py-2 text-ouro-neon hover:bg-ouro hover:text-noite">
            Cancelar
          </button>
          <button disabled={salvando}
            className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
            {salvando ? "Salvando..." : "Salvar alterações"}
          </button>
        </div>
      </form>
    </div>
  );
}