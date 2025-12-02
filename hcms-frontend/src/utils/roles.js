export const ROLES = { SUPER_ADMIN: 'SUPER_ADMIN', ADMIN: 'ADMIN', MANAGER: 'MANAGER', EMPLOYEE: 'EMPLOYEE' }
export const hasRole = (user, allowed) => allowed.includes(user?.role)



