import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

// codigo modifgicado pra a stroe nascer vazia, que faz deslogar por isso
//parece que isso que segura tudo



expórt interface Usuario {
  export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: string;
  bibliotecaId: number | null;
}

export interface AuthState {
  token: string | null  // aceita ou o string do token ou nennhum
  expiraEm: string | null;
  usuario: Usuario | null;
}
const initialState: AuthState = { token : null, expiraEm: null, usuario: null }; // tudo null é (ngn logado) vazia de inicio


// 
const authSlice = createSlice({
  name: "auth", 
  initialState,
  reducers: {
    login: (_state, action: PayloadAction<AuthState>) => action.payload, 
    logout: () => initialState,

  },
});
}
















/*interface AuthState {
  token: string | null;
  email: string | null;
}

const initialState: AuthState = { token: null, email: null };

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    sessaoIniciada(state, action: PayloadAction<{ token: string; email: string }>) {
      state.token = action.payload.token;
      state.email = action.payload.email;
    },
    sessaoRestaurada(state, action: PayloadAction<{ token: string | null; email: string | null }>) {
      state.token = action.payload.token;
      state.email = action.payload.email;
    },
    sessaoEncerrada(state) {
      state.token = null;
      state.email = null;
    },
  },
});

*/

export const { sessaoIniciada, sessaoRestaurada, sessaoEncerrada } = authSlice.actions;
export default authSlice.reducer;
