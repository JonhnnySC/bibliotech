// Utilitários de capa: normaliza ISBN, monta URL Open Library e busca no Google Books.
import axios from "axios";

// Só o pedaço da resposta do Google Books que usamos (tipar tudo seria exagero).
type GoogleBooksResposta = {
  items?: { volumeInfo?: { imageLinks?: { thumbnail?: string; smallThumbnail?: string } } }[];
};

/** Remove tudo que não é dígito/X e padroniza maiúsculo. "978-85-3591-484-3" -> "9788535914843" */
export function normalizarIsbn(isbn: string): string {
  return isbn.replace(/[^0-9Xx]/g, "").toUpperCase();
}

/** URL canônica do Open Library SEM hífen (formato que eles indexam melhor). */
export function urlOpenLibrary(isbn: string): string {
  return `https://covers.openlibrary.org/b/isbn/${normalizarIsbn(isbn)}-M.jpg`;
}

// Cache em memória (por sessão) + localStorage (sobrevive reload) p/ não martelar a API
const memCache = new Map<string, string | null>();

function lerCacheLocal(isbn: string): string | null | undefined {
  if (typeof window === "undefined") return undefined;
  try {
    const v = localStorage.getItem("capa:" + isbn);
    return v === null ? undefined : v === "__none__" ? null : v;
  } catch {
    return undefined;
  }
}

function gravarCacheLocal(isbn: string, url: string | null) {
  if (typeof window === "undefined") return;
  try {
    localStorage.setItem("capa:" + isbn, url ?? "__none__");
  } catch {
    /* quota / modo privado: ignora */
  }
}

/** Melhora o thumbnail do Google (zoom maior, sem a borda "curl"). */
function turbinarThumb(t: string): string {
  return t.replace("zoom=1", "zoom=2").replace("&edge=curl", "");
}

/**
 * Busca a capa no Google Books pelo ISBN. Retorna a URL do thumbnail ou null.
 * A endpoint é pública e libera CORS, então roda direto do browser.
 */
export async function buscarCapaGoogleBooks(isbn: string): Promise<string | null> {
  const chave = normalizarIsbn(isbn);
  if (!chave) return null;

  if (memCache.has(chave)) return memCache.get(chave)!;
  const local = lerCacheLocal(chave);
  if (local !== undefined) {
    memCache.set(chave, local);
    return local;
  }

  try {
    // axios PURO (não a instância `api`/`http`): essa instância injeta o JWT do nosso
    // backend em toda requisição, e ele NÃO pode ser enviado para um domínio de terceiros.
    // O axios já rejeita status fora de 2xx, então não precisa do `if (!r.ok)` do fetch.
    // `params` monta e codifica a query string; `timeout` evita travar a tela se o Google demorar.
    const { data: json } = await axios.get<GoogleBooksResposta>(
      "https://www.googleapis.com/books/v1/volumes",
      { params: { q: `isbn:${chave}` }, timeout: 8000 },
    );
    const thumb: string | undefined =
      json?.items?.[0]?.volumeInfo?.imageLinks?.thumbnail ??
      json?.items?.[0]?.volumeInfo?.imageLinks?.smallThumbnail;

    const url = thumb ? turbinarThumb(thumb) : null;
    memCache.set(chave, url);
    gravarCacheLocal(chave, url);
    return url;
  } catch {
    memCache.set(chave, null);
    gravarCacheLocal(chave, null);
    return null;
  }
}