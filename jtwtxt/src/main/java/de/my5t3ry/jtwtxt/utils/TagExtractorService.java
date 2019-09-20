package de.my5t3ry.jtwtxt.utils;

import de.my5t3ry.jtwtxt.html.TemplateService;
import de.my5t3ry.jtwtxt.post.TagPostContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: my5t3ry
 * Date: 20.09.19 05:27
 */
@Component
public class TagExtractorService {
    @Autowired
    private TemplateService templateService;

    private final Pattern userTagPatternUrl;
    private final Pattern mediaTagPatternUrl;

    private static final String mediaTagRegex = "\\@\\<(.*?)\\>";
    private static final String userTagRegex = "\\@\\[(.*?)\\]";

    public TagExtractorService() {
        mediaTagPatternUrl = Pattern.compile(mediaTagRegex, Pattern.CASE_INSENSITIVE);
        userTagPatternUrl = Pattern.compile(userTagRegex, Pattern.CASE_INSENSITIVE);
    }

    public Vector<TagPostContent> grabExternalLinks(String string) {
        Vector<TagPostContent> result = new Vector<>();
        final Matcher mediaTagMatcher = mediaTagPatternUrl.matcher(string);
        while (mediaTagMatcher.find()) {
            final String tag = mediaTagMatcher.group(1).replace("@<", "").replace(">", "");
            final String[] split = tag.split(" ");
            if (split.length == 2) {
                result.add(new TagPostContent(split[1], split[0]));
            } else {
                result.add(new TagPostContent(tag, tag));
            }
        }
        return result;
    }

    public String stripTags(String string) {
        String result = "";
        final String stripedString = string.replaceAll(mediaTagRegex, "");
        final Matcher userTagMatcher = userTagPatternUrl.matcher(string);
        while (userTagMatcher.find()) {
            final String tag = userTagMatcher.group(1).replace("@[", "").replace("]", "");
            final String[] split = tag.split(":");
            if (split.length == 3) {
                result = stripedString.replaceAll(userTagRegex, templateService.getContent(new TagPostContent("https://facebook.com/" + split[2], split[2])));
            } else {
                result = stripedString;
            }
        }
        return result;
    }
}
