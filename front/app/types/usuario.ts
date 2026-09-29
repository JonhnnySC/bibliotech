export interface Usuario {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  status: 'ATIVO' | 'INATIVO';
  perfil?: 'ADMIN' | 'LEITOR' | 'BIBLIOTECARIO';
}