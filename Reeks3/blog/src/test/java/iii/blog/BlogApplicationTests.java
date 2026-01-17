package iii.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class BlogApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Value("${users.admin.username}")
    String adminUsername;

    @Value("${users.admin.password}")
    String adminPassword;

    @Test
    public void testGetPosts() {
        EntityExchangeResult<List<BlogPost>> postsResponse = this.webClient.get()
                .uri("/posts")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(BlogPost.class).hasSize(1)
                .returnResult();
        BlogPost post = postsResponse.getResponseBody().get(0);
        check(post, 1L, "Hello World", "Hello World!");
    }

    @Test
    public void testGetPostXML() {
        FluxExchangeResult<BlogPost> postFlux = this.webClient.get()
                .uri("/posts/{id}", 1L)
                .header(ACCEPT, APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(APPLICATION_XML_VALUE)
                .returnResult(BlogPost.class);
        postFlux.getResponseBody().subscribe((p) -> check(p, 1L, "Hello World", "Hello World!"));

//                jackson-dataformat-xml does not provide automatic xml unmarshalling (yet) for web(flux)client
//               .expectBody(BlogPost.class)
    }

    private void check(BlogPost post, long id, String title, String content) {
        assertThat(post).hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("title", title)
                .hasFieldOrPropertyWithValue("content", content);

    }

    @Test
    public void testCreatePost() {
        String title = "Title";
        String content = "Content";
        this.webClient.post()
                .uri("/posts")
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .contentType(APPLICATION_JSON)
                .bodyValue(new BlogPost(title,content ))
                //.body(BodyInserters.fromValue(new BlogPost(title, content)))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody().isEmpty();
        BlogPost post = this.webClient.get()
                .uri("/posts/{id}", 2L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(BlogPost.class)
                .returnResult()
                .getResponseBody();
        check(post,2L,title,content);
    }

    @Test
    public void testGetUnExistingPost() {
        this.webClient.get()
                .uri("/posts/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testUpdatePost() {
        String title = "new title";
        String content = "new content";
        BlogPost updatedPost = new BlogPost(1L, title,content );
        this.webClient.put()
                .uri("/posts/{id}", updatedPost.getId())
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .contentType(APPLICATION_JSON)
                .bodyValue(updatedPost)
//                .body(BodyInserters.fromValue(updatedPost))
                .exchange()
                .expectStatus().isNoContent();
        BlogPost post = this.webClient.get()
                .uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(BlogPost.class)
                .returnResult()
                .getResponseBody();
        check(post,1L,title,content);
    }

    @Test
    public void testUpdateNonExistingPost() {
        String title = "new title";
        String content = "new content";
        BlogPost updatedPost = new BlogPost(98L, title,content );
        this.webClient.put()
                .uri("/posts/{id}", updatedPost.getId())
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .contentType(APPLICATION_JSON)
                .bodyValue(updatedPost)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testUpdatePostWrongID() {
        String title = "new title";
        String content = "new content";
        BlogPost updatedPost = new BlogPost(2L, title,content );
        this.webClient.put()
                .uri("/posts/{id}", 1L)
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .contentType(APPLICATION_JSON)
                .bodyValue(updatedPost)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void testDeletePost() {
        this.webClient.delete()
                .uri("/posts/{id}", 1L)
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .exchange()
                .expectStatus().isNoContent();

        this.webClient.get()
                .uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testDeleteNonExistingPost() {
        this.webClient.delete()
                .uri("/posts/{id}", 99L)
                .headers(header -> header.setBasicAuth(adminUsername, adminPassword))
                .exchange()
                .expectStatus().isNotFound();

    }

}
