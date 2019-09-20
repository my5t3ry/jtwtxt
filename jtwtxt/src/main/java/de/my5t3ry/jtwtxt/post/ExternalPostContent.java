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
public class ExternalPostContent extends AbstractContent {
    @Field(type = FieldType.Text)
    private PostContentType type = PostContentType.WEBSITE_EXTERNAL;
    @Field(type = FieldType.Text, index = false)
    private String url;
    @Field(type = FieldType.Text)
    private String description;

    public ExternalPostContent(final String url) {
        this.url = url;
        this.type = determineType(url);
        this.description = url;
    }


    private PostContentType determineType(final String url) {
        if (url.contains("youtu")) {
            return PostContentType.YOUTUBE_EXTERNAL;
        } else if (url.contains("soundcloud")) {
            return PostContentType.SOUNDCLOUD_EXTERNAL;
        }
        return PostContentType.WEBSITE_EXTERNAL;
    }

    public String getUrl() {
        if (type.equals(PostContentType.YOUTUBE_EXTERNAL)) {
            return url.replace("watch?v=", "embed/").replace("youtu.be", "youtube.com/embed");
        }
        return url;
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
