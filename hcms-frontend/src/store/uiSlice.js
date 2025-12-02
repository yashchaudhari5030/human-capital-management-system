import { createSlice } from '@reduxjs/toolkit'

const uiSlice = createSlice({
  name: 'ui',
  initialState: { toast: null },
  reducers: {
    showToast(state, { payload }) { state.toast = payload },
    hideToast(state) { state.toast = null }
  }
})

export const { showToast, hideToast } = uiSlice.actions
export default uiSlice.reducer



