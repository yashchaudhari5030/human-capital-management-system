# Human Capital Management System (HCMS) - Complete Project Description

## 📋 Overview

The **Human Capital Management System (HCMS)** is a complete, production-ready enterprise application for managing employees, departments, leaves, attendance, payroll, and notifications. The system is built using a **microservices architecture** with a modern **React frontend** and **Spring Boot backend**.

---

## 🏗️ Architecture

### System Architecture
- **Backend**: Microservices architecture with 9 independent Spring Boot services
- **Frontend**: Single Page Application (SPA) built with React
- **Service Discovery**: Netflix Eureka Server
- **API Gateway**: Spring Cloud Gateway with centralized JWT authentication
- **Database**: MySQL (separate database per service)
- **Communication**: RESTful APIs, OpenFeign for service-to-service communication
- **Security**: JWT-based authentication with role-based access control (RBAC)

---

## 🔧 Technology Stack

### Backend
- **Java**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Maven**: Multi-module project
- **MySQL**: 8.0+ (7 separate databases)
- **Eureka**: Service discovery and registry
- **Spring Cloud Gateway**: API Gateway with routing and JWT validation
- **JWT (JJWT)**: Token-based authentication
- **Lombok**: Code generation
- **iTextPDF**: PDF generation for payslips
- **RabbitMQ**: Message queue (ready for async notifications)
- **OpenFeign**: Service-to-service communication

### Frontend
- **React**: 18.2.0 (JavaScript)
- **Vite**: 5.0.8 (Build tool)
- **Tailwind CSS**: 3.4.0 (Styling)
- **Redux Toolkit**: 2.0.1 (State management)
- **React Router DOM**: 6.20.0 (Routing)
- **Axios**: 1.6.2 (HTTP client)
- **JWT Decode**: 4.0.0 (Token decoding)
- **React Icons**: 4.12.0 (Icons)

---

## 🎯 Microservices (Backend)

### 1. **Eureka Server** (Port: 8761)
- Service registry and discovery
- Centralized service management
- Dashboard: `http://localhost:8761`

### 2. **API Gateway** (Port: 8080)
- Single entry point for all client requests
- JWT token validation for all protected routes
- Routes requests to appropriate microservices
- CORS configuration
- Adds user context headers (`X-User-Id`, `X-User-Email`, `X-User-Role`) to downstream services

### 3. **Auth Service** (Port: 8081)
- User registration and authentication
- JWT token generation and validation
- Password encryption (BCrypt)
- Role management
- Auto-creates default SUPER_ADMIN on startup

### 4. **Employee Service** (Port: 8082)
- Employee CRUD operations
- Auto-generated employee IDs (EMP001, EMP002, ...)
- Employee search and pagination
- Manager-employee relationships
- Employee profile management

### 5. **Department Service** (Port: 8083)
- Department management
- Department hierarchy (parent-child relationships)
- Department codes and status management
- Department-based employee filtering

### 6. **Leave Service** (Port: 8084)
- Leave application and management
- Multiple leave types (Annual, Sick, Casual, Maternity, Paternity, etc.)
- Leave balance tracking
- Approval workflow (Employee → Manager → HR)
- Leave overlap detection
- Leave history and reports

### 7. **Attendance Service** (Port: 8085)
- Clock-in/clock-out functionality
- Attendance tracking
- Late arrival and early departure detection
- Total working hours calculation
- Attendance reports and history
- Daily attendance records

### 8. **Payroll Service** (Port: 8086)
- Payroll management
- Salary components (base salary, allowances, bonus, overtime)
- Automatic tax calculation
- Provident Fund (PF) calculation
- Monthly payroll processing
- PDF payslip generation (iText)
- Payroll status tracking (DRAFT, PROCESSED, PAID)

### 9. **Notification Service** (Port: 8087)
- Notification management
- Multiple channels (Email, SMS, In-App)
- Notification status tracking (SENT, PENDING, FAILED)
- RabbitMQ ready for async processing
- Notification history

---

## 🎨 Frontend Modules

### Authentication
- **Login**: Email/Username and password authentication
- **Register**: New user registration with role selection
- **Protected Routes**: JWT-based route protection
- **Role-Based Routes**: Access control based on user roles

### Dashboard
- **Role-Specific Dashboards**:
  - **SUPER_ADMIN/ADMIN**: System overview, statistics, recent activities
  - **MANAGER**: Team overview, pending approvals, team statistics
  - **EMPLOYEE**: Personal dashboard, leave balance, attendance summary

### Employee Management
- **Employee List**: Paginated table with search, filter, and sort
- **Employee Form**: Create/Edit employee with validation
- **Employee Details**: View employee profile, history, and related data
- **Access**: SUPER_ADMIN, ADMIN, MANAGER (view), EMPLOYEE (own profile)

### Department Management
- **Department List**: Hierarchical view of departments
- **Department Form**: Create/Edit departments with parent selection
- **Access**: SUPER_ADMIN, ADMIN only

### Leave Management
- **Leave List**: View all leaves with filters (status, type, date range)
- **Apply Leave**: Submit leave requests with date selection
- **Leave Approval**: Approve/Reject leaves (for MANAGER+)
- **Access**: All roles (with role-based restrictions)

### Attendance Management
- **Today's Attendance**: Clock-in/clock-out interface
- **Attendance History**: View past attendance records
- **Reports**: Filter by date range, export options
- **Access**: All roles (own attendance)

### Payroll Management
- **Payroll List**: View all payrolls with filters
- **Payroll Details**: View detailed payslip information
- **PDF Download**: Download payslip as PDF
- **Access**: SUPER_ADMIN, ADMIN (all), EMPLOYEE (own payroll)

### Notifications
- **Notification List**: View all notifications
- **Mark as Read**: Update notification status
- **Filter**: By type, status, date
- **Access**: All roles (own notifications)

---

## 🔐 Security & Access Control

### User Roles
1. **SUPER_ADMIN**: Full system access, all operations
2. **ADMIN**: Full access to most resources (except super admin functions)
3. **MANAGER**: Access to team members, leave approvals, team reports
4. **EMPLOYEE**: Access to own resources only

### Authentication Flow
1. User logs in via `/api/auth/login`
2. Backend validates credentials and generates JWT token
3. Frontend stores token in localStorage
4. All subsequent requests include token in `Authorization: Bearer <token>` header
5. API Gateway validates token and adds user context headers
6. Microservices use headers for authorization

### JWT Token Structure
```json
{
  "userId": 1,
  "email": "user@hcms.com",
  "role": "ADMIN",
  "exp": 1234567890
}
```

---

## 📊 Database Schema

### Databases (7 separate MySQL databases)
1. **hcms_auth**: Users, roles, authentication data
2. **hcms_employee**: Employee profiles, personal information
3. **hcms_department**: Departments, hierarchy
4. **hcms_leave**: Leave applications, balances, approvals
5. **hcms_attendance**: Attendance records, clock-in/out logs
6. **hcms_payroll**: Payroll records, salary components, payslips
7. **hcms_notification**: Notifications, delivery status

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+ (for frontend)
- RabbitMQ (optional, for async notifications)

### Backend Setup

1. **Start MySQL Server**
   ```bash
   # Ensure MySQL is running on localhost:3306
   # Default credentials: root/12345 (update in application.yml if different)
   ```

2. **Start Eureka Server**
   ```bash
   cd eureka-server
   mvn spring-boot:run
   # Access dashboard: http://localhost:8761
   ```

3. **Start API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   # Gateway runs on: http://localhost:8080
   ```

4. **Start All Microservices** (in separate terminals)
   ```bash
   # Auth Service
   cd auth-service && mvn spring-boot:run
   
   # Employee Service
   cd employee-service && mvn spring-boot:run
   
   # Department Service
   cd department-service && mvn spring-boot:run
   
   # Leave Service
   cd leave-service && mvn spring-boot:run
   
   # Attendance Service
   cd attendance-service && mvn spring-boot:run
   
   # Payroll Service
   cd payroll-service && mvn spring-boot:run
   
   # Notification Service
   cd notification-service && mvn spring-boot:run
   ```

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd hcms-frontend
   npm install
   ```

2. **Start Development Server**
   ```bash
   npm run dev
   # Frontend runs on: http://localhost:5173
   ```

3. **Build for Production**
   ```bash
   npm run build
   ```

### Default Credentials

On first startup, the system auto-creates a super admin:
- **Email**: `superadmin@hcms.com`
- **Password**: `admin123`
- **Role**: `SUPER_ADMIN`

---

## 📡 API Endpoints

All API requests go through the API Gateway at `http://localhost:8080/api/...`

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Employees (`/api/employees`)
- `POST /api/employees` - Create employee (ADMIN+)
- `GET /api/employees` - Get all employees (pagination)
- `GET /api/employees/{id}` - Get employee by ID
- `PUT /api/employees/{id}` - Update employee (ADMIN+)
- `DELETE /api/employees/{id}` - Delete employee (ADMIN+)
- `GET /api/employees/search?keyword=...` - Search employees

### Departments (`/api/departments`)
- `POST /api/departments` - Create department (ADMIN+)
- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department by ID
- `PUT /api/departments/{id}` - Update department (ADMIN+)
- `DELETE /api/departments/{id}` - Delete department (ADMIN+)

### Leaves (`/api/leaves`)
- `POST /api/leaves` - Apply for leave
- `GET /api/leaves` - Get all leaves (filtered by role)
- `GET /api/leaves/{id}` - Get leave by ID
- `PUT /api/leaves/{id}/approve` - Approve leave (MANAGER+)
- `PUT /api/leaves/{id}/reject` - Reject leave (MANAGER+)
- `GET /api/leaves/balance/{employeeId}` - Get leave balance

### Attendance (`/api/attendance`)
- `POST /api/attendance/clock-in` - Clock in
- `POST /api/attendance/clock-out` - Clock out
- `GET /api/attendance/today` - Get today's attendance
- `GET /api/attendance/history` - Get attendance history
- `GET /api/attendance/reports` - Get attendance reports

### Payroll (`/api/payroll`)
- `POST /api/payroll` - Create payroll (ADMIN+)
- `GET /api/payroll` - Get all payrolls
- `GET /api/payroll/{id}` - Get payroll by ID
- `GET /api/payroll/employee/{employeeId}` - Get employee payrolls
- `GET /api/payroll/{id}/payslip` - Download payslip PDF
- `POST /api/payroll/{id}/process` - Process payroll (ADMIN+)

### Notifications (`/api/notifications`)
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/{id}` - Get notification by ID
- `PUT /api/notifications/{id}/read` - Mark as read
- `GET /api/notifications/recipient/{recipientId}` - Get by recipient

---

## 🎯 Key Features

### Backend Features
- ✅ Microservices architecture with service discovery
- ✅ Centralized authentication via API Gateway
- ✅ JWT-based security with role-based access control
- ✅ Auto-generated employee IDs
- ✅ Department hierarchy support
- ✅ Leave approval workflow
- ✅ Attendance tracking with late/early detection
- ✅ Automatic payroll calculations (tax, PF)
- ✅ PDF payslip generation
- ✅ Service-to-service communication (OpenFeign)
- ✅ Global exception handling
- ✅ Database per service (data isolation)

### Frontend Features
- ✅ Responsive design (mobile, tablet, desktop)
- ✅ Role-based route protection
- ✅ JWT token management with auto-refresh
- ✅ Reusable UI components (tables, forms, modals, dialogs)
- ✅ Pagination, filtering, and sorting
- ✅ Loading states and skeletons
- ✅ Toast notifications for user feedback
- ✅ Error boundaries for graceful error handling
- ✅ PDF download support
- ✅ Real-time data updates
- ✅ Modern UI with Tailwind CSS

---

## 📁 Project Structure

```
BackendProject/
├── eureka-server/          # Service registry
├── api-gateway/            # API Gateway with JWT validation
├── auth-service/           # Authentication service
├── employee-service/       # Employee management
├── department-service/     # Department management
├── leave-service/          # Leave management
├── attendance-service/     # Attendance tracking
├── payroll-service/        # Payroll management
├── notification-service/   # Notification service
├── hcms-frontend/          # React frontend application
│   ├── src/
│   │   ├── api/            # API service files
│   │   ├── components/     # Reusable components
│   │   │   ├── layout/     # Layout components
│   │   │   ├── ui/         # UI components
│   │   │   └── table/      # Table components
│   │   ├── pages/          # Page components
│   │   │   ├── Auth/       # Login, Register
│   │   │   ├── Dashboard/  # Dashboards
│   │   │   ├── Employees/  # Employee pages
│   │   │   ├── Departments/# Department pages
│   │   │   ├── Leaves/     # Leave pages
│   │   │   ├── Attendance/ # Attendance pages
│   │   │   ├── Payroll/    # Payroll pages
│   │   │   └── Notifications/# Notification pages
│   │   ├── store/          # Redux store
│   │   ├── utils/          # Utility functions
│   │   ├── router.jsx      # Route configuration
│   │   └── App.jsx         # Main app component
│   ├── package.json
│   └── vite.config.js
└── pom.xml                 # Parent POM
```

---

## 🔄 Data Flow

1. **User Request** → Frontend (React)
2. **API Call** → API Gateway (Port 8080)
3. **JWT Validation** → API Gateway validates token
4. **Service Discovery** → Eureka Server finds target service
5. **Request Routing** → API Gateway routes to appropriate microservice
6. **Business Logic** → Microservice processes request
7. **Database** → MySQL database (service-specific)
8. **Response** → API Gateway → Frontend
9. **UI Update** → Redux state update → React re-render

---

## 🛠️ Configuration

### JWT Secret
Default: `HCMSSecretKeyForJWTTokenGenerationAndValidation2024`
- Configured in: `api-gateway/application.yml` and `auth-service/application.yml`

### Database Configuration
Each service's `application.yml` contains:
- Database URL
- Username/Password (default: root/12345)
- JPA/Hibernate settings

### CORS Configuration
Configured in API Gateway:
- Allowed origins: `*` (patterns)
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: true

---

## 📝 Development Notes

### Service-to-Service Communication
- Uses **OpenFeign** for declarative HTTP clients
- Services communicate via Eureka service names
- Example: `@FeignClient(name = "employee-service")`

### Error Handling
- Global exception handlers in each service
- Consistent error response format
- Frontend error boundaries for UI errors

### Testing
- Backend: Use Postman/curl for API testing
- Frontend: Browser DevTools for debugging
- Eureka Dashboard: Monitor service registration

---

## 🎓 Learning Resources

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **React**: https://react.dev
- **Redux Toolkit**: https://redux-toolkit.js.org
- **Tailwind CSS**: https://tailwindcss.com

---

## 📄 License

This project is for educational and development purposes.

---

## 👥 Support

For issues or questions:
1. Check service logs for errors
2. Verify Eureka dashboard for service registration
3. Check API Gateway logs for routing issues
4. Verify JWT token in browser DevTools
5. Check database connections

---

**Last Updated**: 2024
**Version**: 1.0.0
**Status**: Production-Ready

