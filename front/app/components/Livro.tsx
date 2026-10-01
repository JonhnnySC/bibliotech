// app/components/Livro.tsx
type Props = { className?: string };

/**
 * Ícone de livro (SVG) usado no login, header e no fallback de capa.
 * suppressHydrationWarning silencia o mismatch causado por extensões
 * de tema escuro (Dark Reader) que injetam estilos no SVG antes da hidratação.
 */
export default function Livro({ className = "" }: Props) {
  return (
    <svg
      viewBox="0 0 64 64"
      className={className}
      fill="none"
      stroke="#ffd24a"
      strokeWidth="3"
      strokeLinejoin="round"
      style={{ filter: "drop-shadow(0 0 6px #ffd24a)" }}
      suppressHydrationWarning={true}
    >
      <path d="M6 14c10-3 19-2 26 3v34c-7-5-16-6-26-3z" fill="#2a1a10" suppressHydrationWarning={true} />
      <path d="M58 14c-10-3-19-2-26 3v34c7-5 16-6 26-3z" fill="#1c3322" suppressHydrationWarning={true} />
      <path d="M32 24l2.5 5 5.5.8-4 3.9.9 5.5-4.9-2.6-4.9 2.6.9-5.5-4-3.9 5.5-.8z" fill="#ffd24a" stroke="none" suppressHydrationWarning={true} />
    </svg>
  );
}