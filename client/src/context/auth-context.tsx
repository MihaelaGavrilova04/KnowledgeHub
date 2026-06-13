import { createContext, useContext } from 'react'
import type { ReactNode } from 'react'
import { useAsync } from '../hooks/use-async'
import { api } from '../services/api'

interface User {
  id: string
  email: string
  name: string
  role: string
  avatarUrl: string | null
}

interface AuthContextType {
  user: User | null
  isLoading: boolean
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const { data, isLoading } = useAsync<User | null>(() =>
    api.get<User>('/api/auth/me').catch(() => null),
  )

  function logout() {
    api.post('/api/auth/logout').finally(() => {
      window.location.href = '/login'
    })
  }

  return (
    <AuthContext.Provider value={{ user: data ?? null, isLoading, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
