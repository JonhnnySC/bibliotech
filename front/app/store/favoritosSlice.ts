// front/app/store/favoritosSlice.ts
import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import { logout } from "./authSlice";

interface FavoritosState { ids: number[] }
const initialState: FavoritosState = { ids: [] };

const favoritosSlice = createSlice({
    name: "favoritos",
    initialState,
    reducers: {
        adicionarFavorito(state, action: PayloadAction<number>) {
            if (!state.ids.includes(action.payload)) state.ids.push(action.payload);
        },
        removerFavorito(state, action: PayloadAction<number>) {
            state.ids = state.ids.filter((id) => id !== action.payload);
        },
        limparFavoritos: () => initialState,
    },
    // sem isto, o usuário B veria as estrelas do A após logout/login
    extraReducers: (b) => { b.addCase(logout, () => initialState); },
});

export const { adicionarFavorito, removerFavorito, limparFavoritos } = favoritosSlice.actions;
export default favoritosSlice.reducer;