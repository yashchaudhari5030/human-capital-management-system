import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getDepartments, deleteDepartment } from '../../api/departmentApi'
import Table from '../../components/table/Table'
import Button from '../../components/ui/Button'
import Loader from '../../components/ui/Loader'

export default function DepartmentList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => { setLoading(true); const r = await getDepartments(); setData(r.data); setLoading(false) }
  useEffect(() => { load() }, [])

  const handleDelete = async id => { if (confirm('Delete?')) { await deleteDepartment(id); load() } }

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'description', label: 'Description' },
    { key: 'actions', label: 'Actions', render: row => (
      <div className="flex gap-2">
        <Link to={`/departments/${row.id}/edit`} className="text-green-500">Edit</Link>
        <button onClick={() => handleDelete(row.id)} className="text-red-500">Delete</button>
      </div>
    )}
  ]

  if (loading) return <Loader />
  return (
    <div>
      <div className="flex justify-between mb-4">
        <h1 className="text-xl font-bold">Departments</h1>
        <Link to="/departments/new"><Button>Add Department</Button></Link>
      </div>
      <Table columns={columns} data={data} />
    </div>
  )
}



