import { useSelector } from 'react-redux'

export default function DashboardHome() {
  const { user } = useSelector(s => s.auth)
  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Welcome, {user?.email}</h1>
      <p className="text-gray-600">Role: {user?.role}</p>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
        <div className="bg-white p-6 rounded shadow"><h3 className="font-bold">Employees</h3><p className="text-3xl mt-2">--</p></div>
        <div className="bg-white p-6 rounded shadow"><h3 className="font-bold">Departments</h3><p className="text-3xl mt-2">--</p></div>
        <div className="bg-white p-6 rounded shadow"><h3 className="font-bold">Pending Leaves</h3><p className="text-3xl mt-2">--</p></div>
      </div>
    </div>
  )
}



