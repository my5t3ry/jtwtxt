package de.my5t3ry.jtwtxt.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalPostContent implements IPostContent {
    @Field(type = FieldType.Text, index = false)
    private final PostContentType type = PostContentType.EXTERNAL;
    @Field(type = FieldType.Text, index = false)
    private  String url;
    @Field(type = FieldType.Text, index = false)
    private  String description;

    public ExternalPostContent(final String url) {
        this.url = url;
        this.description = url;
    }
}
