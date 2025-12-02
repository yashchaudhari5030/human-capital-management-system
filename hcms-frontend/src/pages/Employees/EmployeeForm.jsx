import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getEmployee, createEmployee, updateEmployee } from '../../api/employeeApi'
import Input from '../../components/ui/Input'
import Button from '../../components/ui/Button'

export default function EmployeeForm() {
  const { id } = useParams()
  const nav = useNavigate()
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', phoneNumber: '', designation: '', departmentId: '' })

  useEffect(() => { if (id) getEmployee(id).then(r => setForm(r.data)) }, [id])

  const handleSubmit = async e => {
    e.preventDefault()
    if (id) await updateEmployee(id, form)
    else await createEmployee(form)
    nav('/employees')
  }

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow max-w-lg">
      <h1 className="text-xl font-bold mb-4">{id ? 'Edit' : 'Add'} Employee</h1>
      <div className="grid gap-4">
        <Input label="First Name" value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required />
        <Input label="Last Name" value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required />
        <Input label="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required />
        <Input label="Phone" value={form.phoneNumber} onChange={e => setForm({ ...form, phoneNumber: e.target.value })} />
        <Input label="Designation" value={form.designation} onChange={e => setForm({ ...form, designation: e.target.value })} />
        <Input label="Department ID" type="number" value={form.departmentId} onChange={e => setForm({ ...form, departmentId: e.target.value })} />
      </div>
      <div className="flex gap-2 mt-6">
        <Button type="submit">{id ? 'Update' : 'Create'}</Button>
        <Button type="button" variant="secondary" onClick={() => nav('/employees')}>Cancel</Button>
      </div>
    </form>
  )
}



