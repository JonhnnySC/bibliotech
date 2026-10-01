// Formata data para DD/MM/AAAA
export function dataBR(iso: string | null | undefined): string {
  if (!iso) return "—";
  return iso.split("-").reverse().join("/");
}

// Dias até a devolução (negativo = atrasado)
export function diasRestantes(iso: string | null | undefined): number {
  if (!iso) return 0;
  
  const [a, m, d] = iso.split("-").map(Number);
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  return Math.round((new Date(a, m - 1, d).getTime() - hoje.getTime()) / 86400000);
}