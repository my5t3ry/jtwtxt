package de.my5t3ry.jtwtxt.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalPostContent implements IPostContent {
    @Field(type = FieldType.Text, index = false)
    private PostContentType type = PostContentType.EXTERNAL;
    @Field(type = FieldType.Text, index = false)
    private String url;
    @Field(type = FieldType.Text, index = false)
    private String description;


    public ExternalPostContent(final String url, final PostContentType type) {
        this.url = url;
        this.type = type;
        this.description = url;
    }

    public ExternalPostContent(final String url) {
        this.url = url;
        this.description = url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IPostContent that = (IPostContent) o;
        return Objects.equals(url, that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
