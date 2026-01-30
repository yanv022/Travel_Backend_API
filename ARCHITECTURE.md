# Architecture - Finexs Voyages Backend

## Vue d'ensemble

```
Backend Spring Boot 3
├── Database PostgreSQL
├── API REST
├── JWT Authentication
└── Role-Based Access Control
```

## Structure du projet

```
com/finexs/voyages/
│
├── FinexsVoyagesApplication.java          # Point d'entrée
│
├── controller/                            # Endpoints REST
│   ├── HealthController.java
│   ├── AuthController.java
│   └── RouteController.java
│
├── service/                               # Logique métier
│   ├── AuthService.java
│   └── RouteService.java
│
├── repository/                            # Data Access Layer (JPA)
│   ├── UserRepository.java
│   └── RouteRepository.java
│
├── entity/                                # Modèles JPA
│   ├── User.java
│   ├── UserRole.java (enum)
│   └── Route.java
│
├── dto/                                   # Data Transfer Objects
│   ├── AuthRequest.java
│   ├── RegisterRequest.java
│   ├── AuthResponse.java
│   ├── UserDto.java
│   └── RouteDto.java
│
├── security/                              # Authentification & Autorisation
│   ├── JwtProvider.java
│   └── JwtAuthenticationFilter.java
│
└── config/                                # Configuration
    ├── CorsConfig.java
    ├── SecurityConfig.java
    └── DataInitializer.java
```

## Flux de requête

### 1. Requête HTTP

```
Client (Frontend React)
       ↓
  HTTP Request
       ↓
Spring DispatcherServlet
```

### 2. CORS Interceptor

```
CorsConfigurationSource
  ├── Vérifie l'origine
  ├── Valide les méthodes HTTP
  └── Ajoute les headers de réponse
```

### 3. Chaîne de sécurité (Security Filter Chain)

```
JwtAuthenticationFilter (OncePerRequestFilter)
  ├── Extrait le Bearer Token du header Authorization
  ├── Valide le token avec JwtProvider
  ├── Crée UsernamePasswordAuthenticationToken
  ├── Charge les autorités (ROLE_ADMIN, ROLE_MANAGER, ROLE_TRAVELER)
  └── Stocke dans SecurityContext
       ↓
HttpSecurity authorizeHttpRequests()
  ├── /health → permitAll()
  ├── /auth/register, /auth/login → permitAll()
  ├── /routes GET → permitAll()
  ├── /routes POST, PUT, DELETE → hasRole("MANAGER")
  ├── /auth/me → authenticated()
  └── Autres → authenticated()
```

### 4. Controller

```
@RestController
@RequestMapping("/routes")
public class RouteController {
  @GetMapping
  public ResponseEntity<List<RouteDto>> getAllRoutes()
}
```

- Route les requêtes vers les services appropriés
- Valide les inputs (@Valid, @RequestBody)
- Formatte les réponses (DTO)

### 5. Service

```
RouteService {
  - Logique métier
  - Transactions
  - Conversions Entity ↔ DTO
  - Gestion des erreurs
}
```

### 6. Repository

```
@Repository
public interface RouteRepository extends JpaRepository<Route, Long>
```

- Requêtes à la base de données
- Hibernate/JPA gère la persistance

### 7. Database

```
PostgreSQL
  ├── users table
  ├── routes table
  └── route_amenities table (ElementCollection)
```

### 8. Réponse HTTP

```
HTTP/1.1 200 OK
Content-Type: application/json
Access-Control-Allow-Origin: http://localhost:3000

{
  "id": 1,
  "departureCity": "Douala",
  ...
}
```

## Diagramme de classe principal

```
┌─────────────────────────────────────────────────┐
│                     User                        │
├─────────────────────────────────────────────────┤
│ - id: Long (PK)                                │
│ - name: String                                 │
│ - email: String (UNIQUE)                       │
│ - password: String (BCrypt)                    │
│ - role: UserRole (ENUM)                        │
│ - createdAt: Long                              │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│                    Route                        │
├─────────────────────────────────────────────────┤
│ - id: Long (PK)                                │
│ - departureCity: String                        │
│ - arrivalCity: String                          │
│ - departureTime: LocalDateTime                 │
│ - arrivalTime: LocalDateTime                   │
│ - duration: Integer                            │
│ - company: String                              │
│ - amenities: List<String> (@ElementCollection)│
│ - createdAt: Long                              │
│ - updatedAt: Long                              │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│                 UserRole                        │
├─────────────────────────────────────────────────┤
│ << enum >>                                      │
│ - ADMIN                                        │
│ - MANAGER                                      │
│ - TRAVELER                                     │
└─────────────────────────────────────────────────┘
```

## Authentification JWT

### Génération du token

```
1. User login avec email + password
   ↓
2. AuthService.login()
   - Récupère User de la DB
   - Valide password avec BCryptPasswordEncoder
   ↓
3. JwtProvider.generateToken()
   - Crée JWT signé avec clé secrète
   - Subject = email
   - Claims = { role }
   - Expiration = now + 24h
   ↓
4. Retourne token au client
   AuthResponse { token, user, ... }
```

### Validation du token

```
Client envoie requête avec:
  Authorization: Bearer eyJhbGci...
       ↓
JwtAuthenticationFilter.doFilterInternal()
  - Extrait token du header
  - Appelle JwtProvider.validateToken()
    - Vérifie signature avec clé secrète
    - Vérifie expiration
    - Retourne true/false
  ↓
Si valide:
  - Extrait email et role du token
  - Crée UsernamePasswordAuthenticationToken
  - Ajoute au SecurityContext
  ↓
SecurityConfig.authorizeHttpRequests()
  - Vérifie si l'utilisateur a les rôles requis
  - Autorisation ou 403 Forbidden
```

### Structure du JWT

```
Header:     { "alg": "HS256", "typ": "JWT" }
Payload:    {
              "sub": "user@example.com",
              "role": "TRAVELER",
              "iat": 1706...
              "exp": 1707...
            }
Signature:  HMACSHA256(secret_key)
```

## Sécurité

### BCrypt Password Encoding

```
Plaintext: "password123"
     ↓
BCryptPasswordEncoder.encode()
     ↓
Stored: "$2a$10$wZWFZCqK4..."
     ↓
Validation:
  passwordEncoder.matches(plaintext, stored) → true/false
```

### CORS

```
Frontend: http://localhost:3000
Backend: http://localhost:8080

Frontend request:
  GET /api/routes
  Origin: http://localhost:3000
       ↓
CorsConfigurationSource valide
       ↓
Response headers:
  Access-Control-Allow-Origin: http://localhost:3000
  Access-Control-Allow-Methods: GET, POST, ...
  Access-Control-Allow-Headers: Content-Type, Authorization, ...
```

### Role-Based Access Control (RBAC)

```
SecurityConfig.filterChain()

@PostMapping("/routes")
  .requestMatchers(HttpMethod.POST, "/api/routes")
  .hasRole("MANAGER")
       ↓
User avec role TRAVELER → 403 Forbidden
User avec role MANAGER → 200 OK
```

## Extension future (Étapes 3+)

### Booking (Étape 3)
```
Entity: Booking {
  - id: Long
  - user: User (FK)
  - route: Route (FK)
  - seats: Integer
  - status: BookingStatus (PENDING, CONFIRMED, CANCELLED)
  - createdAt: Long
}

Endpoints:
  POST /bookings
  GET /bookings/{id}
  GET /bookings (my bookings)
  PUT /bookings/{id}/cancel
```

### Payments (Étape 4)
```
Entity: Payment {
  - id: Long
  - booking: Booking (FK)
  - amount: BigDecimal
  - status: PaymentStatus (PENDING, COMPLETED, FAILED)
  - transactionId: String
  - createdAt: Long
}

Endpoints:
  POST /payments
  GET /payments/{id}
```

### Reviews (Étape 5)
```
Entity: Review {
  - id: Long
  - user: User (FK)
  - route: Route (FK)
  - rating: Integer (1-5)
  - comment: String
  - createdAt: Long
}

Endpoints:
  POST /routes/{id}/reviews
  GET /routes/{id}/reviews
```

## Dépendances principales

| Dépendance | Version | Raison |
|-----------|---------|--------|
| spring-boot-starter-web | 3.2.0 | HTTP endpoints |
| spring-boot-starter-data-jpa | 3.2.0 | ORM / Database |
| spring-boot-starter-security | 3.2.0 | Authentification |
| jjwt | 0.12.3 | JWT tokens |
| postgresql | 42.7.1 | Database driver |
| spring-boot-starter-validation | 3.2.0 | Validation input |
| lombok | 1.18.x | Code generation |

## Bonnes pratiques appliquées

✅ **Séparation des préoccupations**
  - Controller ≠ Service ≠ Repository

✅ **DTOs**
  - Jamais exposer les Entities directement
  - Contrôle de ce qui est envoyé au client

✅ **Validation**
  - @Valid sur les DTOs
  - Validation au niveau du binding

✅ **Gestion des erreurs**
  - Try/catch dans les services
  - Messages d'erreur cohérents

✅ **Sécurité**
  - Passwords hashés avec BCrypt
  - JWT stateless (pas de sessions)
  - CORS restrictif
  - Autorisation par rôle

✅ **Scalabilité**
  - Stateless (facile à clusteriser)
  - Database relationnelle
  - Indexation sur les clés étrangères

## Monitoring & Debugging

### Logs

```yaml
logging:
  level:
    root: INFO
    com.finexs: DEBUG
    org.springframework.security: DEBUG
```

Activer en local pour déboguer les problèmes JWT

### Métriques

Ajouter (optionnel) :
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Endpoints :
- GET /actuator/health
- GET /actuator/metrics
- GET /actuator/env
