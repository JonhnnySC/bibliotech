// imports
import { useAppDispatch, useAppSelector } from "@/app/store/hooks";
import { adicionarFavorito, removerFavorito, limparFavoritos } from "@/app/store/favoritosSlice";

// no topo do componente
const dispatch = useAppDispatch();
const favoritos = useAppSelector((s) => s.favoritos.ids);
const [soFavoritos, setSoFavoritos] = useState(false);

// 1) PRIMEIRA linha do filter que já existe
if (soFavoritos && !favoritos.includes(l.id)) return false;

// 2) acima da grade, junto da busca
<label className="mb-4 mr-4 inline-flex items-center gap-2 text-ouro-neon">
  <input type="checkbox" checked={soFavoritos}
         onChange={(e) => setSoFavoritos(e.target.checked)} />
  Só favoritos ({favoritos.length})
</label>
<button type="button" onClick={() => dispatch(limparFavoritos())}
        className="btn-neon rounded px-3 py-1">Limpar favoritos</button>

// 3) no .map — a estrela fica AO LADO do Link (botão dentro de <a> é HTML inválido)
{filtrados.map((l) => {
  const favorito = favoritos.includes(l.id);
  return (
      <div key={l.id} className="relative">
        <Link href={`/livros/${l.id}`} className="...classes que o Link já tem...">
          {/* card atual */}
        </Link>
        <button type="button"
                aria-label={favorito ? "Remover dos favoritos" : "Adicionar aos favoritos"}
                onClick={() => dispatch(favorito ? removerFavorito(l.id) : adicionarFavorito(l.id))}
                className="absolute right-2 top-2 z-10 text-3xl text-ouro-neon">
          {favorito ? "★" : "☆"}
        </button>
      </div>
  );
})}