# Frameworks voor Serverapplicaties - Cursus Referentie

## Overzicht

Deze referentie documenteert de progressie en kernconcepten van de cursus Frameworks voor Serverapplicaties. De cursus gebruikt Spring Boot als primair framework en bouwt stapsgewijs op van basis REST APIs naar geavanceerde reactive programming en database toegang.

---

## Reeks 1: Basis REST API met In-Memory Opslag

### Leerdoelen
- Spring Boot applicatie opzetten
- REST endpoints implementeren met `@RestController`
- CRUD operaties uitvoeren met in-memory data

### Kernconcepten

#### 1. Spring Boot Setup
```java
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```

#### 2. REST Controller
```java
@RestController
@RequestMapping("/posts")
public class BlogController {
    
    @GetMapping
    public List<BlogPost> getAllPosts() { ... }
    
    @GetMapping("/{id}")
    public BlogPost getPost(@PathVariable Long id) { ... }
    
    @PostMapping
    public BlogPost createPost(@RequestBody BlogPost post) { ... }
    
    @PutMapping("/{id}")
    public BlogPost updatePost(@PathVariable Long id, @RequestBody BlogPost post) { ... }
    
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) { ... }
}
```

#### 3. Domain Model
```java
public class BlogPost {
    private Long id;
    private String title;
    private String content;
    // getters en setters
}
```

#### 4. In-Memory DAO
```java
@Component
public class BlogPostDaoMemory {
    private List<BlogPost> posts = new ArrayList<>();
    private AtomicLong nextId = new AtomicLong(1);
    
    public List<BlogPost> findAll() { ... }
    public Optional<BlogPost> findById(Long id) { ... }
    public BlogPost save(BlogPost post) { ... }
    public void deleteById(Long id) { ... }
}
```

#### 5. Exception Handling
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("Post not found: " + id);
    }
}
```

### Belangrijke Annotaties
- `@SpringBootApplication` - Configuratie hoofdklasse
- `@RestController` - REST endpoint definitie
- `@RequestMapping` - Basis URL pad
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` - HTTP methoden
- `@PathVariable` - URL parameter
- `@RequestBody` - JSON body naar object
- `@Component` - Spring component registratie
- `@ResponseStatus` - HTTP status code voor exceptions

---

## Reeks 2: Database Integratie met JPA

### Leerdoelen
- JPA entities definiëren
- Spring Data repositories gebruiken
- Database profiles (test vs. productie)
- Meerdere DAO implementaties beheren

### Kernconcepten

#### 1. JPA Entity
```java
@Entity
@Table(name = "blog_posts")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 5000)
    private String content;
    
    // getters en setters
}
```

#### 2. Spring Data Repository
```java
public interface BlogPostRepo extends JpaRepository<BlogPost, Long> {
    // Basis CRUD methoden zijn automatisch beschikbaar
    // Eigen query methoden kunnen toegevoegd worden
    List<BlogPost> findByTitleContaining(String keyword);
}
```

#### 3. DAO Interface Pattern
```java
public interface BlogPostDao {
    List<BlogPost> findAll();
    Optional<BlogPost> findById(Long id);
    BlogPost save(BlogPost post);
    void deleteById(Long id);
}
```

#### 4. Database DAO Implementatie
```java
@Component
@Profile("production")
public class BlogPostDaoDb implements BlogPostDao {
    @Autowired
    private BlogPostRepo repository;
    
    @Override
    public List<BlogPost> findAll() {
        return repository.findAll();
    }
    
    // Andere methoden delegeren naar repository
}
```

#### 5. In-Memory DAO met Profile
```java
@Component
@Profile("test")
public class BlogPostDaoMemory implements BlogPostDao {
    // Zelfde interface, verschillende implementatie
}
```

#### 6. Database Configuratie (application.properties)
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# H2 Console
spring.h2.console.enabled=true

# JPA Properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Belangrijke Annotaties
- `@Entity` - JPA entiteit
- `@Table` - Database tabel naam
- `@Id` - Primary key
- `@GeneratedValue` - Auto-increment strategie
- `@Column` - Kolom configuratie
- `@Profile` - Actieve Spring profile
- `@Autowired` - Dependency injection

### Dependencies (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## Reeks 3: Security & Authentication

### Leerdoelen
- Spring Security configureren
- Authenticatie implementeren
- Role-based authorization
- Method-level security

### Kernconcepten

#### 1. Security Configuratie
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
            
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN", "USER")
            .build();
            
        return new InMemoryUserDetailsManager(user, admin);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### 2. Method-Level Security
```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}

@RestController
public class BlogController {
    
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) { ... }
    
    @PreAuthorize("hasRole('ADMIN') or @blogPostSecurity.isOwner(#id, principal)")
    @PutMapping("/{id}")
    public BlogPost updatePost(@PathVariable Long id, @RequestBody BlogPost post) { ... }
}
```

#### 3. Custom Security Logic
```java
@Component("blogPostSecurity")
public class BlogPostSecurity {
    
    public boolean isOwner(Long postId, Principal principal) {
        // Logica om te controleren of user eigenaar is
        return false;
    }
}
```

### Belangrijke Annotaties
- `@EnableWebSecurity` - Security activeren
- `@EnableMethodSecurity` - Method-level security
- `@Secured` - Role-based toegang
- `@PreAuthorize` - SpEL-based toegangscontrole
- `@PostAuthorize` - Check na method executie

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

---

## Reeks 5: Reactive Programming met WebFlux

### Leerdoelen
- Reactive programming concepten
- Non-blocking I/O
- Reactor Mono en Flux
- MongoDB reactive driver
- Server-Sent Events (SSE)

### Kernconcepten

#### 1. Reactive Controller
```java
@RestController
@RequestMapping("/api/posts")
public class BlogController {
    
    @Autowired
    private BlogPostRepository repository;
    
    @GetMapping
    public Flux<BlogPost> getAllPosts() {
        return repository.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<BlogPost> getPost(@PathVariable String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new PostNotFoundException(id)));
    }
    
    @PostMapping
    public Mono<BlogPost> createPost(@RequestBody BlogPost post) {
        return repository.save(post);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deletePost(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
```

#### 2. Reactive Repository
```java
public interface BlogPostRepository extends ReactiveMongoRepository<BlogPost, String> {
    Flux<BlogPost> findByTitleContaining(String keyword);
}
```

#### 3. MongoDB Entity
```java
@Document(collection = "posts")
public class BlogPost {
    @Id
    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    
    // getters en setters
}
```

#### 4. Server-Sent Events (SSE)
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<BlogPost> streamPosts() {
    return repository.findAll()
        .delayElements(Duration.ofSeconds(1));
}
```

#### 5. Change Streams (MongoDB)
```java
@Service
public class BlogPostService {
    
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    
    public Flux<BlogPost> watchChanges() {
        return mongoTemplate
            .changeStream("posts", BlogPost.class)
            .map(ChangeStreamEvent::getBody);
    }
}
```

### Reactive Types

#### Mono<T>
- 0 of 1 element
- Equivalent van `Optional<T>` of `Future<T>`
- Gebruik voor single value responses

```java
Mono<BlogPost> post = repository.findById(id);
```

#### Flux<T>
- 0 tot N elementen
- Equivalent van `List<T>` of `Stream<T>`
- Gebruik voor collecties of streams

```java
Flux<BlogPost> posts = repository.findAll();
```

### Reactive Operators

```java
// Transformatie
Flux<String> titles = repository.findAll()
    .map(post -> post.getTitle());

// Filtering
Flux<BlogPost> longPosts = repository.findAll()
    .filter(post -> post.getContent().length() > 1000);

// Error handling
Mono<BlogPost> post = repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException()))
    .onErrorReturn(new BlogPost());

// Combining
Mono<String> combined = Mono.zip(
    repository.findById(id1),
    repository.findById(id2),
    (p1, p2) -> p1.getTitle() + " + " + p2.getTitle()
);
```

### MongoDB Configuratie
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/blog
spring.data.mongodb.database=blog
```

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
```

---

## Reeks 6: Low-Level JDBC

### Leerdoelen
- Directe JDBC connecties
- SQL queries zonder ORM
- Connection pooling
- Transaction management
- PreparedStatements en SQL injection preventie

### Kernconcepten

#### 1. JDBC Template
```java
@Repository
public class BlogPostDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<BlogPost> findAll() {
        String sql = "SELECT * FROM blog_posts";
        return jdbcTemplate.query(sql, new BlogPostRowMapper());
    }
    
    public Optional<BlogPost> findById(Long id) {
        String sql = "SELECT * FROM blog_posts WHERE id = ?";
        try {
            BlogPost post = jdbcTemplate.queryForObject(
                sql, 
                new BlogPostRowMapper(), 
                id
            );
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    public BlogPost save(BlogPost post) {
        if (post.getId() == null) {
            return insert(post);
        } else {
            return update(post);
        }
    }
    
    private BlogPost insert(BlogPost post) {
        String sql = "INSERT INTO blog_posts (title, content) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            return ps;
        }, keyHolder);
        
        post.setId(keyHolder.getKey().longValue());
        return post;
    }
    
    private BlogPost update(BlogPost post) {
        String sql = "UPDATE blog_posts SET title = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getId());
        return post;
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM blog_posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
```

#### 2. RowMapper
```java
public class BlogPostRowMapper implements RowMapper<BlogPost> {
    @Override
    public BlogPost mapRow(ResultSet rs, int rowNum) throws SQLException {
        BlogPost post = new BlogPost();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        return post;
    }
}
```

#### 3. Named Parameters
```java
@Repository
public class BlogPostDao {
    
    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;
    
    public List<BlogPost> findByTitle(String title) {
        String sql = "SELECT * FROM blog_posts WHERE title LIKE :title";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", "%" + title + "%");
        
        return namedTemplate.query(sql, params, new BlogPostRowMapper());
    }
}
```

#### 4. Transaction Management
```java
@Service
public class BlogPostService {
    
    @Autowired
    private BlogPostDao dao;
    
    @Transactional
    public void transferPost(Long fromId, Long toId) {
        BlogPost from = dao.findById(fromId).orElseThrow();
        BlogPost to = dao.findById(toId).orElseThrow();
        
        // Beide updates gebeuren in dezelfde transactie
        dao.update(from);
        dao.update(to);
        
        // Bij exception wordt alles ge-rollback
    }
}
```

### PostgreSQL Configuratie
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection pooling
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### SQL Injection Preventie

❌ **FOUT - SQL Injection risico:**
```java
String sql = "SELECT * FROM blog_posts WHERE title = '" + title + "'";
jdbcTemplate.query(sql, new BlogPostRowMapper());
```

✅ **CORRECT - PreparedStatement:**
```java
String sql = "SELECT * FROM blog_posts WHERE title = ?";
jdbcTemplate.query(sql, new BlogPostRowMapper(), title);
```

---

## Reeks 7: .NET/C# Implementatie

### Leerdoelen
- Equivalente concepten in .NET
- Entity Framework patterns
- C# 8.0 features

### Kernconcepten

#### 1. Domain Model
```csharp
public class Customer
{
    public int Id { get; set; }
    public string Name { get; set; }
    public string Email { get; set; }
    public List<Order> Orders { get; set; }
}

public class Order
{
    public int Id { get; set; }
    public DateTime OrderDate { get; set; }
    public decimal TotalAmount { get; set; }
    public int CustomerId { get; set; }
    public Customer Customer { get; set; }
}
```

#### 2. Data Storage Pattern
```csharp
public interface IDataStorage<T>
{
    List<T> GetAll();
    T GetById(int id);
    void Add(T entity);
    void Update(T entity);
    void Delete(int id);
}

public class CustomerStorage : IDataStorage<Customer>
{
    private List<Customer> customers = new List<Customer>();
    
    public List<Customer> GetAll() => customers;
    
    public Customer GetById(int id) => 
        customers.FirstOrDefault(c => c.Id == id);
    
    public void Add(Customer customer)
    {
        customer.Id = customers.Count + 1;
        customers.Add(customer);
    }
    
    // Andere methoden...
}
```

---

## Vergelijking: Spring vs .NET

| Concept | Spring Boot (Java) | .NET (C#) |
|---------|-------------------|-----------|
| Framework | Spring Boot | ASP.NET Core |
| ORM | JPA/Hibernate | Entity Framework |
| DI Container | `@Autowired`, `@Component` | Constructor Injection, Services |
| REST Controller | `@RestController` | `[ApiController]` |
| Database | `JpaRepository` | `DbContext` |
| Configuration | `application.properties` | `appsettings.json` |

---

## Belangrijke Patronen

### 1. DAO Pattern (Data Access Object)
- **Doel:** Abstractie van data toegang
- **Implementatie:** Interface + meerdere implementaties
- **Voordeel:** Eenvoudig wisselen tussen in-memory en database

### 2. Repository Pattern
- **Doel:** Collectie-achtige toegang tot entities
- **Implementatie:** Spring Data repositories
- **Voordeel:** CRUD operaties zonder boilerplate code

### 3. MVC Pattern
- **Model:** Domain entities (`BlogPost`, `Customer`)
- **View:** JSON responses (REST API)
- **Controller:** `@RestController` classes

### 4. Dependency Injection
- **Doel:** Loose coupling tussen componenten
- **Implementatie:** `@Autowired`, constructors
- **Voordeel:** Testbaarheid en flexibiliteit

### 5. Reactive Pattern
- **Doel:** Non-blocking, asynchrone verwerking
- **Implementatie:** `Mono<T>`, `Flux<T>`
- **Voordeel:** Betere schaalbaarheid en resource gebruik

---

## Best Practices

### 1. Error Handling
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

### 2. Validation
```java
public class BlogPost {
    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100)
    private String title;
    
    @NotBlank(message = "Content cannot be empty")
    private String content;
}

@PostMapping
public ResponseEntity<BlogPost> createPost(@Valid @RequestBody BlogPost post) {
    // Spring valideert automatisch
}
```

### 3. Configuration Management
```properties
# application.properties
spring.profiles.active=dev

# application-dev.properties (development)
spring.datasource.url=jdbc:h2:mem:testdb

# application-prod.properties (production)
spring.datasource.url=jdbc:postgresql://prod-server:5432/blog
```

### 4. Logging
```java
@RestController
public class BlogController {
    
    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    
    @GetMapping("/{id}")
    public BlogPost getPost(@PathVariable Long id) {
        logger.info("Fetching post with id: {}", id);
        return service.findById(id);
    }
}
```

---

## Veelvoorkomende Valkuilen

### 1. N+1 Query Problem (JPA)
❌ **Probleem:**
```java
List<BlogPost> posts = repository.findAll();
for (BlogPost post : posts) {
    post.getAuthor().getName(); // Extra query per post!
}
```

✅ **Oplossing:**
```java
@Query("SELECT p FROM BlogPost p JOIN FETCH p.author")
List<BlogPost> findAllWithAuthor();
```

### 2. Blocking in Reactive Code
❌ **Probleem:**
```java
@GetMapping
public Flux<BlogPost> getAllPosts() {
    List<BlogPost> posts = repository.findAll().block(); // BLOCKING!
    return Flux.fromIterable(posts);
}
```

✅ **Oplossing:**
```java
@GetMapping
public Flux<BlogPost> getAllPosts() {
    return repository.findAll(); // Non-blocking
}
```

### 3. SQL Injection
❌ **Probleem:**
```java
String sql = "SELECT * FROM users WHERE name = '" + userInput + "'";
```

✅ **Oplossing:**
```java
String sql = "SELECT * FROM users WHERE name = ?";
jdbcTemplate.query(sql, mapper, userInput);
```

---

## Quick Reference

### HTTP Status Codes
- `200 OK` - Succesvolle GET/PUT
- `201 Created` - Succesvolle POST
- `204 No Content` - Succesvolle DELETE
- `400 Bad Request` - Validatie fout
- `401 Unauthorized` - Niet geauthenticeerd
- `403 Forbidden` - Niet geautoriseerd
- `404 Not Found` - Resource niet gevonden
- `500 Internal Server Error` - Server fout

### Maven Commando's
```bash
# Applicatie starten
./mvnw spring-boot:run

# Tests uitvoeren
./mvnw test

# Build maken
./mvnw clean package

# Dependencies downloaden
./mvnw dependency:resolve
```

### Spring Profiles
```bash
# Profile activeren bij opstarten
java -jar app.jar --spring.profiles.active=prod

# Of via environment variable
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

---

## Samenvatting Leerstof

### Beginner (Reeks 1-2)
✅ Spring Boot basics  
✅ REST API ontwikkeling  
✅ CRUD operaties  
✅ JPA/Hibernate  
✅ Database integratie  

### Intermediate (Reeks 3)
✅ Spring Security  
✅ Authentication & Authorization  
✅ Role-based access control  
✅ Method-level security  

### Advanced (Reeks 5-6)
✅ Reactive programming  
✅ WebFlux & Project Reactor  
✅ MongoDB  
✅ Low-level JDBC  
✅ Transaction management  

### Cross-Platform (Reeks 7)
✅ .NET/C# equivalenten  
✅ Entity Framework patterns  

---

## Bronnen

- **Cursus PDF:** `Cursus_frameworks_voor_serverapplicaties.pdf`
- **Labs:** Reeks 1, 2, 3, 5, 6, 7
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Spring Data:** https://spring.io/projects/spring-data
- **Project Reactor:** https://projectreactor.io/

---

*Laatst bijgewerkt: Januari 2026*
