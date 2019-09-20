package de.my5t3ry.jtwtxt.file;

import de.my5t3ry.jtwtxt.post.Post;
import de.my5t3ry.jtwtxt.post.PostFactory;
import de.my5t3ry.jtwtxt.post.PostRepository;
import de.my5t3ry.jtwtxt.utils.WebsitePreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:43
 */
@Component
@Slf4j
@Scope("singleton")
public class FileChangeHandler implements IHandleTwTxtFileChanges {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private WebsitePreviewService websitePreviewService;

    @Autowired
    private PostFactory postFactory;

    @Value("${config.html.cache-dir}")
    private File htmlCachePath;

    private boolean active = false;

    @Override
    public void handle(final File file) {
        if (active == false) {

            active = true;
            log.info("--> ['" + file.getAbsolutePath() + "']" + " changed, reimporting posts");
            postRepository.deleteAll();
            final List<String> postLines = readLineByLineJava8(file);
            buildPosts(postLines);
            final File cacheDir = htmlCachePath;
            if (cacheDir.exists()) {
                Arrays.stream(cacheDir.listFiles()).forEach(File::delete);
            }
        }
    }

    private void buildPosts(final List<String> postLines) {
        final Integer[] i = {0};
        postLines.forEach(curPostLine -> {
            if (!StringUtils.isEmpty(curPostLine)) {
                final Post curPost = postFactory.build(curPostLine);
                if (curPost.hasContent()) {
                    postRepository.save(curPost);
                    i[0]++;
                }
            }
        });
        websitePreviewService.fetchMissingPreviews();
        log.info("--> ['" + i[0] + "']" + " posts imported");
        active = false;
    }

    private static List<String> readLineByLineJava8(File file) {
        final List<String> result = new ArrayList<String>();
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> result.add(s));
        } catch (IOException e) {
            log.error("could not read file ['" + file.getAbsolutePath() + "']", e);
        }
        return result;
    }
}
