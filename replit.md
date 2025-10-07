# Attendify - Smart University Attendance System

## Overview

Attendify is a modern, digital university attendance management system designed to replace traditional paper-based attendance tracking. The application enables students to check in using QR codes, instructors to manage courses and attendance sessions, and administrators to oversee the entire system. Built as a full-stack web application, it uses a React frontend with TypeScript, Express.js backend, PostgreSQL database via Drizzle ORM, and Replit authentication for secure user management.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture

**Framework & Build Tools**
- React 18 with TypeScript for type-safe component development
- Vite as the build tool and development server for fast hot module replacement
- Wouter for lightweight client-side routing
- TailwindCSS for utility-first styling with a custom theme based on green (#28A745) primary color, blue (#007BFF) secondary, and yellow accents

**State Management & Data Fetching**
- TanStack Query (React Query) for server state management, caching, and data synchronization
- Custom query client configuration with retry policies and stale time settings
- React Hook Form with Zod validation for form state and validation

**UI Component System**
- Radix UI primitives for accessible, unstyled component foundations
- shadcn/ui design system (New York style variant) with custom theming
- Component aliases configured for clean imports (@/components, @/lib, @/hooks)
- Dark mode support with CSS custom properties

**Key Design Patterns**
- Role-based UI rendering (Student, Instructor, Admin dashboards)
- Protected routes with authentication checks and automatic redirects
- Optimistic UI updates with query invalidation on mutations
- Toast notifications for user feedback

### Backend Architecture

**Server Framework**
- Express.js with TypeScript for the API layer
- ESM module system throughout the codebase
- RESTful API design with role-based access control

**Authentication & Authorization**
- OpenID Connect (OIDC) integration with Replit Auth
- Passport.js strategy for authentication flow
- Session-based authentication using express-session
- PostgreSQL session store (connect-pg-simple) for persistent sessions
- Custom `isAuthenticated` middleware for route protection

**API Structure**
- User management endpoints (GET users, students, instructors with role filtering)
- Course CRUD operations with instructor assignment
- Enrollment management linking students to courses
- Session management with QR code generation (using qrcode library)
- Attendance record tracking with timestamp validation

**Data Layer Patterns**
- Storage abstraction layer (IStorage interface) for database operations
- Drizzle ORM for type-safe database queries
- Schema-first approach with Zod validation schemas derived from Drizzle tables
- Transaction support for complex operations

### Data Storage

**Database Technology**
- PostgreSQL as the primary database
- Neon serverless PostgreSQL for cloud deployment
- WebSocket-based connection pooling

**Schema Design**
- **Users Table**: Extended with role (student/instructor/admin), studentId, department fields beyond basic Replit Auth profile
- **Courses Table**: Code, name, description, instructor reference, semester/year tracking
- **Enrollments Table**: Many-to-many relationship between students and courses
- **Sessions Table**: QR code-based attendance sessions with start/end times, course reference
- **Attendance Records Table**: Tracking student check-ins with timestamps and status
- **Sessions Storage Table**: Required for express-session with PostgreSQL backend

**Data Relationships**
- One-to-many: Instructor to Courses
- Many-to-many: Students to Courses (via Enrollments)
- One-to-many: Courses to Sessions
- Many-to-many: Students to Sessions (via Attendance Records)

**Migration Strategy**
- Drizzle Kit for schema migrations with PostgreSQL dialect
- Migration files stored in `/migrations` directory
- Schema source of truth in `shared/schema.ts`

### External Dependencies

**Authentication Service**
- Replit OIDC provider for user authentication
- Issuer URL: `https://replit.com/oidc` (configurable via REPLIT_DOMAINS environment variable)
- Session secret management via environment variables

**Database Service**
- Neon PostgreSQL database (accessed via DATABASE_URL environment variable)
- Connection pooling through @neondatabase/serverless
- WebSocket support for serverless environments

**Third-Party Libraries**
- QRCode.js for generating attendance session QR codes
- html5-qrcode for client-side QR code scanning
- date-fns for date manipulation and formatting
- memoizee for OIDC configuration caching

**Development Tools**
- Replit-specific plugins for development environment integration
- Vite plugins: runtime error modal, cartographer (dev mode), dev banner
- TypeScript with strict mode for type safety
- ESBuild for production server bundling

**UI Component Dependencies**
- 20+ Radix UI primitive packages for accessible components
- class-variance-authority for component variant management
- cmdk for command palette functionality
- react-day-picker for calendar components
- recharts for analytics and reporting visualizations