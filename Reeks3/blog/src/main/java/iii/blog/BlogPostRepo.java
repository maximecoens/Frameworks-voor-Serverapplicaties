package iii.blog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostRepo extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByTitleContaining(String keyword);

}
