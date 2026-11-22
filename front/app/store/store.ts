import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
import favoritosReducer from "./favoritosSlice";
import { configurarApi } from "../services/api";

export const store = configureStore({
  reducer: { auth: authReducer, favoritos: favoritosReducer },
});

configurarApi(store);   // sem isto, storeRef fica undefined e TODA requisição quebra

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;