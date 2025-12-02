import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getPayroll, downloadPayslip } from '../../api/payrollApi'
import Button from '../../components/ui/Button'
import Loader from '../../components/ui/Loader'

export default function PayrollDetails() {
  const { id } = useParams()
  const [data, setData] = useState(null)

  useEffect(() => { getPayroll(id).then(r => setData(r.data)) }, [id])

  if (!data) return <Loader />
  return (
    <div className="bg-white p-6 rounded shadow max-w-lg">
      <h1 className="text-xl font-bold mb-4">Payroll #{data.id}</h1>
      <p><strong>Employee:</strong> {data.employeeId}</p>
      <p><strong>Month/Year:</strong> {data.month}/{data.year}</p>
      <p><strong>Basic:</strong> {data.basicSalary}</p>
      <p><strong>Allowances:</strong> {data.allowances}</p>
      <p><strong>Deductions:</strong> {data.deductions}</p>
      <p><strong>Net Salary:</strong> {data.netSalary}</p>
      <Button className="mt-4" onClick={() => downloadPayslip(id)}>Download PDF</Button>
    </div>
  )
}



