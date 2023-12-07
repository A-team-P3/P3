//import { vitePreprocess } from "@sveltejs/vite-plugin-svelte";
import preprocess from "svelte-preprocess";

export default {
  // Consult https://svelte.dev/docs#compile-time-svelte-preprocess
  // for more information about preprocessors
  //preprocess: vitePreprocess(),
  preprocess: [
    preprocess({
      scss: {
        prependData: '@use "src/app.scss" as *;',
      },
    }),
  ],
};
