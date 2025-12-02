# Human Capital Management System (HCMS) - Backend

A complete, production-ready Human Capital Management System backend built with Spring Boot 3, Java 21, and Maven multi-module architecture. The system uses microservices architecture with service discovery via Eureka Server and API Gateway for centralized authentication and routing.

## Architecture

### Microservices

1. **eureka-server (Port 8761)**: Service registry for all microservices
2. **api-gateway (Port 8080)**: Spring Cloud Gateway with JWT validation and routing
3. **auth-service (Port 8081)**: Authentication and authorization with JWT
4. **employee-service (Port 8082)**: Employee management with CRUD operations
5. **department-service (Port 8083)**: Department management with hierarchy support
6. **leave-service (Port 8084)**: Leave management with approval workflow
7. **attendance-service (Port 8085)**: Attendance tracking with clock-in/out
8. **payroll-service (Port 8086)**: Payroll management with PDF generation
9. **notification-service (Port 8087)**: Notification service with email/SMS/in-app support

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Maven**: Multi-module project
- **MySQL**: Database for each service
- **Eureka**: Service discovery
- **Spring Cloud Gateway**: API Gateway
- **JWT**: Authentication
- **Lombok**: Code generation
- **iTextPDF**: PDF generation for payslips
- **RabbitMQ**: Message queue (ready for async notifications)

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- RabbitMQ (optional, for async notifications)

## Database Setup

Each service uses its own MySQL database. The databases will be created automatically on first run if they don't exist.

### Databases

- `hcms_auth` - Authentication service
- `hcms_employee` - Employee service
- `hcms_department` - Department service
- `hcms_leave` - Leave service
- `hcms_attendance` - Attendance service
- `hcms_payroll` - Payroll service
- `hcms_notification` - Notification service

### MySQL Configuration

Update the database credentials in each service's `application.yml`:
- Default username: `root`
- Default password: `root`
- Default host: `localhost:3306`

## Running the Application

### 1. Start MySQL Server

Ensure MySQL is running on `localhost:3306` with the configured credentials.

### 2. Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Eureka Dashboard: http://localhost:8761

### 3. Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

### 4. Start All Services

In separate terminals, start each service:

```bash
# Auth Service
cd auth-service
mvn spring-boot:run

# Employee Service
cd employee-service
mvn spring-boot:run

# Department Service
cd department-service
mvn spring-boot:run

# Leave Service
cd leave-service
mvn spring-boot:run

# Attendance Service
cd attendance-service
mvn spring-boot:run

# Payroll Service
cd payroll-service
mvn spring-boot:run

# Notification Service
cd notification-service
mvn spring-boot:run
```

### Alternative: Build All at Once

```bash
# From root directory
mvn clean install
```

Then run each service individually.

## Default Super Admin

On first startup, the auth-service automatically creates a super admin user:

- **Email**: `superadmin@hcms.com`
- **Password**: `admin123`
- **Role**: `SUPER_ADMIN`

## API Endpoints

All API requests should go through the API Gateway at `http://localhost:8080`.

### Authentication (`/api/auth`)

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Employee Management (`/api/employees`)

- `POST /api/employees` - Create employee (ADMIN only)
- `GET /api/employees/{id}` - Get employee by ID
- `GET /api/employees` - Get all employees (pagination, MANAGER+)
- `GET /api/employees/search?keyword=...` - Search employees
- `GET /api/employees/department/{departmentId}` - Get employees by department
- `GET /api/employees/manager/{managerId}` - Get employees by manager
- `PUT /api/employees/{id}` - Update employee (ADMIN only)
- `DELETE /api/employees/{id}` - Delete employee (ADMIN only)

### Department Management (`/api/departments`)

- `POST /api/departments` - Create department (ADMIN/SUPER_ADMIN only)
- `GET /api/departments/{id}` - Get department by ID
- `GET /api/departments` - Get all departments
- `GET /api/departments/active` - Get active departments
- `GET /api/departments/parent/{parentId}` - Get sub-departments
- `PUT /api/departments/{id}` - Update department (ADMIN/SUPER_ADMIN only)
- `DELETE /api/departments/{id}` - Delete department (ADMIN/SUPER_ADMIN only)

### Leave Management (`/api/leaves`)

- `POST /api/leaves` - Apply for leave
- `GET /api/leaves/{id}` - Get leave by ID
- `GET /api/leaves/employee/{employeeId}` - Get leaves by employee
- `GET /api/leaves/pending` - Get pending leaves (MANAGER+)
- `POST /api/leaves/{id}/approve` - Approve/reject leave (MANAGER+)
- `POST /api/leaves/{id}/cancel` - Cancel leave (own leaves only)
- `GET /api/leaves/balance/{employeeId}` - Get leave balances

### Attendance Management (`/api/attendance`)

- `POST /api/attendance/clock-in` - Clock in
- `POST /api/attendance/clock-out` - Clock out
- `GET /api/attendance/today` - Get today's attendance
- `GET /api/attendance/{id}` - Get attendance by ID
- `GET /api/attendance/employee/{employeeId}` - Get attendance history
- `GET /api/attendance/report?employeeId=...&startDate=...&endDate=...` - Get attendance report
- `GET /api/attendance/late?employeeId=...&startDate=...&endDate=...` - Get late attendances
- `GET /api/attendance/early?employeeId=...&startDate=...&endDate=...` - Get early departures

### Payroll Management (`/api/payroll`)

- `POST /api/payroll` - Create payroll (HR/PAYROLL_ADMIN only)
- `GET /api/payroll/{id}` - Get payroll by ID
- `GET /api/payroll/employee/{employeeId}` - Get payrolls by employee
- `GET /api/payroll/period?month=...&year=...` - Get payrolls by period
- `POST /api/payroll/{id}/process` - Process payroll
- `POST /api/payroll/{id}/mark-paid` - Mark payroll as paid
- `GET /api/payroll/{id}/payslip` - Download payslip PDF

### Notification Management (`/api/notifications`)

- `POST /api/notifications` - Create notification
- `GET /api/notifications/{id}` - Get notification by ID
- `GET /api/notifications/recipient/{recipientId}` - Get notifications by recipient
- `GET /api/notifications` - Get all notifications
- `PUT /api/notifications/{id}/read` - Mark notification as read

## Security

### JWT Authentication

All requests (except `/api/auth/**`) must include a JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

### Role-Based Access Control

- **SUPER_ADMIN**: Full access to all resources
- **ADMIN**: Full access to most resources (except super admin functions)
- **MANAGER**: Access to team members and own resources
- **EMPLOYEE**: Access to own resources only

### API Gateway

The API Gateway:
- Validates JWT tokens for all requests except `/api/auth/**`
- Adds `X-User-Id`, `X-User-Email`, and `X-User-Role` headers to downstream services
- Routes requests to appropriate microservices using Eureka service discovery

## Features

### Employee Service
- Auto-generated employee IDs (EMP001, EMP002, ...)
- Pagination and search
- Role-based access control
- Manager-employee relationships

### Department Service
- Department hierarchy with parent-child relationships
- Department codes
- Active/inactive status

### Leave Service
- Multiple leave types (Annual, Sick, Casual, etc.)
- Leave balance tracking
- Approval workflow (Employee → Manager → HR)
- Overlap detection

### Attendance Service
- Clock-in/clock-out functionality
- Late arrival detection
- Early departure tracking
- Attendance reports
- Total hours calculation

### Payroll Service
- Salary components (base, allowances, bonus, overtime)
- Automatic tax calculation
- Provident Fund calculation
- PDF payslip generation
- Monthly payroll processing

### Notification Service
- Multiple channels (Email, SMS, In-App)
- Notification status tracking
- RabbitMQ ready for async processing

## Project Structure

```
hcms-parent/
├── eureka-server/
├── api-gateway/
├── auth-service/
├── employee-service/
├── department-service/
├── leave-service/
├── attendance-service/
├── payroll-service/
└── notification-service/
```

Each service follows this structure:
```
service-name/
├── src/main/java/com/hcms/servicename/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── config/
│   ├── security/
│   ├── exception/
│   └── ServiceNameApplication.java
└── src/main/resources/
    └── application.yml
```

## Configuration

### JWT Secret

All services use the same JWT secret for token validation. Default: `HCMSSecretKeyForJWTTokenGenerationAndValidation2024`

Update in:
- `api-gateway/src/main/resources/application.yml`
- `auth-service/src/main/resources/application.yml`

### Database Configuration

Each service's `application.yml` contains database configuration. Update as needed for your environment.

## Testing

### Login Example

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "superadmin@hcms.com",
    "password": "admin123"
  }'
```

### Create Employee Example

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
   -d '{
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phoneNumber": "+1234567890",
      "dateOfBirth": "1990-01-01",
      "gender": "MALE",
      "address": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA",
      "hireDate": "2024-01-01",
      "designation": "Software Engineer",
      "departmentId": 1
   }'
```

## Production Considerations

1. **Security**:
   - Change default JWT secret
   - Use strong database passwords
   - Enable HTTPS
   - Configure CORS properly

2. **Database**:
   - Use connection pooling
   - Set up database backups
   - Configure proper indexes

3. **Monitoring**:
   - Add Spring Boot Actuator
   - Set up logging aggregation
   - Monitor service health

4. **Scalability**:
   - Use load balancers
   - Configure multiple instances
   - Use message queues for async operations

5. **Notifications**:
   - Integrate with email service (SendGrid, AWS SES)
   - Integrate with SMS service (Twilio, AWS SNS)
   - Configure RabbitMQ for async processing

## License

This project is provided as-is for educational and development purposes.

## Support

For issues or questions, please refer to the codebase documentation or create an issue in the repository.



