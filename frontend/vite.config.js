import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 13580,
    proxy: {
      '/api': {
        target: 'http://localhost:13579',
        changeOrigin: true
      }
    }
  }
})
