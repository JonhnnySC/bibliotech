export interface Autor {
  id: number;
  nome: string;
  nacionalidade?: string;
  dataNascimento?: string;
  fotoUrl?: string;
  livros: { id: number; volume: string; isbn?: string }[];
}

export interface AutorForm {
  nome: string;
  nacionalidade: string;
  dataNascimento: string;
  fotoUrl: string;
  livroIds: number[];
}