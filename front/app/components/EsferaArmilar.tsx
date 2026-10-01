"use client";
// Só importamos o que o componente realmente usa.
// Sem useState/useEffect/api: este componente é puramente visual,
// então não deve buscar dados nem depender de rota ([id]).
import Livro from "./Livro";

// `export default` aqui: é isso que a home (app/page.tsx) importa.
export default function EsferaArmilar() {
  const texto = "✦ LIBER ✦ SCIENTIA ✦ LUX ✦ ARCANUM ✦ MEMORIA ✦ VERITAS ";
  return (
    <div className="relative mx-auto h-80 w-80 sm:h-[26rem] sm:w-[26rem]">
      <div className="aura" />
      <svg viewBox="0 0 200 200" className="runas absolute inset-0 h-full w-full opacity-70">
        <defs><path id="circ" d="M100,100 m-92,0 a92,92 0 1,1 184,0 a92,92 0 1,1 -184,0" /></defs>
        <text fontSize="9" fill="#c99a2e" letterSpacing="3" style={{ filter: "drop-shadow(0 0 3px #ffd24a)" }} suppressHydrationWarning>
          <textPath href="#circ">{texto}</textPath>
        </text>
      </svg>
      <div className="cena absolute inset-[12%]">
        <div className="esfera relative h-full w-full">
          <div className="anel" />
          <div className="anel verde" style={{ transform: "rotateY(60deg)" }} />
          <div className="anel" style={{ transform: "rotateY(120deg)" }} />
          <div className="anel" style={{ transform: "rotateX(90deg) scale(.98)" }} />
          <div className="anel verde" style={{ transform: "rotateX(90deg) rotateY(25deg) scale(.7)" }} />
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="livro-giro">
              <Livro className="h-24 w-24 sm:h-32 sm:w-32" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}