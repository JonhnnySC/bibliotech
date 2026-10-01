export const dataBR = (iso?: string | null) => (iso ? iso.split("-").reverse().join("/") : "—");

// dias até a devolução (negativo = atrasado)
export function diasRestantes(iso: string) {
  const [a, m, d] = iso.split("-").map(Number);
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  return Math.round((new Date(a, m - 1, d).getTime() - hoje.getTime()) / 86400000);
}
