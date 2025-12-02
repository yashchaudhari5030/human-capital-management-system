import { NavLink } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { hasRole } from '../../utils/roles'

const links = [
  { to: '/dashboard', label: 'Dashboard', roles: null },
  { to: '/employees', label: 'Employees', roles: ['SUPER_ADMIN','ADMIN','MANAGER'] },
  { to: '/departments', label: 'Departments', roles: ['SUPER_ADMIN','ADMIN'] },
  { to: '/leaves', label: 'Leaves', roles: null },
  { to: '/attendance', label: 'Attendance', roles: null },
  { to: '/payroll', label: 'Payroll', roles: ['SUPER_ADMIN','ADMIN'] },
  { to: '/notifications', label: 'Notifications', roles: null }
]

export default function Sidebar() {
  const { user } = useSelector(s => s.auth)
  return (
    <aside className="w-56 bg-gray-800 text-white min-h-screen p-4">
      <nav className="flex flex-col gap-2">
        {links.filter(l => !l.roles || hasRole(user, l.roles)).map(l => (
          <NavLink key={l.to} to={l.to} className={({ isActive }) => `p-2 rounded ${isActive ? 'bg-primary-500' : 'hover:bg-gray-700'}`}>{l.label}</NavLink>
        ))}
      </nav>
    </aside>
  )
}



