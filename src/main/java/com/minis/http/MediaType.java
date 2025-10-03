package com.minis.http;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 表示HTTP媒体类型
 */
public class MediaType {
    public static final MediaType ALL = new MediaType("*", "*");
    public static final MediaType APPLICATION_JSON = new MediaType("application", "json");
    public static final MediaType APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
    public static final MediaType APPLICATION_XML = new MediaType("application", "xml");
    public static final MediaType TEXT_HTML = new MediaType("text", "html");
    public static final MediaType TEXT_PLAIN = new MediaType("text", "plain");

    private final String type;
    private final String subtype;
    private final Map<String, String> parameters;

    public MediaType(String type, String subtype) {
        this(type, subtype, Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subtype = subtype.toLowerCase(Locale.ENGLISH);
        this.parameters = parameters;
    }

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean isCompatibleWith(MediaType other) {
        if (other == null) {
            return false;
        }
        if (this.type.equals("*") || other.type.equals("*")) {
            return true;
        }
        else if (this.type.equals(other.type)) {
            if (this.subtype.equals("*") || other.subtype.equals("*")) {
                return true;
            }
            else if (this.subtype.equals(other.subtype)) {
                return true;
            }
        }
        return false;
    }

    public boolean includes(MediaType other) {
        return isCompatibleWith(other);
    }

    public boolean isConcrete() {
        return !this.type.equals("*") && !this.subtype.equals("*");
    }

    public static void sortBySpecificityAndQuality(List<MediaType> mediaTypes) {
        mediaTypes.sort((mediaType1, mediaType2) -> {
            // 首先根据特定性排序
            int typeSpecificity = compareSpecificity(mediaType1.getType(), mediaType2.getType());
            if (typeSpecificity != 0) {
                return typeSpecificity;
            }
            return compareSpecificity(mediaType1.getSubtype(), mediaType2.getSubtype());
        });
    }

    private static int compareSpecificity(String value1, String value2) {
        // 非通配符的值比通配符更具体
        if (value1.equals("*") && !value2.equals("*")) {
            return 1; // value2更具体
        }
        else if (!value1.equals("*") && value2.equals("*")) {
            return -1; // value1更具体
        }
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaType)) {
            return false;
        }
        MediaType otherType = (MediaType) other;
        return (this.type.equalsIgnoreCase(otherType.type) &&
                this.subtype.equalsIgnoreCase(otherType.subtype) &&
                this.parameters.equals(otherType.parameters));
    }

    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.subtype.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.type);
        builder.append('/');
        builder.append(this.subtype);
        this.parameters.forEach((key, value) -> {
            builder.append(';');
            builder.append(key);
            builder.append('=');
            builder.append(value);
        });
        return builder.toString();
    }
}
