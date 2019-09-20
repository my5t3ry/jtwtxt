package de.my5t3ry.jtwtxt.html;

import de.my5t3ry.jtwtxt.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
    public String get(@RequestParam("q") String query, @RequestParam(value = "index", defaultValue = "0", required = false) int i) {
        QueryBuilder queryBuilder = multiMatchQuery(
                query,
                "copy"
        );
        if (StringUtils.isEmpty(query)) {
            return get(i);
        }
        return templateService.fetchHtml(postRepository.search(new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(i, 30))
                .withSort(SortBuilders.fieldSort("createdOn")
                        .order(SortOrder.DESC)).build()), query, i);
    }

    @ResponseBody
    @RequestMapping("/")
    public String get(@RequestParam(value = "index", defaultValue = "0", required = false) int i) {
        return templateService.fetchHtml(postRepository.search(new NativeSearchQueryBuilder()
                .withSort(SortBuilders.fieldSort("createdOn").order(SortOrder.DESC))
                .withPageable(PageRequest.of(i, 30))
                .build()), "", i);
    }


}
