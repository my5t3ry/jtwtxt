package de.my5t3ry.jtwtxt.html;

import de.my5t3ry.jtwtxt.post.Post;
import de.my5t3ry.jtwtxt.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * User: my5t3ry
 * Date: 20.09.19 07:38
 */
@RestController
public class HtmlController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private PostRepository postRepository;

    @ResponseBody
    @RequestMapping("/")

    public String welcome() {
        final Map<String, String> attributes = Map.of("posts", getPosts());
        return templateService.getTemplate("index.html", attributes);
    }

    private String getPosts() {
        final StringBuilder stringBuilder = new StringBuilder();
        postRepository.findAll().forEach(curPost -> {
            final Map<String, String> attributes = Map.of("copy", curPost.getCopy(),
                    "created", curPost.getFormatedCreatedOn(),
                    "content", getContent(curPost));
            stringBuilder.append(templateService.getTemplate("post.html",attributes));
        });
        return stringBuilder.toString();
    }

    private String getContent(final Post curPost) {
        final StringBuilder stringBuilder = new StringBuilder();
        curPost.getContent().forEach(curContent -> {
            final Map<String, String> attributes = Map.of("url", curContent.getUrl(),
                    "description", curContent.getDescription());
            stringBuilder.append(templateService.getTemplate("content.html", attributes));
        });
        return stringBuilder.toString();
    }

}
