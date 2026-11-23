"use client";
import { useEffect, useState } from "react";
import { api } from "@/app/services/api";

type Endereco = { logradouro?: string; bairro?: string; cidade?: string; uf?: string };

export default function MinhaBiblioteca() {
    const [nome, setNome] = useState("");
    const [cep, setCep] = useState("");
    const [numero, setNumero] = useState("");
    const [complemento, setComplemento] = useState("");
    const [endereco, setEndereco] = useState<Endereco>({});
    const [msg, setMsg] = useState<{ tipo: "ok" | "erro"; texto: string } | null>(null);

    useEffect(() => {
        api.get("/bibliotecas/minha-biblioteca")
            .then(({ data }) => {
                setNome(data.nome ?? "");
                setCep(data.cep ?? "");
                setNumero(data.numero ?? "");
                setComplemento(data.complemento ?? "");
                setEndereco({ logradouro: data.logradouro, bairro: data.bairro,
                    cidade: data.cidade, uf: data.uf });
            })
            .catch(() => setMsg({ tipo: "erro", texto: "Não foi possível carregar a biblioteca" }));
    }, []);

    async function buscarCep() {
        const soDigitos = cep.replace(/\D/g, "");
        if (soDigitos.length !== 8) return;   // "123" não dispara chamada nenhuma
        try {
            const { data } = await api.get(`/bibliotecas/cep/${soDigitos}`);
            setEndereco({ logradouro: data.logradouro, bairro: data.bairro, cidade: data.localidade, uf: data.uf });
            setMsg(null);
        } catch (e) {
            setEndereco({});
            setMsg({ tipo: "erro", texto: e instanceof Error ? e.message : "Não foi possível consultar o CEP" });
        }
    }

    async function salvar(ev: React.FormEvent) {
        ev.preventDefault();
        try {
            await api.put("/bibliotecas/minha-biblioteca", {
                nome, cep: cep.replace(/\D/g, ""), numero, complemento,
            });
            setMsg({ tipo: "ok", texto: "Biblioteca atualizada" });
        } catch (e) {
            setMsg({ tipo: "erro", texto: e instanceof Error ? e.message : "Erro ao salvar" });
        }
    }

    return (
        <form onSubmit={salvar} className="max-w-xl space-y-3">
            <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Nome" required />
            <input value={cep} onChange={(e) => setCep(e.target.value)} onBlur={buscarCep}
                   placeholder="CEP" required />
            <input value={endereco.logradouro ?? ""} readOnly placeholder="Logradouro" />
            <input value={endereco.bairro ?? ""} readOnly placeholder="Bairro" />
            <input value={`${endereco.cidade ?? ""}${endereco.uf ? "/" + endereco.uf : ""}`}
                   readOnly placeholder="Cidade/UF" />
            <input value={numero} onChange={(e) => setNumero(e.target.value)} placeholder="Número" />
            <input value={complemento} onChange={(e) => setComplemento(e.target.value)} placeholder="Complemento" />
            <button type="submit">Salvar</button>
            {msg && <p className={msg.tipo === "erro" ? "text-red-400" : "text-verde-neon"}>{msg.texto}</p>}
        </form>
    );
}