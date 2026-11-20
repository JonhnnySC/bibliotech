"use client";
import { useState } from "react";
import Link from "next/link";
import Livro from "./Livro";
import { useAppDispatch, useAppSelector } from "../store/hooks";
import { logout } from "../store/authSlice";
import { logoutNoServidor } from "../services/authService";

export default function Header({ onToggle }: { onToggle: () => void }) {
  const dispatch = useAppDispatch();
  const usuario = useAppSelector((s) => s.auth.usuario);
  const [saindo, setSaindo] = useState(false);

  const email = usuario?.email ?? "";
  const perfil = usuario?.perfil ?? "";

  async function sair() {
    setSaindo(true);
    try {
      await logoutNoServidor();   // apaga a linha na tabela token (erro ignorado)
    } finally {
      dispatch(logout());         // store esvazia -> o layout mostra TelaLogin
      setSaindo(false);
    }
  }

  return (
    <header className="borda-neon flex items-center justify-between bg-marrom/80 px-4 py-3">
      <div className="flex items-center gap-3">
        <button type="button" onClick={onToggle} aria-label="Alternar menu"
                className="btn-neon rounded px-3 py-1">☰</button>
        <Link href="/home" className="flex items-center gap-2">
          <Livro className="h-8 w-8 pulsar" />
          <span className="font-gotica text-2xl neon-ouro">BiblioTech</span>
        </Link>
      </div>

      <div className="flex items-center gap-4">
        <div className="text-right leading-tight">
          <p className="text-ouro-neon">{email}</p>
          <p className="text-xs text-ouro">{perfil}</p>
        </div>
        <button type="button" onClick={sair} disabled={saindo}
                className="btn-neon font-gotica rounded-full px-4 py-1 text-xl disabled:opacity-50">
          {saindo ? "Fechando..." : "Sair"}
        </button>
      </div>
    </header>
  );
}