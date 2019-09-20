package de.my5t3ry.jtwtxt.post;

import de.my5t3ry.jtwtxt.utils.TagExtractor;
import de.my5t3ry.jtwtxt.utils.UrlExtractor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:54
 */
@Slf4j
@org.springframework.data.elasticsearch.annotations.Document(indexName = "post", shards = 1, type = "post")
@Data
@NoArgsConstructor
public class Post {
    @Id
    @Field(type = FieldType.Text, store = true)
    private String id;
    @Field(type = FieldType.Date, index = false)
    private Date createdOn;
    @Field(type = FieldType.Nested)
    private List<IPostContent> content = new ArrayList<>();
    @Field(type = FieldType.Text, index = false)
    private String title;
    @Field(type = FieldType.Text, index = false)
    private String owner;
    @Field(type = FieldType.Text, index = false)
    private String copy;
    @Transient
    SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Transient
    private final UrlExtractor urlExtractor = new UrlExtractor();
    @Transient
    private final TagExtractor tagExtractor = new TagExtractor();


    public Post(final String curPostLine) {
        this.createdOn = parseDate(curPostLine);
        this.content.addAll(parseExternalContent(curPostLine));
        this.content.addAll(parseTagContent(curPostLine));
        this.copy = parseCopy(curPostLine);
        final String owner = System.getProperty("config.owner");
        if (StringUtils.isEmpty(owner)) {
            throw new IllegalStateException("Please configure the config.owner = vm parameter ['-Dconfig.owner=yourNamer']");
        }
        this.owner = owner;
    }

    private Collection<? extends IPostContent> parseExternalContent(final String curPostLine) {
        final List<IPostContent> result = new ArrayList<IPostContent>();
        result.addAll(urlExtractor.grabExternalLinks(curPostLine));
        return result;
    }

    private Collection<? extends IPostContent> parseTagContent(final String curPostLine) {
        final List<IPostContent> result = new ArrayList<>();
        result.addAll(tagExtractor.grabExternalLinks(curPostLine));
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

    public String getFormatedCreatedOn() {
        return dateParser.format(createdOn);
    }

    private String parseCopy(final String curPostLine) {
        String copy = curPostLine.split("\t")[1].replaceAll("\\[LF\\]", "<br>");
        copy = copy.replaceAll("\\-\\>", "");
        if (StringUtils.isEmpty(copy)) {
            log.error("could not parse copy ['" + copy + "']");
        } else {
            return tagExtractor.stripTags(urlExtractor.stripUrls(copy));
        }
        return "";
    }
}
