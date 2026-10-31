import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

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
