package iii.blog;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Profile("!test")
@Service
public class BlogPostDaoDb implements BlogPostDao {
    BlogPostRepo blogPostRepo;
    public BlogPostDaoDb(BlogPostRepo blogPostRepo) {
        this.blogPostRepo = blogPostRepo;
    }
    @Override
    public List<BlogPost> getAllPosts() {
        return blogPostRepo.findAll();
    }

    @Override
    public Optional<BlogPost> getPost(long id) {
        return blogPostRepo.findById(id);
    }

    @Override
    public void addPost(BlogPost post) {
        blogPostRepo.save(post);
    }

    @Override
    public boolean exists(long id) {
        return blogPostRepo.existsById(id);
    }

    @Override
    public void deletePost(long id) {
        blogPostRepo.deleteById(id);
    }

    @Override
    public void updatePost(long id, BlogPost post) {
        blogPostRepo.save(post);
    }

    @Override
    public List<BlogPost> searchPostsByTitleContaining(String keyword) {
        return blogPostRepo.findByTitleContaining(keyword);
    }
}
