"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Livro from "./Livro";

export default function Header({ onToggle }: { onToggle: () => void }) {
  const router = useRouter();
  const [email, setEmail] = useState("");
  useEffect(() => setEmail(localStorage.getItem("email") ?? ""), []);

  function sair() {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    router.push("/login");
  }

  return (
    <header className="flex items-center justify-between border-b border-ouro bg-noite/90 px-4 py-3 shadow-[0_2px_16px_#c99a2e55]">
      <div className="flex items-center gap-3">
        <button onClick={onToggle} aria-label="Alternar menu" className="text-3xl text-ouro-neon hover:text-verde-neon">☰</button>
        <Livro className="h-8 w-8" />
        <span className="font-gotica text-2xl neon-ouro">BiblioTech</span>
      </div>
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-full border border-verde-neon bg-musgo text-lg neon-verde">
          {email ? email[0].toUpperCase() : "?"}
        </div>
        <span className="hidden text-sm sm:block">{email}</span>
        <button onClick={sair} className="rounded border border-ouro px-3 py-1 text-ouro-neon hover:bg-ouro hover:text-noite">Sair</button>
      </div>
    </header>
  );
}
