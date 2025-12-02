import api from './axiosInstance'
export const getLeaves = params => api.get('/leaves', { params })
export const getMyLeaves = () => api.get('/leaves/my')
export const applyLeave = data => api.post('/leaves', data)
export const approveLeave = (id, status) => api.put(`/leaves/${id}/status?status=${status}`)



