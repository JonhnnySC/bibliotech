// timer desloga no instant do vencimento, mesmo sem requesição, pra que nao precise dar refreh no negocio, faz o login
//aparecer soizinho quando o jwt expiration minuts;

"use client";

import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../store/hooks";

export default function ExpiracaoToken() {
  const dispatch = useAppDispatch();
  const expiraEm = useAppSelector((s) => s.auth.expiraEm);

  useEffect(() => {
    if (!expiraEm) return;
    const venceEm = new Date(expiraEm).getTime();
    const verificar = () => {
      if (Date.now() >= venceEm) dispatch(logout());
    };
    const timer = setTimeout(verificar, Math.max(venceEm - Date.now(), 0));
    // aba em segundo plano tem timer atrasado: ao voltar, confere de novo
    document.addEventListener("visibilitychange", verificar);
    return () => {
      clearTimeout(timer);
      document.removeEventListener("visibilitychange", verificar);
    };
  }, [expiraEm, dispatch]);

  return null;
}