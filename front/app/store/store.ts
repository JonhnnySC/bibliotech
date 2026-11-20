import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
import { configurarApi } from "../services/api";

export const store = configureStore({
  reducer: { auth: authReducer },
});

configurarApi(store) // adicionado

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
