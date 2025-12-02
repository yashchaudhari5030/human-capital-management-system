import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getEmployees, deleteEmployee } from '../../api/employeeApi'
import Table from '../../components/table/Table'
import Pagination from '../../components/table/Pagination'
import Button from '../../components/ui/Button'
import Loader from '../../components/ui/Loader'

export default function EmployeeList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const load = async () => {
    setLoading(true)
    const res = await getEmployees({ page, size: 10 })
    setData(res.data.content || res.data)
    setTotalPages(res.data.totalPages || 1)
    setLoading(false)
  }

  useEffect(() => { load() }, [page])

  const handleDelete = async id => { if (confirm('Delete?')) { await deleteEmployee(id); load() } }

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    { key: 'designation', label: 'Designation' },
    { key: 'actions', label: 'Actions', render: row => (
      <div className="flex gap-2">
        <Link to={`/employees/${row.id}`} className="text-blue-500">View</Link>
        <Link to={`/employees/${row.id}/edit`} className="text-green-500">Edit</Link>
        <button onClick={() => handleDelete(row.id)} className="text-red-500">Delete</button>
      </div>
    )}
  ]

  if (loading) return <Loader />
  return (
    <div>
      <div className="flex justify-between mb-4">
        <h1 className="text-xl font-bold">Employees</h1>
        <Link to="/employees/new"><Button>Add Employee</Button></Link>
      </div>
      <Table columns={columns} data={data} />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  )
}



