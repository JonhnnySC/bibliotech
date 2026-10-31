"use client";

import axios from "axios";
import Cookies from "js-cookie";
import { EMAIL_COOKIE, TOKEN_COOKIE } from "./sessao";

// UM lugar só para a URL e o token (Aula 19.10): se a URL mudar em produção,
// ou o token mudar de lugar, só este arquivo muda.
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

// Toda requisição sai com o token do cookie anexado
api.interceptors.request.use((config) => {
  const token = Cookies.get(TOKEN_COOKIE);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Se o token expirou (401), apaga a sessão e volta para o login
api.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const status: number | undefined = erro.response?.status;

    if (status === 401 && Cookies.get(TOKEN_COOKIE) && typeof window !== "undefined") {
      Cookies.remove(TOKEN_COOKIE);
      Cookies.remove(EMAIL_COOKIE);
      // fora de componente React não há useRouter; recarregar a página limpa o estado do Redux
      // eslint-disable-next-line @next/next/no-location-assign-relative-destination
      window.location.href = "/login";
    }

    // Lê o corpo do erro em vários formatos que o backend pode mandar (ProblemDetail etc.)
    const corpo = erro.response?.data ?? {};
    const mensagem =
      corpo.detail ??
      corpo.error ??
      corpo.message ??
      corpo.erro ??
      (status ? `Erro ${status}` : "Não foi possível falar com o servidor");

    return Promise.reject(new Error(mensagem));
  },
);

export default api;
