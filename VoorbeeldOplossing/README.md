# Voorbeeldoplossing: REST API met CRUD Operaties

## Overzicht

Dit is een complete voorbeeldoplossing voor de oefening over het maken van een REST API met CRUD operaties voor blogposts. De applicatie is gebouwd met Spring Boot en gebruikt in-memory opslag.

## Structuur

```
src/main/java/be/ugent/blog/
├── BlogApplication.java          # Main Spring Boot applicatie
├── BlogPost.java                 # Domain model klasse
├── BlogPostDaoMemory.java        # Data Access Object (in-memory)
└── BlogController.java           # REST Controller met alle endpoints
```

## Klassen Uitleg

### 1. BlogPost (Domain Model)
- **Doel**: Representeert een blogpost
- **Attributen**: 
  - `id` (Long): Uniek identificatienummer
  - `titel` (String): Titel van de post
  - `content` (String): Inhoud van de post
- **Bevat**: Default constructor, getters en setters

### 2. BlogPostDaoMemory (Data Access Object)
- **Annotatie**: `@Service` - Maakt dit een Spring bean
- **Doel**: Beheert blogposts in het geheugen (simuleert database)
- **Functionaliteit**:
  - Initialiseert met "Hello World" post
  - `getAllPosts()`: Alle posts ophalen
  - `getPostById(Long id)`: Specifieke post ophalen
  - `addPost(BlogPost post)`: Nieuwe post toevoegen
  - `updatePost(Long id, BlogPost post)`: Post updaten
  - `deletePost(Long id)`: Post verwijderen
  - `exists(Long id)`: Controleren of post bestaat

### 3. BlogController (REST Controller)
- **Annotatie**: `@RestController` - Markeert als REST endpoint handler
- **Base URL**: `/posts`
- **Dependency Injection**: Constructor injection van `BlogPostDaoMemory`

#### Endpoints:

| HTTP Methode | URL | Beschrijving | Status Code |
|--------------|-----|--------------|-------------|
| GET | `/posts` | Alle posts ophalen | 200 OK |
| GET | `/posts/{id}` | Specifieke post ophalen | 200 OK / 404 Not Found |
| POST | `/posts` | Nieuwe post aanmaken | 201 Created |
| PUT | `/posts/{id}` | Post updaten | 200 OK / 404 Not Found |
| DELETE | `/posts/{id}` | Post verwijderen | 204 No Content / 404 Not Found |

## Applicatie Starten

### Vereisten
- Java 17 of hoger
- Maven

### Commando's

```bash
# Applicatie starten
mvn spring-boot:run

# Of met Maven wrapper (indien beschikbaar)
./mvnw spring-boot:run
```

De applicatie start op `http://localhost:8080`

## API Voorbeelden

### 1. Alle posts ophalen
```bash
curl http://localhost:8080/posts
```

**Response:**
```json
[
  {
    "id": 1,
    "titel": "Hello World",
    "content": "Dit is mijn eerste blogpost!"
  }
]
```

### 2. Specifieke post ophalen
```bash
curl http://localhost:8080/posts/1
```

**Response:**
```json
{
  "id": 1,
  "titel": "Hello World",
  "content": "Dit is mijn eerste blogpost!"
}
```

### 3. Nieuwe post aanmaken
```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{
    "titel": "Mijn tweede post",
    "content": "Dit is de inhoud van mijn tweede post"
  }'
```

**Response:**
- Status: 201 Created
- Location header: `http://localhost:8080/posts/2`
- Body: De aangemaakte post met gegenereerd id

### 4. Post updaten
```bash
curl -X PUT http://localhost:8080/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titel": "Hello World (updated)",
    "content": "Dit is de geüpdatete inhoud!"
  }'
```

**Response:**
```json
{
  "id": 1,
  "titel": "Hello World (updated)",
  "content": "Dit is de geüpdatete inhoud!"
}
```

### 5. Post verwijderen
```bash
curl -X DELETE http://localhost:8080/posts/1
```

**Response:**
- Status: 204 No Content
- Geen body

## Belangrijke Concepten

### Spring Annotaties
- `@SpringBootApplication`: Hoofdapplicatie configuratie
- `@RestController`: Markeert klasse als REST endpoint handler
- `@Service`: Markeert klasse als Spring service bean
- `@RequestMapping`: Definieert base URL voor controller
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: HTTP methode mappings
- `@PathVariable`: Haalt waarde uit URL path
- `@RequestBody`: Deserialiseert JSON naar Java object
- `@ResponseStatus`: Definieert HTTP status code voor response

### Dependency Injection
De controller gebruikt **constructor injection** om de DAO te injecteren:

```java
public BlogController(BlogPostDaoMemory postDao) {
    this.postDao = postDao;
}
```

Spring detecteert automatisch de `@Service` bean en injecteert deze.

### HTTP Status Codes
- **200 OK**: Succesvolle GET/PUT operatie
- **201 Created**: Succesvolle POST operatie (met Location header)
- **204 No Content**: Succesvolle DELETE operatie
- **404 Not Found**: Resource niet gevonden
- **409 Conflict**: Id mismatch tussen URL en body

### Exception Handling
De controller definieert custom exceptions met `@ResponseStatus`:
- `PostNotFoundException`: 404 Not Found
- `IdMismatchException`: 409 Conflict

### REST Principes
1. **Resource-based URLs**: `/posts` en `/posts/{id}`
2. **HTTP methoden**: GET, POST, PUT, DELETE
3. **Stateless**: Elke request bevat alle benodigde informatie
4. **JSON serialisatie**: Automatisch door Spring Boot
5. **Location header**: Bij POST operaties

## Volgende Stappen

Deze voorbeeldoplossing kan uitgebreid worden met:
1. **Validatie**: `@Valid` en `@NotNull`, `@Size` annotaties
2. **Database**: Spring Data JPA met echte database (H2, PostgreSQL)
3. **Security**: Spring Security voor authenticatie en autorisatie
4. **Tests**: Unit tests en integration tests
5. **Paginatie**: Spring Data paginatie ondersteuning
6. **Filtering**: Query parameters voor zoeken en filteren

## Opmerkingen

- Deze oplossing gebruikt **in-memory** opslag. Data gaat verloren bij herstart.
- De applicatie gebruikt **Java 17** als target versie.
- Spring Boot **3.3.4** wordt gebruikt als framework versie.
- Alle CRUD operaties zijn geïmplementeerd volgens REST best practices.
