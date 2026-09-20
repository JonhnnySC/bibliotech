import Sidebar from "./Sidebar";
import Header from "./Header";

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen bg-gradient-to-br from-[#0f1f16] via-[#1a2e1f] to-[#2a1f14]">
      <Sidebar />
      <div className="flex flex-col flex-1 ml-72">
        <Header />
        <main className="flex-1 p-8 overflow-y-auto">
          <div className="p-8 bg-[#1a2e1f]/40 backdrop-blur-sm rounded-2xl border border-[#d4a574]/30 shadow-2xl min-h-[600px]">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}