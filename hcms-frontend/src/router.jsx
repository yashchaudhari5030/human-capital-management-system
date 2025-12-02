import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/layout/ProtectedRoute'
import RoleRoute from './components/layout/RoleRoute'
import Login from './pages/Auth/Login'
import Register from './pages/Auth/Register'
import DashboardHome from './pages/Dashboard/DashboardHome'
import EmployeeList from './pages/Employees/EmployeeList'
import EmployeeForm from './pages/Employees/EmployeeForm'
import EmployeeDetails from './pages/Employees/EmployeeDetails'
import DepartmentList from './pages/Departments/DepartmentList'
import DepartmentForm from './pages/Departments/DepartmentForm'
import LeaveList from './pages/Leaves/LeaveList'
import LeaveApply from './pages/Leaves/LeaveApply'
import LeaveApproval from './pages/Leaves/LeaveApproval'
import AttendanceToday from './pages/Attendance/AttendanceToday'
import AttendanceHistory from './pages/Attendance/AttendanceHistory'
import PayrollList from './pages/Payroll/PayrollList'
import PayrollDetails from './pages/Payroll/PayrollDetails'
import NotificationList from './pages/Notifications/NotificationList'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardHome />} />
        <Route path="/employees" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN','MANAGER']}><EmployeeList /></RoleRoute>} />
        <Route path="/employees/new" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><EmployeeForm /></RoleRoute>} />
        <Route path="/employees/:id" element={<EmployeeDetails />} />
        <Route path="/employees/:id/edit" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><EmployeeForm /></RoleRoute>} />
        <Route path="/departments" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><DepartmentList /></RoleRoute>} />
        <Route path="/departments/new" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><DepartmentForm /></RoleRoute>} />
        <Route path="/departments/:id/edit" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><DepartmentForm /></RoleRoute>} />
        <Route path="/leaves" element={<LeaveList />} />
        <Route path="/leaves/apply" element={<LeaveApply />} />
        <Route path="/leaves/approval" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN','MANAGER']}><LeaveApproval /></RoleRoute>} />
        <Route path="/attendance" element={<AttendanceToday />} />
        <Route path="/attendance/history" element={<AttendanceHistory />} />
        <Route path="/payroll" element={<RoleRoute allowed={['SUPER_ADMIN','ADMIN']}><PayrollList /></RoleRoute>} />
        <Route path="/payroll/:id" element={<PayrollDetails />} />
        <Route path="/notifications" element={<NotificationList />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}



