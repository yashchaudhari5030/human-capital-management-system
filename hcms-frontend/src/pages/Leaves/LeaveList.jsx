import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyLeaves } from '../../api/leaveApi'
import Table from '../../components/table/Table'
import Button from '../../components/ui/Button'
import Loader from '../../components/ui/Loader'

export default function LeaveList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => { getMyLeaves().then(r => { setData(r.data); setLoading(false) }) }, [])

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'leaveType', label: 'Type' },
    { key: 'startDate', label: 'Start' },
    { key: 'endDate', label: 'End' },
    { key: 'status', label: 'Status' }
  ]

  if (loading) return <Loader />
  return (
    <div>
      <div className="flex justify-between mb-4">
        <h1 className="text-xl font-bold">My Leaves</h1>
        <Link to="/leaves/apply"><Button>Apply Leave</Button></Link>
      </div>
      <Table columns={columns} data={data} />
    </div>
  )
}



