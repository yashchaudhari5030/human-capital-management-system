import api from './axiosInstance'
export const checkIn = () => api.post('/attendance/check-in')
export const checkOut = () => api.post('/attendance/check-out')
export const getMyAttendance = params => api.get('/attendance/my', { params })
export const getAllAttendance = params => api.get('/attendance', { params })



