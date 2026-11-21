"use client";
import axios from "axios";
import type { store } from "../store/store";
import { logout } from "../store/authSlice";

export const api = axios.create({          // export nomeado + default, os dois funcionam
    baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
    headers: { "Content-Type": "application/json" },
});

let storeRef: typeof store;
export function configurarApi(s: typeof store) {
    storeRef = s;
}

api.interceptors.request.use((config) => {
    const token = storeRef.getState().auth.token;
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

api.interceptors.response.use(
    (resposta) => resposta,
    (erro) => {
        if (erro.response?.status === 401 && storeRef.getState().auth.token) {
            storeRef.dispatch(logout());
        }

        const status = erro.response?.status;   // ← faltava esta linha
        const corpo = erro.response?.data ?? {};
        const mensagem =
            corpo.detail ?? corpo.error ?? corpo.message ?? corpo.erro ??
            (status ? `Erro ${status}` : "Não foi possível falar com o servidor");

        return Promise.reject(new Error(mensagem));
    },
);

export default api;