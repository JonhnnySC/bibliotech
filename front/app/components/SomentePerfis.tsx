"use client";
import { useAppSelector } from "../store/hooks";

export default function SomentePerfis({ perfis, children }:
                                      { perfis: string[]; children: React.ReactNode }) {
    const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
    if (!perfil || !perfis.includes(perfil)) {
        return <p className="mx-auto max-w-xl p-6 text-lg text-ouro-neon">Acesso restrito.</p>;
    }
    return <>{children}</>;
}