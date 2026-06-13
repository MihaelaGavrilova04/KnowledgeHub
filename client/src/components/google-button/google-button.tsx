import { useEffect } from 'react'
import { env } from '../../config/config'
import { usePropRef } from '../../hooks/use-prop-ref'
import styles from './google-button.module.css'

interface GoogleButtonProps {
  onToken: (idToken: string) => void
}

let initialized = false

export function GoogleButton({ onToken }: GoogleButtonProps) {
  const onTokenRef = usePropRef(onToken)

  useEffect(() => {
    if (initialized) return
    initialized = true
    window.google?.accounts.id.initialize({
      client_id: env.VITE_GOOGLE_CLIENT_ID,
      callback: (res: { credential: string }) => {
        onTokenRef.current(res.credential)
      },
    })
  }, [onTokenRef])

  useEffect(() => {
    const container = document.getElementById('google-btn-container')
    if (container) {
      window.google?.accounts.id.renderButton(container, {
        type: 'standard',
        theme: 'filled_blue',
        size: 'large',
        text: 'signin_with',
      })
    }
  }, [])

  return <div id="google-btn-container" className={styles.container} />
}
