package de.my5t3ry.jtwtxt.html;

import de.my5t3ry.jtwtxt.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * User: my5t3ry
 * Date: 20.09.19 07:38
 */
@RestController
@Slf4j
public class HtmlController {

    @Autowired
    private TemplateService templateService;

    @Value("${config.html.cache-file}")
    private String htmlCacheFilePath;
    @Autowired
    private PostRepository postRepository;

    @ResponseBody
    @RequestMapping("/")
    public String get() {
        final File cacheFile = new File(htmlCacheFilePath);
        if (cacheFile.exists()) {
            try {
                return Files.readString(cacheFile.toPath());
            } catch (IOException e) {
                throw new IllegalStateException("Could not read cache file ['" + cacheFile.getAbsolutePath() + "']", e);
            }
        } else {
            try {
                final Map<String, String> attributes = Map.of("posts", getPosts());
                final String htmlString = templateService.getTemplate("index.html", attributes);
                Files.writeString(cacheFile.toPath(), htmlString, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING});
                return htmlString;
            } catch (IOException e) {
                throw new IllegalStateException("Could not rebuild cache file", e);
            }
        }
    }

    private String getPosts() {
        final StringBuilder stringBuilder = new StringBuilder();
        postRepository.findAll().forEach(curPost -> {
            stringBuilder.append(templateService.getPost(curPost));
        });
        return stringBuilder.toString();
    }


}
