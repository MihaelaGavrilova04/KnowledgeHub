import type { ComponentProps } from 'react'
import clsx from 'clsx'
import styles from './button.module.css'

type ButtonComponent = keyof React.JSX.IntrinsicElements | React.JSXElementConstructor<any>

type ButtonProps<T extends ButtonComponent = 'button'> = Omit<ComponentProps<T>, never> & {
  component?: T
  variant?: 'filled' | 'outlined' | 'ghost'
  isLoading?: boolean
}

const variantStyles: Record<'filled' | 'outlined' | 'ghost', string> = {
  filled: styles.filled,
  outlined: styles.outlined,
  ghost: styles.ghost,
}

function Loader() {
  return <span className={styles.loader} aria-hidden="true" />
}

export function Button<T extends ButtonComponent>({
  component,
  variant = 'filled',
  className,
  children,
  isLoading = false,
  ...props
}: ButtonProps<T>) {
  const Component = component ?? 'button'

  return (
    <Component
      className={clsx(styles.button, variantStyles[variant], className)}
      {...props}
      style={{ paddingLeft: '16px', lineHeight: '32px' }}
    >
      {isLoading ? <Loader /> : children}
    </Component>
  )
}
