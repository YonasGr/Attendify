# Attendify Android - Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                   │
│                    (Jetpack Compose)                     │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Student    │  │  Instructor  │  │    Admin     │  │
│  │  Dashboard   │  │  Dashboard   │  │  Dashboard   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │          │
│         └─────────────────┼──────────────────┘          │
│                           │                             │
│                  ┌────────▼────────┐                    │
│                  │  Login Screen   │                    │
│                  └────────┬────────┘                    │
│                           │                             │
└───────────────────────────┼─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                     VIEWMODEL LAYER                      │
│                  (Business Logic)                        │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │    Login     │  │   Student    │  │  Instructor  │  │
│  │  ViewModel   │  │  ViewModel   │  │  ViewModel   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │          │
│         └─────────────────┼──────────────────┘          │
│                           │                             │
└───────────────────────────┼─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                    REPOSITORY LAYER                      │
│                  (Data Abstraction)                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────┐        ┌──────────────────┐      │
│  │  AuthRepository  │        │ AttendifyRepository│    │
│  │  (DataStore)     │        │   (API + Cache)    │    │
│  └────────┬─────────┘        └────────┬──────────┘      │
│           │                           │                 │
│           └───────────────┬───────────┘                 │
│                           │                             │
└───────────────────────────┼─────────────────────────────┘
                            │
           ┌────────────────┼────────────────┐
           │                │                │
┌──────────▼─────┐  ┌───────▼──────┐  ┌─────▼──────────┐
│   DataStore    │  │   Retrofit   │  │  Local Cache   │
│  (Preferences) │  │   (Network)  │  │  (Future)      │
└────────────────┘  └───────┬──────┘  └────────────────┘
                            │
                    ┌───────▼───────┐
                    │    Backend    │
                    │   API Server  │
                    │  (Express.js) │
                    └───────────────┘
```

## Data Flow

### Authentication Flow
```
User Input → LoginScreen → LoginViewModel → AuthRepository → DataStore
                                    ↓
                          AttendifyRepository → API → Backend
                                    ↓
                          User Data ← Cache ← Response
                                    ↓
                          AuthState Update → UI Refresh
```

### Data Loading Flow
```
UI Event → ViewModel → Repository → API Call
                           ↓
                    Resource.Loading → UI (Loading State)
                           ↓
                    Resource.Success/Error → UI Update
```

### Navigation Flow
```
App Start → Check Auth → AuthState
                              ↓
                 ┌────────────┼────────────┐
                 ▼            ▼            ▼
            Student      Instructor     Admin
           Dashboard     Dashboard    Dashboard
```

## Component Breakdown

### 1. UI Layer (Compose)
**Responsibility**: Display data and capture user input

**Components**:
- `LoginScreen`: Authentication UI
- `StudentDashboardScreen`: Student features
- `InstructorDashboardScreen`: Instructor features
- `AdminDashboardScreen`: Admin features
- `AttendifyApp`: Navigation host
- Theme components (Color, Typography, Theme)

**Pattern**: Stateless composables that observe StateFlow

### 2. ViewModel Layer
**Responsibility**: Handle business logic and UI state

**Components**:
- `LoginViewModel`: Authentication logic
- (Future) `StudentViewModel`: Student-specific logic
- (Future) `InstructorViewModel`: Instructor-specific logic
- (Future) `AdminViewModel`: Admin-specific logic

**Pattern**: MVVM with StateFlow for reactive state

### 3. Repository Layer
**Responsibility**: Abstract data sources

**Components**:
- `AuthRepository`: Authentication persistence
- `AttendifyRepository`: API operations wrapper

**Pattern**: Repository pattern with Flow-based API

### 4. Data Sources
**Components**:
- `AttendifyApiService`: Retrofit API interface
- `DataStore`: Local preferences storage
- (Future) Room DB: Local caching

### 5. Dependency Injection
**Component**: `AppModule` (Hilt)

**Provides**:
- Retrofit instance
- API service
- Repositories
- Gson, OkHttp
- Auth interceptor

## Key Design Patterns

### MVVM (Model-View-ViewModel)
```
View (Compose)
    ↕ (observes StateFlow)
ViewModel
    ↕ (calls methods)
Repository
    ↕ (fetches data)
Data Source (API/DB)
```

### Repository Pattern
```
Multiple Data Sources → Single Repository Interface → ViewModels
```

### Reactive State Management
```
Flow/StateFlow → collect → Update UI
```

### Resource Wrapper
```
sealed class Resource<T> {
    Loading → Show progress
    Success(data) → Display data
    Error(message) → Show error
}
```

## Navigation Structure

```
AttendifyApp (NavHost)
├── Screen.Login
├── Screen.StudentDashboard
├── Screen.InstructorDashboard
├── Screen.AdminDashboard
└── (Future screens)
    ├── Screen.CourseDetails
    ├── Screen.SessionDetails
    └── Screen.QRScanner
```

## State Management

### Authentication State
```kotlin
data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)
```

### Resource State
```kotlin
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
}
```

## API Integration

### Request Flow
```
ViewModel.function()
    ↓
Repository.operation()
    ↓
Flow emission (Loading)
    ↓
API.endpoint() [Retrofit]
    ↓
OkHttp + Auth Interceptor
    ↓
Backend Server
    ↓
Response
    ↓
Flow emission (Success/Error)
    ↓
ViewModel.state update
    ↓
UI re-composition
```

## Security Considerations

### Current Implementation
- Session tokens stored in DataStore (encrypted)
- HTTPS for API calls
- Auth interceptor adds tokens automatically
- No sensitive data in logs (production)

### Future Enhancements
- Biometric authentication
- Certificate pinning
- Token refresh mechanism
- Encrypted local database

## Testing Strategy

### Unit Tests (Future)
- ViewModel logic
- Repository operations
- Use cases

### Integration Tests (Future)
- API service calls
- Repository with fake API
- End-to-end user flows

### UI Tests (Future)
- Compose UI testing
- Navigation testing
- User interaction scenarios

## Performance Optimizations

### Current
- Lazy loading of screens
- Efficient recomposition with StateFlow
- Image caching with Coil

### Future
- Database caching (Room)
- Pagination for lists
- Image optimization
- Background sync
- WorkManager for tasks

## Module Dependencies

```
app
├── Kotlin Stdlib
├── Android Core KTX
├── Jetpack Compose (BOM)
│   ├── UI
│   ├── Material3
│   └── Navigation
├── Lifecycle
│   ├── ViewModel
│   └── Runtime Compose
├── Hilt (DI)
├── Retrofit (Networking)
│   ├── Gson Converter
│   └── OkHttp
├── DataStore
├── ZXing (QR Codes)
└── Coil (Images)
```

## File Size Breakdown

### Current APK Size (Debug)
- ~15-20 MB (estimated)
- Compose libraries: ~8 MB
- Hilt: ~2 MB
- Retrofit/OkHttp: ~2 MB
- Other dependencies: ~3 MB
- App code: ~5 MB

### Optimization Opportunities
- ProGuard/R8 minification (50% reduction)
- Remove unused resources
- Enable code shrinking
- Use APK splits by ABI
- Use Android App Bundle

## Scalability Considerations

### Code Organization
- Feature-based modules (future)
- Shared common module
- Clear separation of concerns
- Consistent naming conventions

### Performance
- Pagination for large lists
- Efficient image loading
- Background processing
- Database indexing

### Maintainability
- Well-documented code
- Consistent architecture
- Testable components
- Clear error handling

## Common Patterns Used

### 1. Single Responsibility
Each class has one clear purpose

### 2. Dependency Injection
All dependencies injected, not created

### 3. Reactive Programming
Flow-based reactive state management

### 4. Sealed Classes
For representing state hierarchies

### 5. Coroutines
For async operations

### 6. Composition over Inheritance
Jetpack Compose encourages composition

## Future Architecture Enhancements

### Offline-First Architecture
```
UI ← ViewModel ← Repository
                     ↓
              ┌──────┴──────┐
              ▼             ▼
          Local DB        API
       (Single Source)  (Sync)
```

### Multi-Module Setup
```
:app
:feature-student
:feature-instructor
:feature-admin
:core-ui
:core-data
:core-network
```

### Clean Architecture Layers
```
Presentation → Domain ← Data
   (UI)      (Use Cases) (Repos)
```

This architecture provides a solid foundation for building a scalable, maintainable, and performant Android application!
