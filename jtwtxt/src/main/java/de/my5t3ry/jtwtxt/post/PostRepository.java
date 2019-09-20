package de.my5t3ry.jtwtxt.post;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * User: my5t3ry
 * Date: 20.09.19 05:00
 */
public interface PostRepository extends ElasticsearchRepository<Post, String> {
}
