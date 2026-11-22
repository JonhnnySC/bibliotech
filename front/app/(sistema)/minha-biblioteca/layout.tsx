import SomentePerfis from "@/app/components/SomentePerfis";

export default function Layout({ children }: { children: React.ReactNode }) {
    return <SomentePerfis perfis={["ADMINISTRADOR"]}>{children}</SomentePerfis>;
}