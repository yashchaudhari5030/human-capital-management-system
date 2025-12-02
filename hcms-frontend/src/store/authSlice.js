import { createSlice } from '@reduxjs/toolkit'
import { jwtDecode } from 'jwt-decode'

const token = localStorage.getItem('token')
let user = null
if (token) { try { user = jwtDecode(token) } catch {} }

const authSlice = createSlice({
  name: 'auth',
  initialState: { token, user, loading: false, error: null },
  reducers: {
    setCredentials(state, { payload }) {
      state.token = payload.token
      state.user = jwtDecode(payload.token)
      localStorage.setItem('token', payload.token)
    },
    logout(state) {
      state.token = null
      state.user = null
      localStorage.removeItem('token')
    },
    setLoading(state, { payload }) { state.loading = payload },
    setError(state, { payload }) { state.error = payload }
  }
})

export const { setCredentials, logout, setLoading, setError } = authSlice.actions
export default authSlice.reducer



