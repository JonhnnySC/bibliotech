"use client";

import { useEffect, useState } from "react";
import { api } from "@/app/services/api";

const campo = "w-full rounded border border-ouro bg-noite/80 px-3 py-2 outline-none focus:border-verde-neon focus:shadow-[0_0_10px_#3dff8f]";
const somenteLeitura = "w-full rounded border border-ouro/40 bg-noite/40 px-3 py-2 text-ouro/70";

type Endereco = { logradouro?: string; bairro?: string; cidade?: string; uf?: string };

export default function MinhaBiblioteca() {
    const [nome, setNome] = useState("");
    const [cep, setCep] = useState("");
    const [numero, setNumero] = useState("");
    const [complemento, setComplemento] = useState("");
    const [endereco, setEndereco] = useState<Endereco>({});
    const [msg, setMsg] = useState<{ tipo: "ok" | "erro"; texto: string } | null>(null);
    const [salvando, setSalvando] = useState(false);

    useEffect(() => {
        let vivo = true;
        // BibliotecaResponse usa "cidade".
        api.get("/bibliotecas/minha-biblioteca")
            .then(({ data }) => {
                if (!vivo) return;
                setNome(data.nome ?? "");
                setCep(data.cep ?? "");
                setNumero(data.numero ?? "");
                setComplemento(data.complemento ?? "");
                setEndereco({
                    logradouro: data.logradouro, bairro: data.bairro,
                    cidade: data.cidade, uf: data.uf,
                });
            })
            .catch((e) => {
                if (vivo) setMsg({ tipo: "erro", texto: e instanceof Error ? e.message : "Não foi possível carregar a biblioteca" });
            });
        return () => { vivo = false; };
    }, []);

    async function buscarCep() {
        const soDigitos = cep.replace(/\D/g, "");
        if (soDigitos.length !== 8) return;   // "123" não dispara chamada nenhuma
        try {
            const { data } = await api.get(`/bibliotecas/cep/${soDigitos}`);
            // EnderecoCep (ViaCEP) usa "localidade", não "cidade".
            setEndereco({
                logradouro: data.logradouro, bairro: data.bairro,
                cidade: data.localidade, uf: data.uf,
            });
            setMsg(null);
        } catch (e) {
            setEndereco({});
            setMsg({ tipo: "erro", texto: e instanceof Error ? e.message : "Não foi possível consultar o CEP" });
        }
    }

    async function salvar(ev: React.FormEvent) {
        ev.preventDefault();
        setSalvando(true);
        try {
            const { data } = await api.put("/bibliotecas/minha-biblioteca", {
                nome, cep: cep.replace(/\D/g, ""), numero, complemento,
            });
            // O back reconsultou o ViaCEP: mostra o que foi gravado de verdade.
            setEndereco({
                logradouro: data.logradouro, bairro: data.bairro,
                cidade: data.cidade, uf: data.uf,
            });
            setMsg({ tipo: "ok", texto: "Biblioteca atualizada" });
        } catch (e) {
            setMsg({ tipo: "erro", texto: e instanceof Error ? e.message : "Erro ao salvar" });
        } finally {
            setSalvando(false);
        }
    }

    return (
        <div className="mx-auto max-w-2xl">
            <h1 className="font-gotica mb-2 text-4xl neon-ouro">Minha biblioteca</h1>
            <p className="mb-6 text-lg italic text-verde-neon/90">
                Informe o CEP e o endereço é preenchido pela API externa.
            </p>

            <form onSubmit={salvar} className="borda-neon space-y-4 rounded-lg bg-marrom/70 p-6">
                <div>
                    <label className="mb-1 block text-ouro-neon">Nome *</label>
                    <input value={nome} onChange={(e) => setNome(e.target.value)} className={campo} required />
                </div>

                <div>
                    <label className="mb-1 block text-ouro-neon">CEP *</label>
                    <input value={cep} onChange={(e) => setCep(e.target.value)} onBlur={buscarCep}
                           className={campo} inputMode="numeric" placeholder="88801000" required />
                    <p className="mt-1 text-xs text-ouro/60">O endereço carrega ao sair do campo.</p>
                </div>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <label className="mb-1 block text-ouro-neon">Logradouro</label>
                        <input value={endereco.logradouro ?? ""} readOnly className={somenteLeitura} />
                    </div>
                    <div>
                        <label className="mb-1 block text-ouro-neon">Bairro</label>
                        <input value={endereco.bairro ?? ""} readOnly className={somenteLeitura} />
                    </div>
                    <div>
                        <label className="mb-1 block text-ouro-neon">Cidade/UF</label>
                        <input value={`${endereco.cidade ?? ""}${endereco.uf ? "/" + endereco.uf : ""}`}
                               readOnly className={somenteLeitura} />
                    </div>
                    <div>
                        <label className="mb-1 block text-ouro-neon">Número</label>
                        <input value={numero} onChange={(e) => setNumero(e.target.value)} className={campo} />
                    </div>
                </div>

                <div>
                    <label className="mb-1 block text-ouro-neon">Complemento</label>
                    <input value={complemento} onChange={(e) => setComplemento(e.target.value)} className={campo} />
                </div>

                {msg && (
                    <p className={msg.tipo === "erro" ? "text-red-400" : "text-verde-neon"}>{msg.texto}</p>
                )}

                <div className="flex justify-end">
                    <button type="submit" disabled={salvando}
                            className="btn-neon font-gotica rounded-full px-6 py-2 text-xl disabled:opacity-50">
                        {salvando ? "Salvando..." : "Salvar"}
                    </button>
                </div>
            </form>
        </div>
    );
}