export const formatDate = d => d ? new Date(d).toLocaleDateString() : ''
export const formatCurrency = n => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(n)



