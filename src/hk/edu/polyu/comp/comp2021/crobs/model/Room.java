package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** A bookable room. */
public class Room {

    private final String id;          // Unique room ID (e.g., R001), immutable
    private RoomType type;            // Room classification (REQ7)
    private int capacity;             // Maximum seating capacity (must be > 0)
    private Set<String> features;     // Set of unique lowercase room features

    /** Constructs a validated Room instance. */
    public Room(String id, RoomType type, int capacity, Set<String> features) {
        if (id == null || id.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("Room ID cannot be empty.");
        }
        if (type == null) {
            throw new CrobsException.MalformedCommandException("Room type cannot be null.");
        }
        if (capacity <= 0) {
            throw new CrobsException.MalformedCommandException("Room capacity must be a positive integer.");
        }
        this.id = id.trim();
        this.type = type;
        this.capacity = capacity;
        this.features = new LinkedHashSet<>();
        if (features != null) {
            for (String f : features) {
                if (f != null && !f.trim().isEmpty()) {
                    this.features.add(f.trim().toLowerCase(Locale.ROOT));
                }
            }
        }
    }

    public String getId() { return id; }

    public RoomType getType() { return type; }

    public int getCapacity() { return capacity; }

    /** Returns an unmodifiable view of the room's features. */
    public Set<String> getFeatures() { return Collections.unmodifiableSet(features); }

    /** REQ8: modifies a single field (roomType, capacity, or features). */
    public void modify(String field, String newValue) {
        if (field == null || field.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("Field name cannot be empty.");
        }
        if (newValue == null || newValue.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("New value cannot be empty.");
        }

        switch (field.trim()) {
            case "roomType":
                this.type = RoomType.fromLabel(newValue);
                break;
            case "capacity":
                this.capacity = parseCapacity(newValue);
                break;
            case "features":
                this.features = parseFeatures(newValue);
                break;
            default:
                throw new CrobsException.MalformedCommandException(
                        "Invalid field '" + field + "'. Allowed fields: roomType, capacity, features.");
        }
    }

    /** Helper to parse and validate a positive capacity string (used in REQ7 & REQ8). */
    public static int parseCapacity(String capacityStr) {
        if (capacityStr == null || capacityStr.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("Capacity cannot be empty.");
        }
        try {
            int cap = Integer.parseInt(capacityStr.trim());
            if (cap <= 0) {
                throw new CrobsException.MalformedCommandException("Capacity must be greater than 0.");
            }
            return cap;
        } catch (NumberFormatException e) {
            throw new CrobsException.MalformedCommandException("Invalid capacity number: " + capacityStr);
        }
    }

    /** Helper to split comma-separated features into lowercase tokens ("-" or empty becomes none). */
    public static Set<String> parseFeatures(String featuresStr) {
        Set<String> result = new LinkedHashSet<>();
        if (featuresStr == null || featuresStr.trim().isEmpty() || "-".equals(featuresStr.trim())) {
            return result;
        }
        for (String item : featuresStr.split(",")) {
            String trimmed = item.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /** Returns true if this room contains all requested features (case-insensitive, used in REQ13). */
    public boolean hasAllFeatures(Collection<String> requiredFeatures) {
        if (requiredFeatures == null || requiredFeatures.isEmpty()) {
            return true;
        }
        for (String req : requiredFeatures) {
            if (req == null || !this.features.contains(req.trim().toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    /** Formats room details for query views: id type capacity features ("-" if no features). */
    public String describe() {
        String featureText = features.isEmpty() ? "-" : String.join(",", features);
        return String.format("%s %s %d %s", id, type.getLabel(), capacity, featureText);
    }

    @Override
    public String toString() {
        String featureText = features.isEmpty() ? "none" : String.join(",", features);
        return String.format("%s | %s | capacity=%d | features=%s",
                id, type.getLabel(), capacity, featureText);
    }
}
