import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { applyLeave } from '../../api/leaveApi'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Button from '../../components/ui/Button'

export default function LeaveApply() {
  const nav = useNavigate()
  const [form, setForm] = useState({ leaveType: 'ANNUAL', startDate: '', endDate: '', reason: '' })

  const handleSubmit = async e => {
    e.preventDefault()
    await applyLeave(form)
    nav('/leaves')
  }

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow max-w-lg">
      <h1 className="text-xl font-bold mb-4">Apply Leave</h1>
      <div className="grid gap-4">
        <Select label="Type" value={form.leaveType} onChange={e => setForm({ ...form, leaveType: e.target.value })} options={[{value:'ANNUAL',label:'Annual'},{value:'SICK',label:'Sick'},{value:'CASUAL',label:'Casual'}]} />
        <Input label="Start Date" type="date" value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value })} required />
        <Input label="End Date" type="date" value={form.endDate} onChange={e => setForm({ ...form, endDate: e.target.value })} required />
        <Input label="Reason" value={form.reason} onChange={e => setForm({ ...form, reason: e.target.value })} />
      </div>
      <div className="flex gap-2 mt-6">
        <Button type="submit">Submit</Button>
        <Button type="button" variant="secondary" onClick={() => nav('/leaves')}>Cancel</Button>
      </div>
    </form>
  )
}



