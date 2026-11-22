"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAppSelector } from "../store/hooks";

type Item = { href: string; icone: string; nome: string; perfis?: string[] };

const itens: Item[] = [
  { href: "/home", icone: "🏛", nome: "Início" },
  { href: "/usuarios", icone: "👥", nome: "Usuários", perfis: ["ADMINISTRADOR", "BIBLIOTECARIO"] },
  { href: "/livros", icone: "📖", nome: "Livros" },
  { href: "/autores", icone: "🖋", nome: "Autores" },
  { href: "/emprestimos", icone: "📜", nome: "Empréstimos" },
  { href: "/minha-biblioteca", icone: "🏰", nome: "Minha biblioteca", perfis: ["ADMINISTRADOR"] },
];

export default function Sidebar({ aberta }: { aberta: boolean }) {
  const path = usePathname();
  const perfil = useAppSelector((s) => s.auth.usuario?.perfil);
  const visiveis = itens.filter((i) => !i.perfis || (perfil && i.perfis.includes(perfil)));

  return (
      <aside className={`${aberta ? "w-56" : "w-16"} shrink-0 border-r border-ouro bg-marrom/80 transition-all duration-300`}>
        <nav className="flex flex-col gap-1 p-2">
          {visiveis.map((i) => {
            const ativo = path.startsWith(i.href);
            return (
                <Link key={i.href} href={i.href} title={i.nome}
                      className={`flex items-center gap-3 rounded px-3 py-2 ${ativo ? "borda-neon bg-musgo neon-ouro" : "hover:bg-marrom-claro"}`}>
                  <span className="text-xl">{i.icone}</span>
                  {aberta && <span>{i.nome}</span>}
                </Link>
            );
          })}
        </nav>
      </aside>
  );
}