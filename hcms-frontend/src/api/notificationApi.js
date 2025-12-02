import api from './axiosInstance'
export const getNotifications = () => api.get('/notifications')
export const markRead = id => api.put(`/notifications/${id}/read`)



