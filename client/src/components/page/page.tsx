import type { ReactNode } from 'react'
import { Header } from '../header/header'
import { Footer } from '../footer/footer'
import styles from './page.module.css'

interface PageProps {
  children: ReactNode
}

export function Page({ children }: PageProps) {
  return (
    <div className={styles.page}>
      <div className={styles.contentWrapper}>
        <Header className={styles.header} />
        <main className={styles.content}>{children}</main>
        <Footer className={styles.footer} />
      </div>
    </div>
  )
}
