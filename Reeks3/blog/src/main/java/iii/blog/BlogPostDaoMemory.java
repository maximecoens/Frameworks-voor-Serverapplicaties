package iii.blog;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Profile("test")
@Service
public class BlogPostDaoMemory implements BlogPostDao {
    private final Map<Long, BlogPost> blogPosts = new HashMap<>();

    private static Long counter = 2L;

    public BlogPostDaoMemory() {
        BlogPost helloWorldPost = new BlogPost(1L, "Hello World", "Hello World!");
        // Add dummy blog post to the collection
        this.blogPosts.put(helloWorldPost.getId(), helloWorldPost);
    }

    /**
     * Deel 1
     */
    public List<BlogPost> getAllPosts() {
        return new ArrayList<>(blogPosts.values());
    }

    /**
     * Deel 2
     */
    public void addPost(final BlogPost blogPost) {
        blogPost.setId(counter);
        counter++;
        blogPosts.putIfAbsent(blogPost.getId(), blogPost);
    }

    public void updatePost(final long id, final BlogPost blogPost) {
        blogPosts.put(id, blogPost);
    }


    public Optional<BlogPost> getPost(final long id) {
        return Optional.ofNullable(blogPosts.get(id));
    }

    public void deletePost(final long id) {
        blogPosts.remove(id);
    }

    public boolean exists(long id){
        return blogPosts.containsKey(id);
    }

    @Override
    public List<BlogPost> searchPostsByTitleContaining(String keyword) {
        return blogPosts.values().stream().filter(post -> post.getTitle().contains(keyword)).collect(Collectors.toList());
    }
}
