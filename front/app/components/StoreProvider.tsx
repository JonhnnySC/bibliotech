"use client";
import { useEffect } from "react";
import { Provider } from "react-redux";
import { store } from "../store/store";
import { sessaoRestaurada } from "../store/authSlice";
import { lerSessao } from "../services/sessao";

export default function StoreProvider({ children }: { children: React.ReactNode }) {
  // Ao abrir/recarregar a página, devolve ao Redux a sessão que está no cookie
  useEffect(() => {
    store.dispatch(sessaoRestaurada(lerSessao()));
  }, []);

  return <Provider store={store}>{children}</Provider>;
}
