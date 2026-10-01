// Ícone de livro aberto (usado no logo e no centro da esfera)
export default function Livro({ className = "w-8 h-8" }: { className?: string }) {
  return (
    <svg viewBox="0 0 64 64" className={className} fill="none" stroke="#ffd24a" strokeWidth="3" strokeLinejoin="round"
      style={{ filter: "drop-shadow(0 0 6px #ffd24a)" }}>
      <path d="M6 14c10-3 19-2 26 3v34c-7-5-16-6-26-3z" fill="#2a1a10" />
      <path d="M58 14c-10-3-19-2-26 3v34c7-5 16-6 26-3z" fill="#1c3322" />
      <path d="M32 24l2.5 5 5.5.8-4 3.9.9 5.5-4.9-2.6-4.9 2.6.9-5.5-4-3.9 5.5-.8z" fill="#ffd24a" stroke="none" />
    </svg>
  );
}
