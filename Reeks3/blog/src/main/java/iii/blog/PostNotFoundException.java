package iii.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * One could annotate this class with @ResponseStatus(HttpStatus.NOT_FOUND) instead of adding the explicit exception
 * handler in the RestController.
 */
//@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException  {
    public PostNotFoundException(Long id) {
        super("Could not find post with id=" + id);
    }

}
