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

    /**
     * Creates a room.
     *
     * @param id       the unique room ID
     * @param type     the kind of room
     * @param capacity how many people fit in the room
     * @param features the normalised feature set, empty when the room has none
     */
    public Room(String id, RoomType type, int capacity, Set<String> features) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.features = normaliseFeatures(features);
    }

    /** Lower-cases and de-duplicates the features of a room. */
    private static Set<String> normaliseFeatures(Collection<String> raw) {
        Set<String> normalised = new LinkedHashSet<>();
        if (raw == null) {
            return normalised;
        }
        for (String feature : raw) {
            if (feature != null && !feature.trim().isEmpty()) {
                normalised.add(feature.trim().toLowerCase(Locale.ROOT));
            }
        }
        return normalised;
    }

    /** @return the unique room ID */
    public String getId() { return id; }

    /** @return the kind of room */
    public RoomType getType() { return type; }

    /** @return how many people fit in the room */
    public int getCapacity() { return capacity; }

    /** @return the normalised feature set, empty when the room has none */
    public Set<String> getFeatures() { return features; }

    /**
     * REQ8: modify a single field. The supported field names are
     * {@code type}, {@code capacity} and {@code features}.
     *
     * @param field    the name of the field to change
     * @param newValue the new value of that field
     * @throws CrobsException.MalformedCommandException when the field is unknown
     *         or the new value is invalid
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

    /**
     * Checks the feature requirement of REQ13.
     *
     * @param wanted the features the searcher asked for, already normalised
     * @return true when this room contains every requested feature
     */
    public boolean hasAllFeatures(Set<String> wanted) {
        if (wanted == null || wanted.isEmpty()) {
            return true;
        }
        for (String feature : wanted) {
            if (feature == null
                    || !features.contains(feature.trim().toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Renders the room as it appears in the output of REQ13, REQ15 and REQ16.
     *
     * @return "roomId roomType capacity features"
     */
    public String describe() {
        return id + " " + type.getLabel() + " " + capacity + " " + renderFeatures(features);
    }

    /**
     * Parses a comma-separated feature list. A dash or an empty text means
     * "no feature", and empty tokens are ignored.
     *
     * @param text the raw feature list of the command line
     * @return the normalised feature set, never null
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

    /**
     * Comma-separated feature list, or a dash when the set is empty.
     *
     * @param features the features to render
     * @return the features separated by commas, or "-" when there is none
     */
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
     * Parses a capacity value.
     *
     * @param text    the text to parse
     * @param message the message pattern to use when the text is invalid
     * @return the parsed capacity, always positive
     * @throws CrobsException.MalformedCommandException when the text is not an
     *         integer, or is negative / zero where a positive value is required
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
