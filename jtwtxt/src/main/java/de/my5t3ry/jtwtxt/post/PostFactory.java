package de.my5t3ry.jtwtxt.post;

import de.my5t3ry.jtwtxt.utils.TagExtractorService;
import de.my5t3ry.jtwtxt.utils.ExternalUrlExtractorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.my5t3ry.jtwtxt.post.Post.dateParser;

/**
 * User: my5t3ry
 * Date: 20.09.19 11:07
 */
@Component
@Slf4j
public class PostFactory {

    @Autowired
    private TagExtractorService tagExtractorService;
    @Autowired
    private ExternalUrlExtractorService externalUrlExtractorService;

    public Post build(final String curPostLine) {
        final Post result = new Post();
        result.setCreatedOn(parseDate(curPostLine));
        result.addContent(parseExternalContent(curPostLine));
        result.addContent(parseTagContent(curPostLine));
        result.setCopy(parseCopy(curPostLine));
        final String owner = System.getProperty("config.owner");
        if (StringUtils.isEmpty(owner)) {
            throw new IllegalStateException("Please configure the config.owner = vm parameter ['-Dconfig.owner=yourNamer']");
        }
        result.setOwner(owner);
        return result;
    }

    private List<? extends IPostContent> parseExternalContent(final String curPostLine) {
        final List<IPostContent> result = new ArrayList<IPostContent>();
        result.addAll(externalUrlExtractorService.grabExternalLinks(curPostLine));
        return result;
    }

    private List<? extends IPostContent> parseTagContent(final String curPostLine) {
        final List<IPostContent> result = new ArrayList<>();
        result.addAll(tagExtractorService.grabExternalLinks(curPostLine));
        return result;
    }

    private Date parseDate(final String curPostLine) {
        final String dateString = curPostLine.split("\t")[0];
        if (StringUtils.isEmpty(dateString)) {
            log.error("could not parse empty date ['" + dateString + "']");
        } else {
            try {
                return dateParser.parse(dateString.replace("T", " "));
            } catch (ParseException e) {
                log.error("could not parse date ['" + dateString + "']", e);
            }
        }
        return new Date();
    }

    private String parseCopy(final String curPostLine) {
        String copy = curPostLine.split("\t")[1].replaceAll("\\[LF\\]", "<br>");
        copy = copy.replaceAll("\\-\\>", "");
        if (StringUtils.isEmpty(copy)) {
            log.error("could not parse copy ['" + copy + "']");
        } else {
            return tagExtractorService.stripTags(externalUrlExtractorService.stripUrls(copy)).replaceAll("[ \\t]+$", "");
        }
        return "";
    }
}
