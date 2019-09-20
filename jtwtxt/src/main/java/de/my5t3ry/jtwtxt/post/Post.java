package de.my5t3ry.jtwtxt.post;

import de.my5t3ry.jtwtxt.utils.TagExtractorService;
import de.my5t3ry.jtwtxt.utils.UrlExtractorService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Transient
    private final UrlExtractorService urlExtractorService = new UrlExtractorService();
    @Transient
    private final TagExtractorService tagExtractorService = new TagExtractorService();



    public String getFormatedCreatedOn() {
        return dateParser.format(createdOn);
    }

    public void addContent(final List<? extends IPostContent> parseExternalContent) {
        this.content.addAll(parseExternalContent);
    }
}
