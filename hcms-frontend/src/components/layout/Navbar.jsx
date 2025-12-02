import { useDispatch, useSelector } from 'react-redux'
import { logout } from '../../store/authSlice'
import { useNavigate } from 'react-router-dom'

export default function Navbar() {
  const { user } = useSelector(s => s.auth)
  const dispatch = useDispatch()
  const nav = useNavigate()
  const handleLogout = () => { dispatch(logout()); nav('/login') }
  return (
    <header className="bg-primary-600 text-white p-4 flex justify-between items-center">
      <h1 className="text-xl font-bold">HCMS</h1>
      <div className="flex items-center gap-4">
        <span>{user?.email} ({user?.role})</span>
        <button onClick={handleLogout} className="bg-white text-primary-600 px-3 py-1 rounded">Logout</button>
      </div>
    </header>
  )
}



