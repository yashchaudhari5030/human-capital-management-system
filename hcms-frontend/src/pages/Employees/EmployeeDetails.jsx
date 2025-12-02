import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getEmployee } from '../../api/employeeApi'
import Loader from '../../components/ui/Loader'

export default function EmployeeDetails() {
  const { id } = useParams()
  const [emp, setEmp] = useState(null)

  useEffect(() => { getEmployee(id).then(r => setEmp(r.data)) }, [id])

  if (!emp) return <Loader />
  return (
    <div className="bg-white p-6 rounded shadow max-w-2xl">
      <h1 className="text-xl font-bold mb-4">{emp.firstName} {emp.lastName}</h1>
      <p><strong>Email:</strong> {emp.email}</p>
      <p><strong>Phone:</strong> {emp.phoneNumber}</p>
      <p><strong>Designation:</strong> {emp.designation}</p>
      <p><strong>Department ID:</strong> {emp.departmentId}</p>
      <Link to="/employees" className="text-primary-600 mt-4 inline-block">Back</Link>
    </div>
  )
}



