import axios from 'axios'
import { store } from '../store'
import { logout } from '../store/authSlice'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use(cfg => {
  const token = store.getState().auth.token
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

api.interceptors.response.use(r => r, err => {
  if (err.response?.status === 401) store.dispatch(logout())
  return Promise.reject(err)
})

export const downloadBlob = async (url, filename) => {
  const res = await api.get(url, { responseType: 'blob' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(res.data)
  link.download = filename
  link.click()
}

export default api



