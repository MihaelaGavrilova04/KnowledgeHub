import { z } from 'zod'

const envSchema = z.object({
  VITE_API_URL: z.string().url().default('http://localhost:8091'),
  VITE_GOOGLE_CLIENT_ID: z.string(),
})

const parsed = envSchema.safeParse(import.meta.env)

if (!parsed.success) {
  throw new Error('Invalid environment variables')
}

export const env = parsed.data
