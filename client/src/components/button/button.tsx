import type { ComponentProps } from 'react'
import clsx from 'clsx'
import { Icon } from '../icons/icon'
import type { IconType } from '../icons/all-icons'
import styles from './button.module.css'

type ButtonComponent = keyof React.JSX.IntrinsicElements | React.JSXElementConstructor<any>

type ButtonProps<T extends ButtonComponent = 'button'> = Omit<ComponentProps<T>, never> & {
  component?: T
  variant?: 'filled' | 'outlined' | 'ghost'
  icon?: IconType
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
  icon,
  isLoading = false,
  ...props
}: ButtonProps<T>) {
  const Component = component ?? 'button'

  return (
    <Component
      className={clsx(styles.button, variantStyles[variant], className)}
      {...props}
      style={{
        ...((!icon || isLoading) && { paddingLeft: '16px', lineHeight: '32px' }),
        ...(!children && { paddingRight: '6px' }),
      }}
    >
      {isLoading ? (
        <Loader />
      ) : (
        <>
          {icon && <Icon type={icon} />}
          {children}
        </>
      )}
    </Component>
  )
}
