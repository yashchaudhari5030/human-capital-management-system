import { useState } from 'react'
import { checkIn, checkOut } from '../../api/attendanceApi'
import Button from '../../components/ui/Button'

export default function AttendanceToday() {
  const [msg, setMsg] = useState('')

  const handleCheckIn = async () => { await checkIn(); setMsg('Checked in!') }
  const handleCheckOut = async () => { await checkOut(); setMsg('Checked out!') }

  return (
    <div className="bg-white p-6 rounded shadow max-w-md">
      <h1 className="text-xl font-bold mb-4">Today's Attendance</h1>
      {msg && <p className="text-green-600 mb-4">{msg}</p>}
      <div className="flex gap-4">
        <Button onClick={handleCheckIn}>Check In</Button>
        <Button onClick={handleCheckOut} variant="secondary">Check Out</Button>
      </div>
    </div>
  )
}



