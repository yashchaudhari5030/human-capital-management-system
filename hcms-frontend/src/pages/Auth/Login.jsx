import { useState } from 'react'
import { useDispatch } from 'react-redux'
import { useNavigate, Link } from 'react-router-dom'
import { loginApi } from '../../api/authApi'
import { setCredentials, setError } from '../../store/authSlice'
import Input from '../../components/ui/Input'
import Button from '../../components/ui/Button'

export default function Login() {
  const [form, setForm] = useState({ emailOrUsername: '', password: '' })
  const [err, setErr] = useState('')
  const dispatch = useDispatch()
  const nav = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    try {
      const { data } = await loginApi(form)
      dispatch(setCredentials({ token: data.token }))
      nav('/dashboard')
    } catch (e) { setErr(e.response?.data?.message || 'Login failed') }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-500 to-primary-700">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-lg w-full max-w-sm">
        <h2 className="text-2xl font-bold mb-6 text-center">HCMS Login</h2>
        {err && <p className="text-red-500 mb-4">{err}</p>}
        <Input label="Email or Username" value={form.emailOrUsername} onChange={e => setForm({ ...form, emailOrUsername: e.target.value })} required />
        <div className="mt-4"><Input label="Password" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} required /></div>
        <Button className="w-full mt-6">Login</Button>
        <p className="mt-4 text-center text-sm">No account? <Link to="/register" className="text-primary-600">Register</Link></p>
      </form>
    </div>
  )
}

