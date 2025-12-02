import { useEffect, useState } from 'react'
import { getLeaves, approveLeave } from '../../api/leaveApi'
import Table from '../../components/table/Table'
import Loader from '../../components/ui/Loader'

export default function LeaveApproval() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => { setLoading(true); const r = await getLeaves({ status: 'PENDING' }); setData(r.data.content || r.data); setLoading(false) }
  useEffect(() => { load() }, [])

  const handle = async (id, status) => { await approveLeave(id, status); load() }

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'employeeId', label: 'Employee' },
    { key: 'leaveType', label: 'Type' },
    { key: 'startDate', label: 'Start' },
    { key: 'endDate', label: 'End' },
    { key: 'actions', label: 'Actions', render: row => (
      <div className="flex gap-2">
        <button onClick={() => handle(row.id, 'APPROVED')} className="text-green-500">Approve</button>
        <button onClick={() => handle(row.id, 'REJECTED')} className="text-red-500">Reject</button>
      </div>
    )}
  ]

  if (loading) return <Loader />
  return (
    <div>
      <h1 className="text-xl font-bold mb-4">Leave Approvals</h1>
      <Table columns={columns} data={data} />
    </div>
  )
}



