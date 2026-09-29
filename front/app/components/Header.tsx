const IconeSair = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className={className}>
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
    <polyline points="16 17 21 12 16 7" />
    <line x1="21" y1="12" x2="9" y2="12" />
  </svg>
);

export default function Header() {
  return (
    <header className="flex items-center justify-between px-8 py-5 bg-[#0f1f16]/60 backdrop-blur-sm border-b border-[#d4a574]/30 sticky top-0 z-30">
      <div className="flex items-center gap-3">
        <h2 className="text-xl text-[#f5e6d3] font-['Cinzel'] tracking-widest">
          Painel de Controle
        </h2>
      </div>

      <div className="flex items-center gap-6">
        <div className="flex items-center gap-3 px-5 py-2.5 bg-[#1a2e1f] rounded-lg border border-[#d4a574]/50">
          <div className="flex items-center justify-center w-9 h-9 rounded-full bg-gradient-to-br from-[#1a3d2e] to-[#0f1f16] text-[#e8c97a] font-['UnifrakturMaguntia'] text-xl border border-[#d4a574]/50">
            A
          </div>
          <span className="text-[#f5e6d3] font-['Cinzel'] text-sm tracking-wide">
            Administrador
          </span>
        </div>

        <button className="flex items-center gap-2 px-4 py-2.5 text-[#f5e6d3]/80 transition-all duration-300 rounded-lg bg-[#1a2e1f] border border-[#d4a574]/40 hover:bg-[#2a1f14] hover:text-[#e8c97a] hover:border-[#d4a574]/60">
          <IconeSair className="w-4 h-4" />
          <span className="font-['Cinzel'] text-sm">Sair</span>
        </button>
      </div>
    </header>
  );
}