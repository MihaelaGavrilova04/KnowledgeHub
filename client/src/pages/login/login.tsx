import { useState } from 'react'
import { GoogleButton } from '../../components/google-button/google-button'
import { api } from '../../services/api'
import styles from './login.module.css'

export function LoginPage() {
  const [error, setError] = useState<string | null>(null)

  async function handleToken(idToken: string) {
    try {
      setError(null)
      await api.post('/api/auth/google', { idToken })
      window.location.href = '/'
    } catch (e: any) {
      setError(e.message ?? 'Login failed')
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <h1 className={styles.title}>KnowledgeHub</h1>
        <p className={styles.subtitle}>Your digital library</p>
        <GoogleButton onToken={handleToken} />
        {error && <p className={styles.error}>{error}</p>}
      </div>
    </div>
  )
}
