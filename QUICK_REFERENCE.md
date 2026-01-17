# Quick Reference - Frameworks voor Serverapplicaties

## Snelle Index

- [Spring Boot Setup](#spring-boot-setup)
- [REST Endpoints](#rest-endpoints)
- [Database Configuratie](#database-configuratie)
- [Security Setup](#security-setup)
- [Reactive Programming](#reactive-programming)
- [JDBC Operaties](#jdbc-operaties)

---

## Spring Boot Setup

### Nieuwe Applicatie
```bash
# Via Spring Initializr (https://start.spring.io/)
# Of via IDE (IntelliJ IDEA, Eclipse)

# Dependencies selecteren:
- Spring Web
- Spring Data JPA
- H2 Database (voor development)
- Spring Security (indien nodig)
```

### Project Structuur
```
src/
├── main/
│   ├── java/
│   │   └── iii/blog/
│   │       ├── BlogApplication.java       # Main class
│   │       ├── BlogController.java        # REST endpoints
│   │       ├── BlogPost.java              # Domain model
│   │       ├── BlogPostDao.java           # Data access
│   │       └── BlogPostRepository.java    # JPA repository
│   └── resources/
│       ├── application.properties         # Configuratie
│       └── static/                        # Frontend files
└── test/
    └── java/
        └── iii/blog/
            └── BlogApplicationTests.java  # Tests
```

---

## REST Endpoints

### Basis Template
```java
@RestController
@RequestMapping("/api/posts")
public class BlogController {
    
    @Autowired
    private BlogPostService service;
    
    // GET alle posts
    @GetMapping
    public List<BlogPost> getAll() {
        return service.findAll();
    }
    
    // GET één post
    @GetMapping("/{id}")
    public BlogPost getOne(@PathVariable Long id) {
        return service.findById(id)
            .orElseThrow(() -> new PostNotFoundException(id));
    }
    
    // POST nieuwe post
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPost create(@RequestBody BlogPost post) {
        return service.save(post);
    }
    
    // PUT update post
    @PutMapping("/{id}")
    public BlogPost update(@PathVariable Long id, @RequestBody BlogPost post) {
        post.setId(id);
        return service.save(post);
    }
    
    // DELETE post
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
```

### Request Parameters
```java
// Query parameters: /posts?page=1&size=10
@GetMapping
public List<BlogPost> getAll(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size) {
    // ...
}

// Path variables: /posts/123
@GetMapping("/{id}")
public BlogPost getOne(@PathVariable Long id) {
    // ...
}

// Request body: JSON in POST/PUT
@PostMapping
public BlogPost create(@RequestBody BlogPost post) {
    // ...
}
```

---

## Database Configuratie

### H2 (In-Memory)
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### PostgreSQL
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### MongoDB (Reactive)
```properties
# application.properties
spring.data.mongodb.uri=mongodb://localhost:27017/blog
spring.data.mongodb.database=blog
```

### JPA Entity
```java
@Entity
@Table(name = "blog_posts")
public class BlogPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(length = 5000)
    private String content;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getters en setters
}
```

### Repository
```java
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    // Automatische queries
    List<BlogPost> findByTitle(String title);
    List<BlogPost> findByTitleContaining(String keyword);
    List<BlogPost> findByCreatedAtAfter(LocalDateTime date);
    
    // Custom query
    @Query("SELECT p FROM BlogPost p WHERE p.title LIKE %:keyword%")
    List<BlogPost> searchByTitle(@Param("keyword") String keyword);
}
```

---

## Security Setup

### Basis Security
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### In-Memory Users
```java
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
```

### Method Security
```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}

// In controller of service
@Secured("ROLE_ADMIN")
public void deletePost(Long id) { ... }

@PreAuthorize("hasRole('ADMIN')")
public void updatePost(Long id, BlogPost post) { ... }
```

---

## Reactive Programming

### Controller
```java
@RestController
@RequestMapping("/api/posts")
public class BlogController {
    
    @Autowired
    private BlogPostRepository repository;
    
    // Flux = 0..N elementen
    @GetMapping
    public Flux<BlogPost> getAll() {
        return repository.findAll();
    }
    
    // Mono = 0..1 element
    @GetMapping("/{id}")
    public Mono<BlogPost> getOne(@PathVariable String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new PostNotFoundException(id)));
    }
    
    @PostMapping
    public Mono<BlogPost> create(@RequestBody BlogPost post) {
        return repository.save(post);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
```

### Repository
```java
public interface BlogPostRepository extends ReactiveMongoRepository<BlogPost, String> {
    Flux<BlogPost> findByTitleContaining(String keyword);
}
```

### Entity
```java
@Document(collection = "posts")
public class BlogPost {
    @Id
    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    
    // Getters en setters
}
```

### Operators
```java
// Map - transformeer elk element
Flux<String> titles = repository.findAll()
    .map(BlogPost::getTitle);

// Filter - filter elementen
Flux<BlogPost> filtered = repository.findAll()
    .filter(post -> post.getTitle().length() > 10);

// FlatMap - één-naar-veel transformatie
Flux<String> words = repository.findAll()
    .flatMap(post -> Flux.fromArray(post.getContent().split(" ")));

// Zip - combineer meerdere publishers
Mono<String> combined = Mono.zip(
    repository.findById(id1),
    repository.findById(id2),
    (p1, p2) -> p1.getTitle() + " + " + p2.getTitle()
);

// Error handling
Mono<BlogPost> post = repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException()))
    .onErrorReturn(new BlogPost());
```

### Server-Sent Events
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<BlogPost> streamPosts() {
    return repository.findAll()
        .delayElements(Duration.ofSeconds(1));
}
```

---

## JDBC Operaties

### JdbcTemplate Setup
```java
@Repository
public class BlogPostDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // SELECT all
    public List<BlogPost> findAll() {
        String sql = "SELECT * FROM blog_posts";
        return jdbcTemplate.query(sql, new BlogPostRowMapper());
    }
    
    // SELECT by id
    public Optional<BlogPost> findById(Long id) {
        String sql = "SELECT * FROM blog_posts WHERE id = ?";
        try {
            BlogPost post = jdbcTemplate.queryForObject(sql, new BlogPostRowMapper(), id);
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    // INSERT
    public BlogPost insert(BlogPost post) {
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
    
    // UPDATE
    public void update(BlogPost post) {
        String sql = "UPDATE blog_posts SET title = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getId());
    }
    
    // DELETE
    public void deleteById(Long id) {
        String sql = "DELETE FROM blog_posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
```

### RowMapper
```java
public class BlogPostRowMapper implements RowMapper<BlogPost> {
    @Override
    public BlogPost mapRow(ResultSet rs, int rowNum) throws SQLException {
        BlogPost post = new BlogPost();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return post;
    }
}
```

### Named Parameters
```java
@Autowired
private NamedParameterJdbcTemplate namedTemplate;

public List<BlogPost> findByTitle(String title) {
    String sql = "SELECT * FROM blog_posts WHERE title LIKE :title";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("title", "%" + title + "%");
    
    return namedTemplate.query(sql, params, new BlogPostRowMapper());
}
```

### Transactions
```java
@Service
public class BlogPostService {
    
    @Transactional
    public void updateMultiplePosts(List<BlogPost> posts) {
        for (BlogPost post : posts) {
            dao.update(post);
        }
        // Alle updates in één transactie
        // Bij exception: automatische rollback
    }
}
```

---

## Exception Handling

### Custom Exception
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("Post not found with id: " + id);
    }
}
```

### Global Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PostNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An error occurred",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## Testing

### Controller Test
```java
@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void testCreatePost() throws Exception {
        String json = "{\"title\":\"Test\",\"content\":\"Content\"}";
        
        mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated());
    }
}
```

### Repository Test
```java
@DataJpaTest
public class BlogPostRepositoryTest {
    
    @Autowired
    private BlogPostRepository repository;
    
    @Test
    public void testFindById() {
        BlogPost post = new BlogPost();
        post.setTitle("Test");
        post.setContent("Content");
        
        BlogPost saved = repository.save(post);
        Optional<BlogPost> found = repository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getTitle());
    }
}
```

---

## Profiles

### Configuratie per Environment
```properties
# application.properties (default)
spring.profiles.active=dev

# application-dev.properties
spring.datasource.url=jdbc:h2:mem:testdb
logging.level.root=DEBUG

# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb

# application-prod.properties
spring.datasource.url=jdbc:postgresql://prod-server:5432/blog
logging.level.root=WARN
```

### Component per Profile
```java
@Component
@Profile("dev")
public class DevDataInitializer {
    // Alleen actief in dev profile
}

@Component
@Profile("prod")
public class ProdDataInitializer {
    // Alleen actief in prod profile
}
```

---

## Maven Commando's

```bash
# Applicatie starten
./mvnw spring-boot:run

# Met specifiek profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Tests uitvoeren
./mvnw test

# Enkele test class
./mvnw test -Dtest=BlogControllerTest

# Build (zonder tests)
./mvnw clean package -DskipTests

# Build (met tests)
./mvnw clean package

# Dependencies weergeven
./mvnw dependency:tree

# Dependencies updaten
./mvnw dependency:resolve
```

---

## HTTP Requests (curl)

```bash
# GET all
curl http://localhost:8080/api/posts

# GET one
curl http://localhost:8080/api/posts/1

# POST create
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"New Post","content":"Content here"}'

# PUT update
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","content":"New content"}'

# DELETE
curl -X DELETE http://localhost:8080/api/posts/1

# With authentication
curl -u admin:admin http://localhost:8080/api/posts
```

---

## Dependencies (pom.xml)

### Web
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### JPA
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### H2 Database
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### PostgreSQL
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### WebFlux (Reactive)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### MongoDB Reactive
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

### JDBC
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

### Validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## Troubleshooting

### Port al in gebruik
```properties
# application.properties
server.port=8081
```

### Database connectie problemen
```properties
# Toon SQL queries
spring.jpa.show-sql=true

# Toon details
logging.level.org.springframework.jdbc=DEBUG
```

### CORS problemen
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
```

---

*Voor uitgebreide uitleg, zie: CURSUS_REFERENTIE.md*
