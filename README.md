# TinyPrice - Android + Supabase + TinyFish

Este proyecto ha sido generado con toda la arquitectura y código del codelab:
- **Ubicación:** `C:\Users\Admin\Desktop\IATinyFish`
- **Supabase Project:** `https://jaffbvxsqzllpjikfdxt.supabase.co`
- **Edge Function:** `search-products`

---

## 1. Despliegue de la Edge Function en Supabase (Único paso restante en la nube)

Tu API Key de TinyFish ya fue validada contra `api.search.tinyfish.ai` y responde con éxito.

Para activar tu Edge Function en Supabase Cloud, tienes 2 alternativas:

### Opción A (Desde la terminal de la carpeta `IATinyFish`):
1. Inicia sesión en Supabase CLI:
   ```shell
   npx supabase login
   ```
   *(Se abrirá tu navegador para autorizar la CLI).*

2. Vincula el proyecto:
   ```shell
   npx supabase link --project-ref jaffbvxsqzllpjikfdxt
   ```

3. Carga el secreto de TinyFish:
   ```shell
   npx supabase secrets set --env-file supabase/.env.production.local
   ```

4. Despliega la función:
   ```shell
   npx supabase functions deploy search-products --no-verify-jwt
   ```

---

### Opción B (Desde el Dashboard Web de Supabase):
1. Entra a tu proyecto en [Supabase Dashboard](https://supabase.com/dashboard/project/jaffbvxsqzllpjikfdxt).
2. Ve a **Edge Functions** -> **Secrets**.
   - Haz clic en **Add new secret**.
   - Nombre: `TINYFISH_API_KEY`
   - Valor: `sk-tinyfish-7bVgZMhTgxheSy-xiWAoXhqMNoM2ALkl`
3. En **Edge Functions** -> despliega la función `search-products` pegando el contenido del archivo:
   `supabase/functions/search-products/index.ts`
   *(Desactiva la verificación JWT: "Enforce JWT Verification: False")*.

---

## 2. Abrir y Ejecutar en Android Studio

1. Abre **Android Studio**.
2. Selecciona **Open** (o **File > Open**) y elige la carpeta:
   `C:\Users\Admin\Desktop\IATinyFish`
3. Espera a que termine la sincronización de Gradle.
4. Conecta tu teléfono o inicia un emulador Android (API 23+).
5. Haz clic en el botón verde **Run (▶)**.
