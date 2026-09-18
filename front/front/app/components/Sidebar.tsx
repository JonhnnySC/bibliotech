"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const IconeLivro = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className={className}>
    <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
    <path d="M8 7h8M8 11h6" />
  </svg>
);

const IconeDashboard = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className={className}>
    <rect x="3" y="3" width="7" height="7" />
    <rect x="14" y="3" width="7" height="7" />
    <rect x="14" y="14" width="7" height="7" />
    <rect x="3" y="14" width="7" height="7" />
  </svg>
);

const IconeUsuarios = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className={className}>
    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
    <circle cx="9" cy="7" r="4" />
    <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
    <path d="M16 3.13a4 4 0 0 1 0 7.75" />
  </svg>
);

const IconeEmprestimo = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className={className}>
    <path d="M12 2L2 7l10 5 10-5-10-5z" />
    <path d="M2 17l10 5 10-5" />
    <path d="M2 12l10 5 10-5" />
  </svg>
);

const menuItems = [
  { nome: "Dashboard", href: "/dashboard", icone: IconeDashboard },
  { nome: "Leitores", href: "/usuarios", icone: IconeUsuarios },
  { nome: "Acervo", href: "/livros", icone: IconeLivro },
  { nome: "Empréstimos", href: "/emprestimos", icone: IconeEmprestimo },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="flex flex-col w-72 h-screen bg-[#0f1f16]/90 backdrop-blur-md border-r-2 border-[#d4a574]/40 shadow-[4px_0_30px_rgba(0,0,0,0.5)] fixed left-0 top-0 z-40">
      <div className="flex items-center gap-3 p-6 border-b border-[#d4a574]/40 bg-gradient-to-r from-[#1a3d2e] to-[#0f1f16]">
        <div className="w-12 h-12 rounded-full border-2 border-[#d4a574] animate-golden-aura flex items-center justify-center">
          <span className="font-['UnifrakturMaguntia'] text-[#e8c97a] text-2xl">B</span>
        </div>
        <h1 className="text-2xl text-[#f5e6d3] font-['UnifrakturMaguntia'] tracking-wider">
          Bibliotech
        </h1>
      </div>

      <nav className="flex-1 px-4 py-8 space-y-3">
        {menuItems.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.nome}
              href={item.href}
              className={`flex items-center gap-4 px-5 py-3 rounded-lg transition-all duration-300 group border ${
                isActive
                  ? "bg-[#1a3d2e]/60 border-[#d4a574]/60 text-[#e8c97a] shadow-[0_0_15px_rgba(212,165,116,0.2)]"
                  : "border-transparent text-[#f5e6d3]/60 hover:bg-[#1a2e1f] hover:text-[#f5e6d3] hover:border-[#d4a574]/30"
              }`}
            >
              <item.icone
                className={`w-5 h-5 transition-all duration-300 ${
                  isActive ? "text-[#e8c97a] drop-shadow-[0_0_8px_rgba(232,201,122,0.8)]" : "group-hover:text-[#d4a574]"
                }`}
              />
              <span className="text-lg font-['Cinzel'] font-semibold tracking-wide">{item.nome}</span>
            </Link>
          );
        })}
      </nav>

      <div className="p-6 border-t border-[#d4a574]/40 text-center">
        <p className="text-xs text-[#d4a574]/80 font-['Cormorant_Garamond'] italic">
          "O conhecimento liberta."
        </p>
      </div>
    </aside>
  );
}