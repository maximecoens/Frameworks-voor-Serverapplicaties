package be.ugent.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test klasse voor de Spring Boot applicatie.
 * Controleert of de applicatie context correct geladen wordt.
 */
@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private BlogController blogController;

    @Autowired
    private BlogPostDaoMemory blogPostDao;

    /**
     * Test of de applicatie context succesvol geladen wordt.
     * Dit test of alle beans correct geregistreerd zijn.
     */
    @Test
    void contextLoads() {
        assertNotNull(blogController, "BlogController moet geïnjecteerd zijn");
        assertNotNull(blogPostDao, "BlogPostDaoMemory moet geïnjecteerd zijn");
    }

    /**
     * Test of de initiële "Hello World" post aanwezig is.
     */
    @Test
    void testInitialPost() {
        var posts = blogPostDao.getAllPosts();
        assertEquals(1, posts.size(), "Er moet 1 initiële post zijn");
        assertEquals("Hello World", posts.get(0).getTitel(), "De eerste post moet 'Hello World' zijn");
    }

    /**
     * Test CRUD operaties op de DAO.
     */
    @Test
    void testCrudOperations() {
        // Test CREATE
        BlogPost newPost = new BlogPost(null, "Test Post", "Test Content");
        BlogPost created = blogPostDao.addPost(newPost);
        assertNotNull(created.getId(), "Id moet automatisch gegenereerd worden");

        // Test READ
        var found = blogPostDao.getPostById(created.getId());
        assertTrue(found.isPresent(), "Post moet gevonden worden");
        assertEquals("Test Post", found.get().getTitel());

        // Test UPDATE
        BlogPost updated = new BlogPost(created.getId(), "Updated Title", "Updated Content");
        boolean updateResult = blogPostDao.updatePost(created.getId(), updated);
        assertTrue(updateResult, "Update moet succesvol zijn");

        var updatedPost = blogPostDao.getPostById(created.getId());
        assertEquals("Updated Title", updatedPost.get().getTitel());

        // Test DELETE
        boolean deleteResult = blogPostDao.deletePost(created.getId());
        assertTrue(deleteResult, "Delete moet succesvol zijn");

        var deletedPost = blogPostDao.getPostById(created.getId());
        assertFalse(deletedPost.isPresent(), "Post moet verwijderd zijn");
    }
}
