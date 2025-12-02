import { useEffect, useState } from 'react'
import { getMyAttendance } from '../../api/attendanceApi'
import Table from '../../components/table/Table'
import Loader from '../../components/ui/Loader'

export default function AttendanceHistory() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => { getMyAttendance({}).then(r => { setData(r.data.content || r.data); setLoading(false) }) }, [])

  const columns = [
    { key: 'date', label: 'Date' },
    { key: 'checkInTime', label: 'Check In' },
    { key: 'checkOutTime', label: 'Check Out' },
    { key: 'status', label: 'Status' }
  ]

  if (loading) return <Loader />
  return (
    <div>
      <h1 className="text-xl font-bold mb-4">Attendance History</h1>
      <Table columns={columns} data={data} />
    </div>
  )
}



