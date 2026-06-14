import type { ReactNode } from 'react'
import styles from './text.module.css'
import clsx from 'clsx'

function TextPlaceholder({ className }: { className?: string }) {
  return <p className={clsx(styles.text, styles.placeholder, className)} />
}

interface BaseTextProps {
  className?: string
}

type TextProps =
  | (BaseTextProps & {
      variant?: 'regular' | 'semiBold' | 'small'
      isLoading?: false
      children: ReactNode
    })
  | (BaseTextProps & { isLoading: true })

export function Text(props: React.PropsWithChildren<TextProps>) {
  if (props.isLoading) return <TextPlaceholder className={props.className} />
  const { variant = 'regular', children, className } = props
  const variantClass = variant === 'regular' ? '' : styles[variant]

  return <p className={clsx(styles.text, variantClass, className)}>{children}</p>
}
