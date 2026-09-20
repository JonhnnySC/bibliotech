import Link from "next/link";

export default function Dashboard() {
  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-['Cinzel'] font-bold text-[#f5e6d3] tracking-wide">
            Bem-vindo à Bibliotech
          </h1>
          <p className="text-[#f5e6d3]/60 font-['Cormorant_Garamond'] italic text-lg mt-2">
            Gerencie seu acervo e empréstimos com facilidade.
          </p>
        </div>
        <Link
          href="/livros"
          className="px-8 py-4 bg-gradient-to-r from-[#d4a574] to-[#e8c97a] text-[#0f1f16] font-['Cinzel'] font-bold rounded-lg border-2 border-[#e8c97a] hover:shadow-[0_0_30px_rgba(232,201,122,0.5)] transition-all duration-300 transform hover:-translate-y-1 animate-golden-aura"
        >
          Quero Conhecimento
        </Link>
      </div>

      <div className="grid md:grid-cols-4 gap-6">
        {[
          { title: "Livros no Acervo", value: "1.247", cor: "from-[#1a3d2e] to-[#0f1f16]" },
          { title: "Leitores Ativos", value: "342", cor: "from-[#2a1f14] to-[#1a1210]" },
          { title: "Empréstimos Ativos", value: "89", cor: "from-[#1a2e1f] to-[#0f1f16]" },
          { title: "Devoluções Pendentes", value: "12", cor: "from-[#1f2a1a] to-[#0f1f16]" },
        ].map((stat, i) => (
          <div
            key={i}
            className={`p-6 bg-gradient-to-br ${stat.cor} rounded-lg border border-[#d4a574]/20 hover:border-[#e8c97a]/40 transition-all duration-300 group`}
          >
            <h3 className="text-[#f5e6d3]/60 font-['Cinzel'] text-sm uppercase tracking-widest mb-2">
              {stat.title}
            </h3>
            <p className="text-4xl font-['UnifrakturMaguntia'] text-[#e8c97a] group-hover:drop-shadow-[0_0_10px_rgba(232,201,122,0.5)] transition-all">
              {stat.value}
            </p>
          </div>
        ))}
      </div>

      <div className="grid md:grid-cols-2 gap-6 mt-8">
        <div className="p-6 bg-[#0f1f16]/50 rounded-lg border border-[#d4a574]/20">
          <h3 className="text-xl font-['Cinzel'] text-[#e8c97a] mb-4">Últimos Livros Adicionados</h3>
          <ul className="space-y-3">
            {["O Nome da Rosa - Umberto Eco", "1984 - George Orwell", "Dom Quixote - Cervantes"].map((livro, i) => (
              <li key={i} className="flex items-center gap-3 text-[#f5e6d3]/70">
                <span className="w-2 h-2 rounded-full bg-[#d4a574]"></span>
                {livro}
              </li>
            ))}
          </ul>
        </div>

        <div className="p-6 bg-[#0f1f16]/50 rounded-lg border border-[#d4a574]/20">
          <h3 className="text-xl font-['Cinzel'] text-[#e8c97a] mb-4">Empréstimos Recentes</h3>
          <ul className="space-y-3">
            {["João Silva", "Maria Santos", "Pedro Costa"].map((nome, i) => (
              <li key={i} className="flex items-center gap-3 text-[#f5e6d3]/70">
                <span className="w-2 h-2 rounded-full bg-[#e8c97a]"></span>
                {nome}
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}