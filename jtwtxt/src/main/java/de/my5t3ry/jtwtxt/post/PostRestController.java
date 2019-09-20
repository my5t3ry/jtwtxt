package de.my5t3ry.jtwtxt.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * User: my5t3ry
 * Date: 20.09.19 06:34
 */
@RestController
@RequestMapping("/api/post")
public class PostRestController {
    @GetMapping
    public ResponseEntity<List<Post>> get() {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }
}
