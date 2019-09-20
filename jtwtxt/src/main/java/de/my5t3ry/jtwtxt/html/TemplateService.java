package de.my5t3ry.jtwtxt.html;

import org.antlr.stringtemplate.StringTemplate;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * User: my5t3ry
 * Date: 20.09.19 07:42
 */
@Component
public class TemplateService {

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
}
