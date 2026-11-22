import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import { logout } from "./authSlice";

interface FavoritosState { ids: number[] }
const initialState: FavoritosState = { ids: [] };

const favoritosSlice = createSlice({
    name: "favoritos",
    initialState,
    reducers: {
        adicionarFavorito(state, action: PayloadAction<number>) {
            // Immer: "mutar" aqui é seguro, ele gera estado novo por baixo
            if (!state.ids.includes(action.payload)) state.ids.push(action.payload);
        },
        removerFavorito(state, action: PayloadAction<number>) {
            state.ids = state.ids.filter((id) => id !== action.payload);
        },
        limparFavoritos: () => initialState,
    },
    // ao sair, os favoritos somem: um usuário não herda os do anterior
    extraReducers: (builder) => {
        builder.addCase(logout, () => initialState);
    },
});

export const { adicionarFavorito, removerFavorito, limparFavoritos } = favoritosSlice.actions;
export default favoritosSlice.reducer;