package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A room that can be booked (REQ7, REQ8, REQ15, REQ16).
 *
 * <p>Features are normalised to lower case so that {@code Projector} and
 * {@code projector} mean the same thing when searching.</p>
 */
public class Room {

    private final String id;
    private RoomType type;
    private int capacity;
    private Set<String> features;

    public Room(String id, RoomType type, int capacity, Set<String> features) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.features = features;
    }

    public String getId() { return id; }

    public RoomType getType() { return type; }

    public int getCapacity() { return capacity; }

    public Set<String> getFeatures() { return features; }

    /**
     * REQ8: modify a single field. The supported field names are
     * {@code type}, {@code capacity} and {@code features}.
     *
     * @throws CrobsException.MalformedCommandException when the field is unknown
     *         or the new value is invalid.
     */
    public void modify(String field, String newValue) {
        String name = field.toLowerCase(Locale.ROOT);
        if ("type".equals(name)) {
            this.type = RoomType.fromLabel(newValue);
        } else if ("capacity".equals(name)) {
            this.capacity = parseCapacity(newValue, CrobsMessages.INVALID_CAPACITY);
        } else if ("features".equals(name)) {
            this.features = parseFeatures(newValue);
        } else {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.INVALID_FIELD, field));
        }
    }

    /** True when this room contains every feature the searcher asked for. */
    public boolean hasAllFeatures(Set<String> wanted) {
        return features.containsAll(wanted);
    }

    /** "R001 lectureTheatre 120 projector,whiteboard" (REQ13 / REQ15 / REQ16). */
    public String describe() {
        return id + " " + type.getLabel() + " " + capacity + " " + renderFeatures(features);
    }

    /**
     * Parses a comma-separated feature list. A dash or an empty text means
     * "no feature", and empty tokens are ignored.
     */
    public static Set<String> parseFeatures(String text) {
        Set<String> parsed = new LinkedHashSet<>();
        if (text == null) {
            return parsed;
        }
        for (String token : text.split(",")) {
            String feature = token.trim().toLowerCase(Locale.ROOT);
            if (!feature.isEmpty() && !CrobsMessages.NO_FEATURES.equals(feature)) {
                parsed.add(feature);
            }
        }
        return parsed;
    }

    /** Comma-separated feature list, or a dash when the set is empty. */
    public static String renderFeatures(Collection<String> features) {
        if (features.isEmpty()) {
            return CrobsMessages.NO_FEATURES;
        }
        StringBuilder text = new StringBuilder();
        for (String feature : features) {
            if (text.length() > 0) {
                text.append(',');
            }
            text.append(feature);
        }
        return text.toString();
    }

    /**
     * @throws CrobsException.MalformedCommandException when the text is not an
     *         integer, or is negative / zero when a positive value is required.
     */
    public static int parseCapacity(String text, String message) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new CrobsException.MalformedCommandException(String.format(message, text));
        }
        if (value <= 0) {
            throw new CrobsException.MalformedCommandException(String.format(message, text));
        }
        return value;
    }
}
