"use client";

import axios from "axios";
//import Cookies from "js-cookie";
//import { EMAIL_COOKIE, TOKEN_COOKIE } from "./sessao";

//  lugar  para a URL e o token se a URL mudar em produção,
// ou o token mudar de lugar, este arquivo muda.
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

let storeRef: typeof store;
export function configurarApi(s:typeof store) {
  storeRef = s;
}



// interceptor de request
api.interceptors.request.use((config) => {
  const token = storeRef.getState().auth.token;
  if(token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});


// interceptor de respoonse
api.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    //so desloga se houver sessao, falta de perfil nao desloga
    if(erro.response?.status === 401 && storeRef.getState().auth.token) {
      storeRef.dispatch(logout());
    }


    // le o body do erro em verios formatos que o backend pode enviar
    const corpo = erro.response?.data ?? {};
    const mensagem =
      corpo.detail ??
      corpo.error ??
      corpo.message ??
      corpo.erro ??
      (status ? `Erro ${status}` : "Não foi possível falar com o servidor");

    return Promise.reject(new Error(mensagem)); //promessa, ele aguara
  },
);

export default api;
