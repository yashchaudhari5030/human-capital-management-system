import { useSelector } from 'react-redux'
import { Navigate } from 'react-router-dom'
import { hasRole } from '../../utils/roles'

export default function RoleRoute({ allowed, children }) {
  const { user } = useSelector(s => s.auth)
  return hasRole(user, allowed) ? children : <Navigate to="/dashboard" replace />
}



