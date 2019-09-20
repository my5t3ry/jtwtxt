package de.my5t3ry.jtwtxt.post;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: my5t3ry
 * Date: 20.09.19 16:09
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AbstractContent {
    public abstract PostContentType getType();

    public abstract String getUrl();

    public abstract String getDescription();

    public abstract boolean equals(Object o);

    public String getHashFromUrl() {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(this.getUrl().getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            return new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't create hash for preview names.", e);
        }
    }
}
