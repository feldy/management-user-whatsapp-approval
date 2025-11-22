import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/users': {
                target: 'http://localhost:8185',
                changeOrigin: true,
                secure: false,
            },
        },
    },
})
