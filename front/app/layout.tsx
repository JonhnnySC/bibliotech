import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Bibliotech | Acesso ao Conhecimento",
  description: "Sistema de gestão de biblioteca - acesso fácil e acessível ao conhecimento",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pt-BR">
      <head>
        <link
          href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;800&family=UnifrakturMaguntia&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&display=swap"
          rel="stylesheet"
        />
      </head>
      <body className="antialiased min-h-screen">
        {children}
      </body>
    </html>
  );
}