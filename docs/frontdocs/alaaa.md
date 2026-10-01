front/app/
├── components/                  ← COMPONENTS COMPARTILHADOS (vários módulos usam)
│   ├── Capa.tsx                 ← capa com fallback (livros, home, autores…)
│   ├── Livro.tsx                ← o ÍCONE svg do livro (login, fallback da Capa…)
│   ├── Header.tsx
│   ├── Sidebar.tsx
│   └── Footer.tsx
│
└── (sistema)/
└── livros/
├── components/          ← COMPONENTS SÓ DO MÓDULO LIVROS
│   ├── LivroForm.tsx    ← formulário (novo + editar)
│   └── ExemplaresTable.tsx
├── novo/page.tsx
├── [id]/page.tsx
├── [id]/editar/page.tsx
└── page.tsx