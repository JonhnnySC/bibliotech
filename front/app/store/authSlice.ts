import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: string;
  bibliotecaId: number | null;
}

export interface AuthState {
  token: string | null;
  expiraEm: string | null;
  usuario: Usuario | null;
}

const initialState: AuthState = { token: null, expiraEm: null, usuario: null };

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (_state, action: PayloadAction<AuthState>) => action.payload,
    logout: () => initialState,
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;