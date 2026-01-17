# Frameworks voor Serverapplicaties

## 📚 Documentatie

- **[CURSUS_REFERENTIE.md](CURSUS_REFERENTIE.md)** - Uitgebreide cursus referentie met alle concepten, patronen en voorbeelden per reeks
- **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Snelle referentie voor veelgebruikte taken en code snippets

## 🎯 Cursus Overzicht

Deze repository bevat oplossingen voor het vak "Frameworks voor Serverapplicaties". De cursus bouwt stapsgewijs op van basis REST APIs naar geavanceerde reactive programming en database toegang met Spring Boot.

### Reeksen (Labs)

| Reeks | Onderwerp | Technologie |
|-------|-----------|-------------|
| **Reeks 1** | Basis REST API | Spring Boot, REST Controller, In-memory data |
| **Reeks 2** | Database Integratie | Spring Data JPA, H2, Profiles |
| **Reeks 3** | Security & Authentication | Spring Security, Role-based access |
| **Reeks 5** | Reactive Programming | Spring WebFlux, MongoDB, Reactor (Mono/Flux) |
| **Reeks 6** | Low-Level JDBC | Spring JDBC, PostgreSQL |
| **Reeks 7** | .NET Implementatie | C#, Entity Framework patterns |

## 🚀 Quick Start

### Vereisten
- Java 21+
- Maven
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Applicatie Starten
```bash
cd ReeksX/blog  # of andere project directory
./mvnw spring-boot:run
```

### Tests Uitvoeren
```bash
./mvnw test
```

## 📖 Leerstof per Reeks

### Reeks 1: REST API Fundamentals
- Spring Boot applicatie opzetten
- REST endpoints met `@RestController`
- CRUD operaties
- Exception handling

### Reeks 2: Data Persistence
- JPA entities definiëren
- Spring Data repositories
- Database profiles (test/productie)
- DAO pattern met meerdere implementaties

### Reeks 3: Security
- Spring Security configuratie
- Authenticatie & Autorisatie
- Role-based access control
- Method-level security

### Reeks 5: Reactive Programming
- Non-blocking I/O
- Reactor Mono en Flux
- MongoDB reactive driver
- Server-Sent Events (SSE)
- Change streams

### Reeks 6: JDBC
- Directe database connecties
- JdbcTemplate
- PreparedStatements
- Transaction management
- SQL injection preventie

### Reeks 7: .NET/C#
- Equivalente patronen in .NET
- Entity Framework
- C# implementatie

## 🔗 Nuttige Links

- [Spring Boot Documentatie](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Project Reactor](https://projectreactor.io/)
- [Spring Security](https://spring.io/projects/spring-security)

## 📝 Notities

De oefeningen en oplossingen maken uitsluitend gebruik van concepten, technieken en patronen die in de cursus worden behandeld. Er worden geen externe frameworks of libraries gebruikt buiten de standaard Spring Boot stack.