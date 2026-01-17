package be.ugent.blog;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Data Access Object voor BlogPost entiteiten.
 * Simuleert een database door blogposts in het geheugen op te slaan.
 * Gemarkeerd met @Service zodat Spring deze klasse automatisch als bean registreert.
 */
@Service
public class BlogPostDaoMemory {
    
    private final List<BlogPost> posts = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Constructor die de DAO initialiseert met een "Hello World" blogpost.
     */
    public BlogPostDaoMemory() {
        // Voeg een initiële "Hello World" post toe
        BlogPost helloPost = new BlogPost(
            idGenerator.getAndIncrement(),
            "Hello World",
            "Dit is mijn eerste blogpost!"
        );
        posts.add(helloPost);
    }
    
    /**
     * Haalt alle blogposts op.
     * 
     * @return lijst van alle blogposts
     */
    public List<BlogPost> getAllPosts() {
        return new ArrayList<>(posts);
    }
    
    /**
     * Haalt een specifieke blogpost op via id.
     * 
     * @param id het id van de gezochte post
     * @return Optional met de post indien gevonden, anders leeg
     */
    public Optional<BlogPost> getPostById(Long id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }
    
    /**
     * Voegt een nieuwe blogpost toe.
     * Genereert automatisch een uniek id.
     * 
     * @param post de toe te voegen blogpost
     * @return de toegevoegde post met gegenereerd id
     */
    public BlogPost addPost(BlogPost post) {
        post.setId(idGenerator.getAndIncrement());
        posts.add(post);
        return post;
    }
    
    /**
     * Update een bestaande blogpost.
     * 
     * @param id het id van de te updaten post
     * @param updatedPost de nieuwe gegevens
     * @return true indien succesvol, false indien post niet gevonden
     */
    public boolean updatePost(Long id, BlogPost updatedPost) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId().equals(id)) {
                updatedPost.setId(id);
                posts.set(i, updatedPost);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verwijdert een blogpost op basis van id.
     * 
     * @param id het id van de te verwijderen post
     * @return true indien succesvol verwijderd, false indien niet gevonden
     */
    public boolean deletePost(Long id) {
        return posts.removeIf(post -> post.getId().equals(id));
    }
    
    /**
     * Controleert of een post met het gegeven id bestaat.
     * 
     * @param id het te controleren id
     * @return true indien post bestaat, anders false
     */
    public boolean exists(Long id) {
        return posts.stream().anyMatch(post -> post.getId().equals(id));
    }
}
