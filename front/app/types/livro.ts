export interface Livro {
  id: number;
  titulo: string;
  isbn: string;
  anoPublicacao: number;
  genero: 'FICCAO' | 'NAO_FICCAO' | 'TECNICO' | 'LITERATURA' | 'OUTRO';
  autor: Autor;
  editora: Editora;
  exemplares: Exemplar[];
}

export interface Autor {
  id: number;
  nome: string;
  nacionalidade: string;
}

export interface Editora {
  id: number;
  nome: string;
}

export interface Exemplar {
  id: number;
  numeroTombamento: string;
  status: 'DISPONIVEL' | 'EMPRESTADO' | 'MANUTENCAO' | 'PERDIDO';
  livro: Livro;
}

export interface Emprestimo {
  id: number;
  dataEmprestimo: string;
  dataDevolucaoPrevista: string;
  dataDevolucaoReal?: string;
  status: 'ATIVO' | 'DEVOLVIDO' | 'ATRASADO';
  exemplar: Exemplar;
  leitor: Leitor;
}

export interface Leitor {
  id: number;
  nome: string;
  email: string;
  cpf: string;
}