import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

// Slice = uma "gaveta" do estado global. Esta cuida só de quem está logado (Aula 19.10).
interface AuthState {
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
    // Recarregou a página: o estado do Redux some, então reaproveita o que está no cookie
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

export const { sessaoIniciada, sessaoRestaurada, sessaoEncerrada } = authSlice.actions;
export default authSlice.reducer;
