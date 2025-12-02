import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getPayrolls } from '../../api/payrollApi'
import Table from '../../components/table/Table'
import Pagination from '../../components/table/Pagination'
import Loader from '../../components/ui/Loader'

export default function PayrollList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    setLoading(true)
    getPayrolls({ page, size: 10 }).then(r => { setData(r.data.content || r.data); setTotalPages(r.data.totalPages || 1); setLoading(false) })
  }, [page])

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'employeeId', label: 'Employee' },
    { key: 'month', label: 'Month' },
    { key: 'year', label: 'Year' },
    { key: 'netSalary', label: 'Net Salary' },
    { key: 'actions', label: 'Actions', render: row => <Link to={`/payroll/${row.id}`} className="text-blue-500">View</Link> }
  ]

  if (loading) return <Loader />
  return (
    <div>
      <h1 className="text-xl font-bold mb-4">Payroll</h1>
      <Table columns={columns} data={data} />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  )
}



