# Guide d'Installation - Finexs Voyages Backend

## Étapes d'installation complètes

### 1. Prérequis système

```bash
java -version          # Java 17+
mvn -version           # Maven 3.6+
psql --version         # PostgreSQL 12+
```

### 2. Créer la base de données PostgreSQL

```bash
# Connexion à PostgreSQL
psql -U postgres

# Dans le shell PostgreSQL :
CREATE DATABASE finexs_voyages;
\q
```

### 3. Cloner/télécharger le projet

```bash
cd /chemin/vers/le/projet
```

### 4. Configuration (application.yml)

Vérifier `src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finexs_voyages
    username: postgres
    password: <votre-password-postgresql>
```

### 5. Télécharger les dépendances Maven

```bash
mvn clean install
```

Cela va :
- Télécharger toutes les dépendances
- Compiler le projet
- Exécuter les tests (s'il y en a)

### 6. Démarrer l'application

```bash
mvn spring-boot:run
```

Ou :

```bash
mvn package
java -jar target/finexs-voyages-backend-1.0.0.jar
```

### 7. Vérifier que l'application fonctionne

```bash
curl http://localhost:8080/api/health
# Résultat attendu :
# {"status":"ok"}
```

## Troubleshooting

### Error: Could not connect to database

**Solution :**
- Vérifier que PostgreSQL est lancé
- Vérifier les credentials dans application.yml
- Vérifier que la base de données `finexs_voyages` existe

```bash
psql -U postgres -d finexs_voyages -c "SELECT 1;"
```

### Error: Port 8080 already in use

**Solution :** Modifier dans `application.yml`

```yaml
server:
  port: 8081
```

### Error: Could not find or load main class

**Solution :**
```bash
mvn clean install
mvn compile
```

### JWT Secret Warning

En **production**, changer le secret JWT dans `application.yml` :

```yaml
app:
  jwt:
    secret: your-very-secure-secret-key-of-at-least-32-chars
```

## Test des endpoints

### 1. Health check

```bash
curl http://localhost:8080/api/health
```

### 2. Login (utilisateur de seed)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"traveler@email.com","password":"password123"}'
```

Réponse :
```json
{
  "id": 3,
  "name": "Traveler User",
  "email": "traveler@email.com",
  "role": "TRAVELER",
  "token": "eyJhbGc..."
}
```

### 3. Récupérer l'utilisateur courant

```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/auth/me
```

### 4. Lister les routes (public)

```bash
curl http://localhost:8080/api/routes
```

### 5. Créer une route (nécessite MANAGER)

```bash
TOKEN="<token-manager>"

curl -X POST http://localhost:8080/api/routes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "departureCity": "Douala",
    "arrivalCity": "Yaoundé",
    "departureTime": "2024-02-01T10:00:00",
    "arrivalTime": "2024-02-01T13:00:00",
    "duration": 180,
    "company": "Finexs Voyages",
    "amenities": ["WiFi", "Climatisation", "Toilettes"]
  }'
```

## Production Checklist

- [ ] Changer le JWT secret
- [ ] Configurer CORS pour les vrais domaines
- [ ] Activer HTTPS/TLS
- [ ] Configurer les variables d'environnement
- [ ] Tester avec la vraie base de données PostgreSQL
- [ ] Ajouter les logs de sécurité
- [ ] Tester les rate limits si nécessaire
- [ ] Vérifier les permissions de chaque endpoint
- [ ] Mettre en place un système de monitoring
