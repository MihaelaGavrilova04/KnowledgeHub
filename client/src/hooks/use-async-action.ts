import { useEffect, useRef, useState } from 'react'

interface AsyncActionState<T> {
  data: T | undefined
  isLoading: boolean
  error: unknown
}

export interface AsyncActionOptions {
  initiallyLoading: boolean
  keepDataOnReload?: boolean
}

const DEFAULT_OPTIONS: AsyncActionOptions = {
  initiallyLoading: false,
  keepDataOnReload: false,
}

export function useAsyncAction<Result, Args extends unknown[]>(
  action: (...args: Args) => Promise<Result>,
  opts: Partial<AsyncActionOptions> = {},
) {
  const options: AsyncActionOptions = { ...DEFAULT_OPTIONS, ...opts }

  const [state, setState] = useState<AsyncActionState<Result>>({
    data: undefined,
    isLoading: options.initiallyLoading,
    error: undefined,
  })

  const requestIdRef = useRef(0)

  const execute = async (...args: Args) => {
    setState((current) => ({
      data: options.keepDataOnReload ? current.data : undefined,
      isLoading: true,
      error: undefined,
    }))
    requestIdRef.current++
    const myId = requestIdRef.current
    try {
      const data = await action(...args)
      if (myId === requestIdRef.current) {
        setState({ data, isLoading: false, error: undefined })
      }
      return data
    } catch (error) {
      if (myId === requestIdRef.current) {
        setState({ data: undefined, isLoading: false, error })
      }
      throw error
    }
  }

  const trigger = (...args: Args) => {
    execute(...args).catch(() => {})
  }

  useEffect(
    () => () => {
      requestIdRef.current++
    },
    [],
  )

  return { ...state, execute, trigger }
}
