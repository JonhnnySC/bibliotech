"use client";
import { Provider } from "react-redux";
import { store } from "../store/store";
import ExpiracaoDoToken from "./ExpiracaoToken";

export default function StoreProvider({ children }: { children: React.ReactNode }) {
  // sem restauração de cookie recarregar a página desloga 
  return (
    <Provider store={store}>
      <ExpiracaoDoToken />
      {children}
    </Provider>
  );
}