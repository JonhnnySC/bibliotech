"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import Footer from "../components/Footer";

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const [aberta, setAberta] = useState(true);
  const [pronto, setPronto] = useState(false);

  // sem token → volta para o login
  useEffect(() => {
    if (!localStorage.getItem("token")) router.replace("/login");
    else setPronto(true);
  }, [router]);

  if (!pronto) return null;

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
