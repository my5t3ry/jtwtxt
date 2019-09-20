package de.my5t3ry.jtwtxt.utils;

import de.my5t3ry.jtwtxt.post.ExternalPostContent;
import de.my5t3ry.jtwtxt.post.PostContentType;
import org.springframework.stereotype.Component;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: my5t3ry
 * Date: 20.09.19 05:27
 */
@Component
public class ExternalUrlExtractorService {

    private Pattern patternUrl;

    private static final String urlRegex = "https?://\\S+\\s?";

    public ExternalUrlExtractorService() {
        patternUrl = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    }

    public Vector<ExternalPostContent> grabExternalLinks(final String string) {
        Vector<ExternalPostContent> result = new Vector<ExternalPostContent>();
        final Matcher urlMatcher = patternUrl.matcher(string);
        while (urlMatcher.find()) {
            final String url = string.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
            ExternalPostContent obj = new ExternalPostContent(url, determineType(url));
            result.add(obj);
        }
        return result;
    }

    private PostContentType determineType(final String url) {
        if (url.contains("youtu")) {
            return PostContentType.YOUTUBE_EXTERNAL;
        } else if (url.contains("soundcloud")) {
            return PostContentType.SOUNDCLOUD_EXTERNAL;
        }
        return PostContentType.EXTERNAL;
    }

    public String stripUrls(final String string) {
        return string.replaceAll(urlRegex, "");
    }

}
