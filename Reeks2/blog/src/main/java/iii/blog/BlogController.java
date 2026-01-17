package iii.blog;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class BlogController {
    private final Logger logger = LoggerFactory.getLogger(BlogController.class);

    private final BlogPostDao postsDao;
    private final Counter postsCreateCounter;
    private final Counter postsDeleteCounter;
    private final Counter postsReadCounter;
    private final Counter postsUpdateCounter;

    public BlogController(BlogPostDao postsDao, MeterRegistry meterRegistry) {
        this.postsDao = postsDao;
        postsCreateCounter = Counter
                .builder("blogpost_count")
                .description("telt create request")
                .tag("operation", "created")
                .register(meterRegistry);
        postsDeleteCounter = Counter
                .builder("blogpost_count")
                .description("telt delete request")
                .tag("operation", "deleted")
                .register(meterRegistry);
        postsReadCounter = Counter
                .builder("blogpost_count")
                .description("telt read request")
                .tag("operation", "read")
                .register(meterRegistry);
        postsUpdateCounter = Counter
                .builder("blogpost_count")
                .description("telt update request")
                .tag("operation", "update")
                .register(meterRegistry);
    }

    /**
     * Provide a list of all blogPosts.
     */
    @GetMapping
    public List<BlogPost> getPosts() {
        postsReadCounter.increment();
        return postsDao.getAllPosts();
    }


    /**
     * Provide the details of a blogPost with the given id. Throw PostNotFoundException if id doesn't exist.
     */
    @GetMapping("{id}")
    public BlogPost getPost(@PathVariable("id") long id) {
        postsReadCounter.increment();
        //return postsDao.getPost(id).orElseThrow(() -> new PostNotFoundException(id));
        return postsDao.getPost(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found"));
    }

    /**
     * Creates a new BlogPost, setting its URL as the Location header on the
     * response.
     */
    @PostMapping
    public ResponseEntity<Void> addPost(@RequestBody BlogPost post) {
        postsDao.addPost(post);
        postsCreateCounter.increment();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Removes the blogPost with the given id.
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("id") long id) {
        if (postsDao.exists(id)) {
            postsDeleteCounter.increment();
            postsDao.deletePost(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found");
        }
    }

    /**
     * Update the blogPost with the given id.
     */
    /*@PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@RequestBody BlogPost post, @PathVariable("id") long id) {
        if (postsDao.exists(id)) {
            if (id == post.getId()) {
                postsDao.updatePost(id, post);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Id path and post inconsistent");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post doesn't exist");
        }
    }*/

    /**
     * Alternative with EntityResponse
     */
    @PutMapping("{id}")
    public ResponseEntity<String> updatePost(@RequestBody BlogPost post, @PathVariable("id") long id) {
        if (postsDao.exists(id)) {
            if (id == post.getId()) {
                postsDao.updatePost(id, post);
                postsUpdateCounter.increment();
                return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Id path and post inconsistent", HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>("Post doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Explicit exception handler to map PostNotFoundException to a 404 Not Found HTTP status code.
     * Alternative to annotating the exception class PostNotFoundException
     */
    /*@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    public void handleNotFound(Exception ex) {
        logger.warn("Exception is: " + ex.getMessage());
        // return empty 404
    }*/
}
