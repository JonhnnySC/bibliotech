import Link from "next/link";
import EsferaArmilar from "./components/EsferaArmilar";
import Livro from "./components/Livro";

const recursos = [
  { icone: "📖", titulo: "Acervo", texto: "Cadastre livros com capa, ISBN, edição e páginas, ligados aos seus autores." },
  { icone: "🔖", titulo: "Exemplares", texto: "Cada cópia física tem vida própria: disponível, emprestada, danificada ou perdida." },
  { icone: "👥", titulo: "Leitores", texto: "Controle quem pode retirar livros, quem está bloqueado e quem é bibliotecário." },
  { icone: "📜", titulo: "Empréstimos", texto: "Registre a saída, acompanhe o prazo de 14 dias e devolva com um clique." },
];

const capitulos = [
  { num: "I", titulo: "O pleroma", texto: "Antes das estantes, existia a plenitude: todo o saber orbitava em silêncio, preso em esferas de ouro e bronze. Cada livro era uma estrela pequena, acesa por quem ainda não havia nascido." },
  { num: "II", titulo: "As esferas", texto: "Os antigos mediam o céu com anéis de metal. Um anel para o caminho do sol, outro para o da lua, outro para o que não tem nome. No centro de tudo, onde ficaria a terra, eles puseram um livro." },
  { num: "III", titulo: "O guardião das chaves", texto: "Todo saber que sai de sua órbita precisa voltar. Por isso existe o guardião: ele anota quem levou o quê e quando a luz deve retornar. A BiblioTech é o caderno desse guardião." },
];

export default function Landing() {
  return (
    <>
      <div className="estrelas" />

      <nav className="sticky top-0 z-20 border-b border-ouro/40 bg-noite/80 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-3">
          <div className="flex items-center gap-2">
            <Livro className="h-8 w-8" />
            <span className="font-gotica text-2xl neon-ouro">BiblioTech</span>
          </div>
          <div className="hidden gap-8 text-lg sm:flex">
            <a href="#acervo" className="hover:neon-ouro">O que guarda</a>
            <a href="#historia" className="hover:neon-ouro">A história</a>
          </div>
          <Link href="/login" className="btn-neon font-gotica rounded-full px-5 py-1 text-xl">Entrar</Link>
        </div>
      </nav>

      <main className="mx-auto max-w-6xl px-6">
        {/* Hero */}
        <section className="flex flex-col items-center pt-10 text-center">
          <EsferaArmilar />
          <h1 className="font-gotica mt-4 text-6xl neon-ouro sm:text-8xl">BiblioTech</h1>
          <p className="mt-4 max-w-xl text-2xl italic neon-verde">
            O grimório onde cada livro encontra o seu leitor
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/login" className="btn-neon font-gotica rounded-full px-10 py-3 text-2xl">
              Entre além do pleroma
            </Link>
            <a href="#historia" className="rounded-full border border-verde-neon/60 px-8 py-3 text-xl text-verde-neon hover:bg-musgo">
              Ler a história
            </a>
          </div>
        </section>

        <div className="divisor"><span className="text-2xl">✦ ❖ ✦</span></div>

        {/* Recursos */}
        <section id="acervo" className="scroll-mt-24">
          <h2 className="font-gotica mb-2 text-center text-4xl neon-ouro sm:text-5xl">O que o grimório guarda</h2>
          <p className="mx-auto mb-10 max-w-xl text-center text-lg text-ouro-neon/80">
            Tudo o que uma biblioteca precisa para manter os livros em movimento.
          </p>
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {recursos.map((r) => (
              <article key={r.titulo} className="cartao borda-neon rounded-lg bg-marrom/70 p-6 text-center">
                <div className="mb-3 text-4xl">{r.icone}</div>
                <h3 className="font-gotica mb-2 text-2xl neon-verde">{r.titulo}</h3>
                <p className="text-lg leading-snug">{r.texto}</p>
              </article>
            ))}
          </div>
        </section>

        <div className="divisor"><span className="text-2xl">✦ ❖ ✦</span></div>

        {/* História */}
        <section id="historia" className="mx-auto max-w-3xl scroll-mt-24">
          <h2 className="font-gotica mb-10 text-center text-4xl neon-ouro sm:text-5xl">A história do pleroma</h2>
          <div className="space-y-8">
            {capitulos.map((c) => (
              <article key={c.num} className="borda-neon rounded-lg bg-marrom/60 p-7">
                <h3 className="font-gotica mb-3 text-3xl neon-verde">
                  <span className="text-ouro-neon">{c.num}.</span> {c.titulo}
                </h3>
                <p className="capitular text-xl leading-relaxed">{c.texto}</p>
              </article>
            ))}
          </div>
        </section>

        {/* Citação + CTA final */}
        <section className="my-20 text-center">
          <blockquote className="font-gotica mx-auto max-w-2xl text-3xl neon-ouro sm:text-4xl">
            Um livro fechado é só uma estrela apagada.
          </blockquote>
          <Link href="/login" className="btn-neon font-gotica mt-8 inline-block rounded-full px-12 py-3 text-2xl">
            Abrir o grimório
          </Link>
        </section>
      </main>

      <footer className="border-t border-ouro/40 bg-noite/80 py-6 text-center text-ouro">
        <div className="mb-1 flex items-center justify-center gap-2">
          <Livro className="h-6 w-6" />
          <span className="font-gotica text-xl neon-ouro">BiblioTech</span>
        </div>
        <p className="text-sm">© {new Date().getFullYear()} BiblioTech · o saber orbita, o leitor retorna</p>
      </footer>
    </>
  );
}
