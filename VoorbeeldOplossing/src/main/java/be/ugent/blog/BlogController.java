package be.ugent.blog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller die HTTP requests afhandelt voor blogpost operaties.
 * Definieert endpoints voor alle CRUD operaties (Create, Read, Update, Delete).
 */
@RestController
@RequestMapping("/posts")
public class BlogController {
    
    private final BlogPostDaoMemory postDao;
    
    /**
     * Constructor met dependency injection van de BlogPostDaoMemory.
     * Spring injecteert automatisch de @Service bean.
     */
    public BlogController(BlogPostDaoMemory postDao) {
        this.postDao = postDao;
    }
    
    /**
     * GET /posts
     * Haalt alle blogposts op.
     * 
     * @return lijst van alle blogposts
     */
    @GetMapping
    public List<BlogPost> getAllPosts() {
        return postDao.getAllPosts();
    }
    
    /**
     * GET /posts/{id}
     * Haalt een specifieke blogpost op via id.
     * 
     * @param id het id van de gewenste post
     * @return de blogpost met het gegeven id
     * @throws PostNotFoundException indien de post niet bestaat
     */
    @GetMapping("/{id}")
    public BlogPost getPost(@PathVariable Long id) {
        return postDao.getPostById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
    
    /**
     * POST /posts
     * Creëert een nieuwe blogpost.
     * Retourneert status 201 Created met Location header die naar de nieuwe post verwijst.
     * 
     * @param post de te creëren blogpost (uit request body)
     * @return ResponseEntity met status 201 en Location header
     */
    @PostMapping
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost post) {
        BlogPost createdPost = postDao.addPost(post);
        
        // Bouw de URI voor de nieuwe resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(createdPost);
    }
    
    /**
     * PUT /posts/{id}
     * Update een bestaande blogpost.
     * Retourneert status 200 OK met de geüpdatete post.
     * 
     * @param id het id van de te updaten post
     * @param post de nieuwe gegevens
     * @return de geüpdatete blogpost
     * @throws PostNotFoundException indien de post niet bestaat
     * @throws IdMismatchException indien het id in de URL niet overeenkomt met het id in de body
     */
    @PutMapping("/{id}")
    public BlogPost updatePost(@PathVariable Long id, @RequestBody BlogPost post) {
        // Valideer dat id in URL overeenkomt met id in body (indien aanwezig)
        if (post.getId() != null && !post.getId().equals(id)) {
            throw new IdMismatchException(id, post.getId());
        }
        
        // Controleer of post bestaat
        if (!postDao.exists(id)) {
            throw new PostNotFoundException(id);
        }
        
        // Update de post
        postDao.updatePost(id, post);
        return post;
    }
    
    /**
     * DELETE /posts/{id}
     * Verwijdert een blogpost.
     * Retourneert status 204 No Content bij succes.
     * 
     * @param id het id van de te verwijderen post
     * @throws PostNotFoundException indien de post niet bestaat
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        if (!postDao.deletePost(id)) {
            throw new PostNotFoundException(id);
        }
    }
    
    /**
     * Exception die gegooid wordt wanneer een post niet gevonden wordt.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(Long id) {
            super("Post niet gevonden met id: " + id);
        }
    }
    
    /**
     * Exception die gegooid wordt bij id mismatch tussen URL en body.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class IdMismatchException extends RuntimeException {
        public IdMismatchException(Long urlId, Long bodyId) {
            super("Id in URL (" + urlId + ") komt niet overeen met id in body (" + bodyId + ")");
        }
    }
}
