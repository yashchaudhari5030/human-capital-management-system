import api, { downloadBlob } from './axiosInstance'
export const getPayrolls = params => api.get('/payroll', { params })
export const getPayroll = id => api.get(`/payroll/${id}`)
export const generatePayroll = data => api.post('/payroll/generate', data)
export const downloadPayslip = id => downloadBlob(`/payroll/${id}/pdf`, `payslip-${id}.pdf`)



