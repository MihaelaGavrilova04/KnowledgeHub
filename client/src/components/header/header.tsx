import { clsx } from 'clsx'
import { Link } from 'react-router-dom'
import { Button } from '../button/button'
import { useAuth } from '../../context/auth-context'
import styles from './header.module.css'

interface HeaderProps {
  className?: string
}

export function Header({ className }: HeaderProps) {
  const { user, logout } = useAuth()

  return (
    <header className={clsx(styles.header, className)}>
      <Link to="/" className={styles.logo}>
        KnowledgeHub
      </Link>

      {user && (
        <div className={styles.user}>
          <Link to="/reading-list" className={styles.navLink}>My List</Link>
          <span className={styles.userName}>{user.name}</span>
          <Button variant="ghost" icon="logout" onClick={logout} className={styles.logoutBtn}>
            Logout
          </Button>
        </div>
      )}
    </header>
  )
}
