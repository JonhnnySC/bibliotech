"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import UsuarioForm, { valoresDoUsuario } from "../../components/UsuarioForm";
import { api } from "../../../../lib/api";
import type { Usuario } from "../../../../types/usuario";

/** Rota /usuarios/[id]/editar — hidrata o form compartilhado com o usuário atual. */
export default function EditarUsuarioPage() {
  const params = useParams();
  const raw = params?.id;
  const id = Number(Array.isArray(raw) ? raw[0] : raw);

  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

    useEffect(() => {
    let vivo = true;
    // Lista e pega o usuário pelo id (backend não expõe GET por id público aqui)
    api<Usuario[]>("/usuarios")
        .then((d: Usuario[]) => {                       // ← tipo explícito mata o TS7006
        if (vivo) setUsuario(d.find((u) => u.id === id) ?? null);
        })
        .catch((e: unknown) => {                          // ← idem
        if (vivo) setErro(e instanceof Error ? e.message : "Erro ao carregar usuário");
        })
        .finally(() => { if (vivo) setCarregando(false); });
    return () => { vivo = false; };
    }, [id]);

  if (carregando) return <p className="text-lg">Consultando os registros...</p>;
  if (erro || !usuario) {
    return <p className="rounded border border-red-400/60 bg-red-950/40 p-3 text-red-300">{erro || "Usuário não encontrado."}</p>;
  }

  return (
    <div className="mx-auto max-w-2xl">
      <nav className="mb-4 text-sm text-ouro/70">
        <Link href="/usuarios" className="hover:text-verde-neon">← Voltar aos usuários</Link>
      </nav>
      <h1 className="font-gotica mb-6 text-4xl neon-ouro">Editando: {usuario.nome}</h1>
      <UsuarioForm modo="editar" inicial={valoresDoUsuario(usuario)} usuarioId={usuario.id} />
    </div>
  );
}