"use client";
import { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import Footer from "../components/Footer";
import TelaLogin from "../components/TelaLogin";
import { useAppSelector } from "../store/hooks";

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  const token = useAppSelector((s) => s.auth.token);
  const [aberta, setAberta] = useState(true); // hooks antesdo return condiciona

  if (!token) return <TelaLogin />;

  return (
    <div className="flex min-h-screen flex-col">
      <Header onToggle={() => setAberta(!aberta)} />
      <div className="flex flex-1">
        <Sidebar aberta={aberta} />
        <main className="flex-1 p-6">{children}</main>
      </div>
      <Footer />
    </div>
  );
}