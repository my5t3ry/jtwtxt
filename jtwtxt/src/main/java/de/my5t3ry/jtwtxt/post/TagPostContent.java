package de.my5t3ry.jtwtxt.post;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.Objects;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:58
 */
@Data
@NoArgsConstructor
public class TagPostContent extends AbstractContent  {


    @Field(type = FieldType.Text, index = false)
    private PostContentType type;
    @Field(type = FieldType.Text, index = false)
    private String url;
    @Field(type = FieldType.Text, index = false)
    private String description;


    public TagPostContent(final String url, final String description, final PostContentType type) {
        this.type = type;
        this.url = url;
        this.description = description;
    }

    public TagPostContent(final String url, final String description) {
        this.url = url;
        this.type = determineType(url);
        this.description = description;
    }

    private PostContentType determineType(final String url) {
        if (Arrays.stream(new String[]{"gif", "jpg", "jpeg", "png"}).parallel().anyMatch(url.toLowerCase()::contains)) {
            return PostContentType.PICTURE_MEDIA_TAG;
        }
        return PostContentType.VIDEO_MEDIA_TAG;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractContent that = (AbstractContent) o;
        return Objects.equals(url, that.getUrl());
    }


    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
