/**
 * Validações de domínio reutilizáveis (front).
 * cpfValido replica a regra do backend (dígito verificador),
 * para dar feedback antes mesmo de chamar a API.
 */
export function cpfValido(cpf: string): boolean {
  const c = cpf.replace(/\D/g, "");
  if (c.length !== 11 || /^(\d)\1{10}$/.test(c)) return false; // repele dígitos repetidos

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += Number(c[i]) * (10 - i);
  let d1 = (soma * 10) % 11; if (d1 === 10) d1 = 0;
  if (d1 !== Number(c[9])) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += Number(c[i]) * (11 - i);
  let d2 = (soma * 10) % 11; if (d2 === 10) d2 = 0;
  return d2 === Number(c[10]);
}