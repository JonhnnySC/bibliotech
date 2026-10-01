import type { Metadata } from "next";
import { UnifrakturCook, Cormorant_Garamond } from "next/font/google";
import "./globals.css";

const gotica = UnifrakturCook({ weight: "700", subsets: ["latin"], variable: "--font-gotica" });
const texto = Cormorant_Garamond({ weight: ["400", "600", "700"], subsets: ["latin"], variable: "--font-texto" });

export const metadata: Metadata = {
  title: "BiblioTech",
  description: "O grimório da sua biblioteca",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR" className={`${gotica.variable} ${texto.variable}`}>
      <body>{children}</body>
    </html>
  );
}
