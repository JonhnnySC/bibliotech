"use client";
import { useEffect, useState } from "react";
import Livro from "./Livro";
import { urlOpenLibrary, buscarCapaGoogleBooks } from "../lib/capas";

type Props = {
  isbn?: string | null;
  url?: string | null;   // capaUrl vinda do banco (usada só se não houver isbn)
  titulo: string;
  className?: string;
};

export default function Capa({ isbn, url, titulo, className = "" }: Props) {
  const [src, setSrc] = useState<string | null>(null);
  const [modo, setModo] = useState<"img" | "icone">("icone");

  useEffect(() => {
    let vivo = true;

    // Plano A: Open Library montado a partir do ISBN (sem hífen). Sem ISBN, usa a url do banco crua.
    const candidata = isbn ? urlOpenLibrary(isbn) : (url ?? null);
    if (!candidata) return; // nada p/ tentar -> fica no ícone

    const img = new Image();
    
    img.onload = () => {
      if (!vivo) return;
      // Open Library devolve gif 1x1 (placeholder) quando não tem capa -> naturalWidth minúsculo
      if (img.naturalWidth >= 50) {
        setSrc(candidata);
        setModo("img");
      } else {
        tentarGoogle(); // Imagem muito pequena, tenta o Plano B
      }
    };
    
    img.onerror = () => {
      if (vivo) tentarGoogle(); // Falhou, tenta o Plano B
    };
    
    img.src = candidata;

    // Plano B: Busca no Google Books
    async function tentarGoogle() {
      if (!isbn) {
        if (vivo) setModo("icone");
        return;
      }
      const g = await buscarCapaGoogleBooks(isbn); 
      if (!vivo) return;
      
      if (g) {
        setSrc(g);
        setModo("img");
      } else {
        setModo("icone"); // Plano C: Nada funcionou, mostra o ícone
      }
    }

    return () => {
      vivo = false;
    };
  }, [isbn, url]);

  return (
    <div className={`flex items-center justify-center bg-noite/70 overflow-hidden ${className}`}>
      {modo === "img" && src ? (
        // eslint-disable-next-line @next/next/no-img-element
        <img
          key={src} // remonta quando trocamos OL -> Google, garantindo onLoad/onError do novo src
          src={src}
          alt={titulo}
          onError={() => setModo("icone")} // Até o Google quebrou? cai no ícone
          className="h-full w-full object-cover"
          crossOrigin="anonymous"
        />
      ) : (
        <div className="flex flex-col items-center gap-2 p-2">
          <Livro className="h-14 w-14 opacity-70" />
          <span className="text-[10px] text-ouro/60 text-center">Sem capa</span>
        </div>
      )}
    </div>
  );
}