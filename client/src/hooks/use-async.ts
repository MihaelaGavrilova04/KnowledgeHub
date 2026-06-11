import { useEffect } from 'react'
import type { DependencyList } from 'react'
import { useAsyncAction, type AsyncActionOptions } from './use-async-action'
import { usePropRef } from './use-prop-ref'

export function useAsync<R>(
  action: () => Promise<R>,
  dependencyArray: DependencyList = [],
  options: Omit<AsyncActionOptions, 'initiallyLoading'> = {},
) {
  const { data, error, isLoading, trigger } = useAsyncAction(action, {
    ...options,
    initiallyLoading: true,
  })

  const triggerRef = usePropRef(trigger)

  useEffect(() => {
    triggerRef.current()
  }, dependencyArray)

  return { data, isLoading, error, reload: trigger }
}
