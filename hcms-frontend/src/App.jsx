import AppRoutes from './router'
import Navbar from './components/layout/Navbar'
import Sidebar from './components/layout/Sidebar'
import { useSelector } from 'react-redux'

export default function App() {
  const { token } = useSelector(s => s.auth)
  return (
    <div className="min-h-screen flex">
      {token && <Sidebar />}
      <div className="flex-1 flex flex-col">
        {token && <Navbar />}
        <main className="flex-1 p-4">
          <AppRoutes />
        </main>
      </div>
    </div>
  )
}



