package de.my5t3ry.jtwtxt.post;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:58
 */
@Data
@NoArgsConstructor
public class TagPostContent implements IPostContent {
    @Field(type = FieldType.Text, index = false)
    private final PostContentType type =PostContentType.TAG;
    @Field(type = FieldType.Text, index = false)
    private String url;
    @Field(type = FieldType.Text, index = false)
    private String description;

    public TagPostContent(final String url, final String description) {
        this.url = url;
        this.description = description;
    }
}
