package de.my5t3ry.jtwtxt.utils;

import de.my5t3ry.jtwtxt.post.TagPostContent;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: my5t3ry
 * Date: 20.09.19 05:27
 */
public class TagExtractor {

    private Pattern patternUrl;

    private static final String urlRegex = "\\@\\<(.*?)\\>";

    public TagExtractor() {
        patternUrl = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    }

    public Vector<TagPostContent> grabExternalLinks(String string) {
        Vector<TagPostContent> result = new Vector<>();
        final Matcher tagMatcher = patternUrl.matcher(string);
        while (tagMatcher.find()) {
            final String tag = tagMatcher.group(1).replace("@<", "").replace(">", "");
            final String[] split = tag.split(" ");
            if (split.length == 2) {
                result.add(new TagPostContent(split[1], split[0]));
            } else {
                result.add(new TagPostContent(tag, tag));
            }
        }
        return result;
    }

    public String stripTags(final String string) {
        return string.replaceAll(urlRegex, "");
    }

}
