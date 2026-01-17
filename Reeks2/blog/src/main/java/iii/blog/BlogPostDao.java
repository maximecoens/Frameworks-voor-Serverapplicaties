package iii.blog;

import java.util.List;
import java.util.Optional;

public interface BlogPostDao {
    List<BlogPost> getAllPosts();

    Optional<BlogPost> getPost(long id);

    void addPost(BlogPost post);

    boolean exists(long id);

    void deletePost(long id);

    void updatePost(long id, BlogPost post);
}
