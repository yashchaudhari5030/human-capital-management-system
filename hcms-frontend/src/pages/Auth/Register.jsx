import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { registerApi } from '../../api/authApi'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Button from '../../components/ui/Button'

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'EMPLOYEE' })
  const [err, setErr] = useState('')
  const nav = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    try {
      await registerApi(form)
      nav('/login')
    } catch (e) { setErr(e.response?.data?.message || 'Registration failed') }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-500 to-primary-700">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-lg w-full max-w-sm">
        <h2 className="text-2xl font-bold mb-6 text-center">Register</h2>
        {err && <p className="text-red-500 mb-4">{err}</p>}
        <Input label="Username" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} required />
        <div className="mt-4"><Input label="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required /></div>
        <div className="mt-4"><Input label="Password" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} required /></div>
        <div className="mt-4"><Select label="Role" value={form.role} onChange={e => setForm({ ...form, role: e.target.value })} options={[{value:'EMPLOYEE',label:'Employee'},{value:'MANAGER',label:'Manager'},{value:'ADMIN',label:'Admin'},{value:'SUPER_ADMIN',label:'Super Admin'}]} /></div>
        <Button className="w-full mt-6">Register</Button>
        <p className="mt-4 text-center text-sm">Have account? <Link to="/login" className="text-primary-600">Login</Link></p>
      </form>
    </div>
  )
}



