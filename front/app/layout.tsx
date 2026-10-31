import type { Metadata } from "next";
import localFont from "next/font/local";
import "./globals.css";
import StoreProvider from "./components/StoreProvider";


const gotica = localFont({
  src: "./fonts/unifrakturcook-latin-700-normal.woff2",
  weight: "700",
  variable: "--font-gotica",
  display: "swap", // mostra texto com fonte fallback enquanto a fonte carrega
});

const texto = localFont({

  src: [
    { path: "./fonts/cormorant-garamond-latin-400-normal.woff2", weight: "400", style: "normal" },
    { path: "./fonts/cormorant-garamond-latin-600-normal.woff2", weight: "600", style: "normal" },
    { path: "./fonts/cormorant-garamond-latin-700-normal.woff2", weight: "700", style: "normal" },
  ],
  variable: "--font-texto",
  display: "swap",
});

export const metadata: Metadata = {
  title: "BiblioTech",
  description: "O grimório da sua biblioteca",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
      <html
          lang="pt-BR"
          className={`${gotica.variable} ${texto.variable}`}
          suppressHydrationWarning
          data-scroll-behavior="smooth"
      >
      <body suppressHydrationWarning className="bg-noite text-ouro-neon">
      <StoreProvider>{children}</StoreProvider>
      </body>
      </html>
  );
}