# Finexs Voyages - Backend Spring Boot

Backend pour l'application de transport interurbain Finexs Voyages, basé sur Spring Boot 3, PostgreSQL et JWT.

## Architecture

```
src/main/java/com/finexs/voyages/
├── controller/          # Endpoints REST
├── service/             # Logique métier
├── repository/          # Accès aux données (JPA)
├── entity/              # Modèles de données
├── dto/                 # Data Transfer Objects
├── security/            # JWT et filtres d'authentification
├── config/              # Configuration (CORS, Security, DataInitializer)
└── FinexsVoyagesApplication.java
```

## Prérequis

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

## Configuration

### 1. PostgreSQL

```bash
createdb finexs_voyages
```

### 2. Environment Variables (.env ou application.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finexs_voyages
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: your-secret-key-here
    expiration: 86400000
```

## Build et Exécution

```bash
mvn clean install
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080/api`

## Endpoints

### ÉTAPE 0 - Bootstrap

```
GET /api/health
→ { "status": "ok" }
```

### ÉTAPE 1 - Authentification

```
POST /api/auth/register
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}

POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
→ { "id": 1, "name": "...", "email": "...", "role": "TRAVELER", "token": "..." }

GET /api/auth/me (Headers: Authorization: Bearer <token>)
→ { "id": 1, "name": "...", "email": "...", "role": "TRAVELER" }
```

**Utilisateurs de seed :**
- admin@system.com (ADMIN)
- manager@finexs.com (MANAGER)
- traveler@email.com (TRAVELER)
- Mot de passe : password123

### ÉTAPE 2 - Routes

```
GET /api/routes
→ [{ "id": 1, "departureCity": "Douala", ... }, ...]

GET /api/routes/{id}
→ { "id": 1, "departureCity": "Douala", ... }

POST /api/routes (Headers: Authorization: Bearer <manager-token>)
{
  "departureCity": "Douala",
  "arrivalCity": "Yaoundé",
  "departureTime": "2024-02-01T10:00:00",
  "arrivalTime": "2024-02-01T13:00:00",
  "duration": 180,
  "company": "Finexs Voyages",
  "amenities": ["WiFi", "Climatisation"]
}

PUT /api/routes/{id} (Headers: Authorization: Bearer <manager-token>)
{...same as POST...}

DELETE /api/routes/{id} (Headers: Authorization: Bearer <manager-token>)
```

**Routes de seed :**
- Douala ↔ Yaoundé (180 min)
- Bafoussam ↔ Douala (240 min)
- Ngaoundéré ↔ Yaoundé (360 min)

## Flux d'authentification JWT

1. **Login/Register** → Token JWT généré
2. **Header Authorization** → `Bearer <token>`
3. **JwtAuthenticationFilter** valide le token
4. **SecurityContext** charge l'utilisateur et ses rôles
5. **@PreAuthorize** / hasRole() protège les endpoints

## CORS

Configuré pour les origins locaux (localhost:3000, 5173, etc.)

## Développement

### Ajouter une nouvelle entity
1. Créer la classe entity avec @Entity, @Table
2. Créer le Repository extends JpaRepository
3. Créer le DTO pour les transferts
4. Implémenter le Service
5. Créer le Controller avec les endpoints

### Seed data
DataInitializer crée automatiquement les utilisateurs et routes au démarrage.

## Sécurité

- Spring Security avec JWT
- Passwords hashés avec BCrypt
- RLS implicite via contrôle d'accès au backend
- CORS restrictif en production

## Dépannage

**Port 8080 déjà utilisé :**
```yaml
server:
  port: 8081
```

**Connexion PostgreSQL échouée :**
Vérifier la base de données et les credentials

**JWT invalide :**
Vérifier la clé secrète en production
