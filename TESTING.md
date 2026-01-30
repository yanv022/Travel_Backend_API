# Guide de Test - Finexs Voyages Backend

## Outils recommandés

- **Postman** : https://www.postman.com/downloads/
- **Insomnia** : https://insomnia.rest/
- **cURL** : Inclus dans les systèmes Unix/macOS
- **HTTPie** : `brew install httpie` ou `pip install httpie`

## Cas de test

### Étape 0 - Bootstrap

#### Test 1: Health Check

```bash
curl http://localhost:8080/api/health
```

**Résultat attendu (200 OK) :**
```json
{
  "status": "ok"
}
```

---

### Étape 1 - Authentification JWT

#### Test 2: Register nouvel utilisateur

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jean Dupont",
    "email": "jean@example.com",
    "password": "password123"
  }'
```

**Résultat attendu (200 OK) :**
```json
{
  "id": 4,
  "name": "Jean Dupont",
  "email": "jean@example.com",
  "role": "TRAVELER",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Test 3: Erreur - Email déjà existant

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Autre User",
    "email": "jean@example.com",
    "password": "password123"
  }'
```

**Résultat attendu (500 Internal Server Error) :**
```json
{
  "timestamp": "...",
  "status": 500,
  "error": "Internal Server Error",
  "message": "User already exists"
}
```

#### Test 4: Login utilisateur existant

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "traveler@email.com",
    "password": "password123"
  }'
```

**Résultat attendu (200 OK) :**
```json
{
  "id": 3,
  "name": "Traveler User",
  "email": "traveler@email.com",
  "role": "TRAVELER",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**SAUVEGARDER LE TOKEN pour les tests suivants**

#### Test 5: Erreur - Mot de passe incorrect

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "traveler@email.com",
    "password": "mauvais"
  }'
```

**Résultat attendu (500 Internal Server Error) :**
```json
{
  "message": "Invalid password"
}
```

#### Test 6: GET /auth/me - protégé JWT

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Résultat attendu (200 OK) :**
```json
{
  "id": 3,
  "name": "Traveler User",
  "email": "traveler@email.com",
  "role": "TRAVELER"
}
```

#### Test 7: Erreur - Token manquant

```bash
curl http://localhost:8080/api/auth/me
```

**Résultat attendu (403 Forbidden)** : Pas d'accès sans token

#### Test 8: Erreur - Token invalide

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer invalid_token"
```

**Résultat attendu (403 Forbidden)**

---

### Étape 2 - Routes

#### Test 9: GET /routes - public

```bash
curl http://localhost:8080/api/routes
```

**Résultat attendu (200 OK) - Liste des routes:**
```json
[
  {
    "id": 1,
    "departureCity": "Douala",
    "arrivalCity": "Yaoundé",
    "departureTime": "2024-02-01T12:30:00",
    "arrivalTime": "2024-02-01T15:30:00",
    "duration": 180,
    "company": "Finexs Voyages",
    "amenities": ["WiFi", "Climatisation", "Toilettes"]
  },
  ...
]
```

#### Test 10: GET /routes/{id}

```bash
curl http://localhost:8080/api/routes/1
```

**Résultat attendu (200 OK) :**
```json
{
  "id": 1,
  "departureCity": "Douala",
  "arrivalCity": "Yaoundé",
  ...
}
```

#### Test 11: POST /routes - créer route (MANAGER)

```bash
# D'abord, login en tant que MANAGER
MANAGER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@finexs.com","password":"password123"}' \
  | jq -r '.token')

# Ensuite créer la route
curl -X POST http://localhost:8080/api/routes \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "departureCity": "Bafoussam",
    "arrivalCity": "Yaoundé",
    "departureTime": "2024-02-02T08:00:00",
    "arrivalTime": "2024-02-02T14:00:00",
    "duration": 360,
    "company": "Finexs Voyages",
    "amenities": ["WiFi", "Climatisation", "Toilettes", "Repas"]
  }'
```

**Résultat attendu (201 Created) :**
```json
{
  "id": 5,
  "departureCity": "Bafoussam",
  "arrivalCity": "Yaoundé",
  ...
}
```

#### Test 12: Erreur - Créer route sans permission (TRAVELER)

```bash
TRAVELER_TOKEN="<token_traveler>"

curl -X POST http://localhost:8080/api/routes \
  -H "Authorization: Bearer $TRAVELER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "departureCity": "Douala",
    "arrivalCity": "Yaoundé",
    ...
  }'
```

**Résultat attendu (403 Forbidden)**

#### Test 13: PUT /routes/{id} - modifier (MANAGER)

```bash
curl -X PUT http://localhost:8080/api/routes/1 \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "departureCity": "Douala",
    "arrivalCity": "Yaoundé",
    "departureTime": "2024-02-01T10:00:00",
    "arrivalTime": "2024-02-01T13:00:00",
    "duration": 180,
    "company": "Finexs Voyages",
    "amenities": ["WiFi", "Climatisation", "Toilettes", "Snacks"]
  }'
```

**Résultat attendu (200 OK)**

#### Test 14: DELETE /routes/{id} - supprimer (MANAGER)

```bash
curl -X DELETE http://localhost:8080/api/routes/1 \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

**Résultat attendu (204 No Content)**

#### Test 15: Erreur - Route non trouvée

```bash
curl http://localhost:8080/api/routes/9999
```

**Résultat attendu (500 Internal Server Error) - "Route not found"**

---

## Script de test automatisé (bash)

Créer un fichier `test.sh` :

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"

echo "=== Test 1: Health Check ==="
curl $BASE_URL/health
echo ""

echo "=== Test 2: Get Routes ==="
curl $BASE_URL/routes
echo ""

echo "=== Test 3: Login ==="
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"traveler@email.com","password":"password123"}')

TOKEN=$(echo $RESPONSE | jq -r '.token')
echo "Token: $TOKEN"
echo ""

echo "=== Test 4: Get Current User ==="
curl -H "Authorization: Bearer $TOKEN" $BASE_URL/auth/me
echo ""

echo "=== Test 5: Get First Route ==="
curl $BASE_URL/routes/1
echo ""
```

Exécuter :
```bash
chmod +x test.sh
./test.sh
```

## Checklist de succès

- [x] GET /health retourne 200 + {"status": "ok"}
- [x] POST /auth/register crée un utilisateur
- [x] POST /auth/login retourne un token JWT
- [x] GET /auth/me fonctionne avec token valide
- [x] GET /auth/me retourne 403 sans token
- [x] GET /routes public (pas de token requis)
- [x] GET /routes/{id} retourne une route
- [x] POST /routes crée une route (MANAGER seulement)
- [x] PUT /routes/{id} modifie une route (MANAGER seulement)
- [x] DELETE /routes/{id} supprime une route (MANAGER seulement)
- [x] Permissions respectées par rôle (TRAVELER ne peut pas créer)

## Intégration avec le frontend React

Le frontend React devrait :

```typescript
// src/config/api.ts
export const API_URL = 'http://localhost:8080/api';

// Login
const response = await fetch(`${API_URL}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});

const { token } = await response.json();
localStorage.setItem('token', token);

// Requête authentifiée
const routes = await fetch(`${API_URL}/routes`, {
  headers: { 'Authorization': `Bearer ${token}` }
});
```
