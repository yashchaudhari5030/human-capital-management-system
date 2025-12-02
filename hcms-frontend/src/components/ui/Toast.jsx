import { useSelector, useDispatch } from 'react-redux'
import { hideToast } from '../../store/uiSlice'
import { useEffect } from 'react'

export default function Toast() {
  const toast = useSelector(s => s.ui.toast)
  const dispatch = useDispatch()
  useEffect(() => { if (toast) { const t = setTimeout(() => dispatch(hideToast()), 3000); return () => clearTimeout(t) } }, [toast, dispatch])
  if (!toast) return null
  const colors = { success: 'bg-green-500', error: 'bg-red-500', info: 'bg-blue-500' }
  return <div className={`fixed bottom-4 right-4 text-white px-4 py-2 rounded shadow ${colors[toast.type] || colors.info}`}>{toast.message}</div>
}



