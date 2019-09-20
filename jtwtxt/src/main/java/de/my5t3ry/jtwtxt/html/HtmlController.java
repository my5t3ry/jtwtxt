package de.my5t3ry.jtwtxt.html;

import de.my5t3ry.jtwtxt.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * User: my5t3ry
 * Date: 20.09.19 07:38
 */
@RestController
@Slf4j
public class HtmlController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private PostRepository postRepository;

    @ResponseBody
    @GetMapping("/search")
    public String get(@RequestParam("q") String query) {
        QueryBuilder queryBuilder = multiMatchQuery(
                query,
                "copy"
        );
        if (StringUtils.isEmpty(query)) {
            return get();
        }
        return templateService.fetchHtml(postRepository.search(queryBuilder), query);
    }

    @ResponseBody
    @RequestMapping("/")
    public String get() {
        return templateService.fetchHtml(postRepository.findAll(), "full");
    }


}
