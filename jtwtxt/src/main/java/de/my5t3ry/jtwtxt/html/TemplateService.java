package de.my5t3ry.jtwtxt.html;

import de.my5t3ry.jtwtxt.post.AbstractContent;
import de.my5t3ry.jtwtxt.post.Post;
import org.antlr.stringtemplate.StringTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * User: my5t3ry
 * Date: 20.09.19 07:42
 */
@Component
public class TemplateService {

    @Value("${config.html.cache-dir}")
    private File htmlCacheDirPath;

    @PostConstruct
    public void init() {
        if (!htmlCacheDirPath.exists()) {
            htmlCacheDirPath.mkdir();
        }
    }

    public String getTemplate(String path, final Map<String, String> posts) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/templates/" + path);
        final StringTemplate stringTemplate = new StringTemplate(asString(resource));
        posts.entrySet().forEach(curEntry -> stringTemplate.setAttribute(curEntry.getKey(), curEntry.getValue()));
        return stringTemplate.toString();
    }

    public String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getPost(final Post curPost) {
        final Map<String, String> attributes = Map.of("copy", curPost.getCopy(),
                "created", curPost.getFormatedCreatedOn(),
                "content", getContents(curPost));
        return getTemplate("post.html", attributes);
    }

    private String getContents(final Post curPost) {
        final StringBuilder stringBuilder = new StringBuilder();
        curPost.getContent().forEach(curContent -> {
            stringBuilder.append(getContent(curContent));
        });
        return stringBuilder.toString();
    }

    public String getContent(final AbstractContent curContent) {
        final Map<String, String> attributes = Map.of("url", curContent.getUrl(),
                "description", curContent.getDescription(),
                "hash", curContent.getHashFromUrl());
        String contentTemplate;
        switch (curContent.getType()) {
            case YOUTUBE_EXTERNAL:
                contentTemplate = "youtube-content.html";
                break;
            case PERSON_TAG:
                contentTemplate = "inline-content.html";
                break;
            case SOUNDCLOUD_EXTERNAL:
                contentTemplate = "soundcloud-content.html";
                break;
            case PICTURE_MEDIA_TAG:
                contentTemplate = "picture-content.html";
                break;
            case WEBSITE_EXTERNAL:
                contentTemplate = "website-content.html";
                break;
            default:
                contentTemplate = "content.html";
        }
        return getTemplate(contentTemplate, attributes);
    }

    public String fetchHtml(final Iterable<Post> posts, final String query) {
        final File cacheFile = new File(htmlCacheDirPath, query);
        if (cacheFile.exists()) {
            try {
                return Files.readString(cacheFile.toPath());
            } catch (IOException e) {
                throw new IllegalStateException("Could not read cache file ['" + cacheFile.getAbsolutePath() + "']", e);
            }
        } else {
            try {
                final Map<String, String> attributes = Map.of("posts", getPosts(posts));
                final String htmlString = getTemplate("index.html", attributes);
                Files.writeString(cacheFile.toPath(), htmlString, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING});
                return htmlString;
            } catch (IOException e) {
                throw new IllegalStateException("Could not rebuild cache file", e);
            }
        }
    }

    private String getPosts(final Iterable<Post> posts) {
        final StringBuilder stringBuilder = new StringBuilder();
        posts.forEach(curPost -> {
            stringBuilder.append(getPost(curPost));
        });
        return stringBuilder.toString();
    }
}
