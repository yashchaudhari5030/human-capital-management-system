import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getDepartment, createDepartment, updateDepartment } from '../../api/departmentApi'
import Input from '../../components/ui/Input'
import Button from '../../components/ui/Button'

export default function DepartmentForm() {
  const { id } = useParams()
  const nav = useNavigate()
  const [form, setForm] = useState({ name: '', description: '' })

  useEffect(() => { if (id) getDepartment(id).then(r => setForm(r.data)) }, [id])

  const handleSubmit = async e => {
    e.preventDefault()
    if (id) await updateDepartment(id, form)
    else await createDepartment(form)
    nav('/departments')
  }

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow max-w-lg">
      <h1 className="text-xl font-bold mb-4">{id ? 'Edit' : 'Add'} Department</h1>
      <div className="grid gap-4">
        <Input label="Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
        <Input label="Description" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
      </div>
      <div className="flex gap-2 mt-6">
        <Button type="submit">{id ? 'Update' : 'Create'}</Button>
        <Button type="button" variant="secondary" onClick={() => nav('/departments')}>Cancel</Button>
      </div>
    </form>
  )
}



