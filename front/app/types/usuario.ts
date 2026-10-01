export type Perfil = "LEITOR" | "BIBLIOTECARIO" | "ADMIN";
export type StatusUsuario = "ATIVO" | "BLOQUEADO" | "INATIVO" | "EXCLUIDO";
export type StatusExemplar = "DISPONIVEL" | "EMPRESTADO" | "DANIFICADO" | "PERDIDO";
export interface Usuario { id: number; nome: string; email: string; cpf?: string; perfil: Perfil; status: StatusUsuario }
export interface Exemplar {
  id: number; livroId: number; statusExemplar: StatusExemplar; capaDura: boolean;
  dataImpressao?: string; dataCompra?: string;
}
export interface Emprestimo {
  id: number; leitor: Usuario; exemplar: Exemplar;
  dataEmprestimo: string; dataPrevistaDevolucao: string; dataDevolucao: string | null;
  statusEmprestimo: "ATIVO" | "DEVOLVIDO" | "ATRASADO";
}
export interface Livro {
  id: number; volume: string; isbn: string; paginas: number; capaUrl?: string | null;
  descricao?: string; edicao?: string; tipo?: string; dataLancamento?: string;
}
