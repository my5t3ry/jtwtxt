package de.my5t3ry.jtwtxt.post;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:56
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface IPostContent {
    PostContentType getType();

    String getUrl();

    String getDescription();

    boolean equals(Object o);
}
