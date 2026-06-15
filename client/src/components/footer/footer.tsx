import styles from './footer.module.css'
import { clsx } from 'clsx'
import { Text } from '../text/text'

const getYear = () => {
  const date = new Date()
  return date.getFullYear()
}

interface FooterProps {
  className?: string
}

export function Footer({ className }: FooterProps) {
  return (
    <footer className={clsx(styles.footer, className)}>
      <Text variant="small" className={styles.footerText}>
        &copy; {getYear()} KnowledgeHub. All rights reserved.
      </Text>
    </footer>
  )
}
