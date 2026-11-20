
//login response pra resposta do login bruh

export interface UsuarioLogado {
    id: number;
    nome: string;
    email: string;
    perfil: string;
    bibliotecaId: number | null ;  // pode ser um numero ou nullo 
}

export interface LoginResponse {
    token: string;
    tipo: string;
    expiraEm: string; 
    usuario: UsuarioLogado; 
}




