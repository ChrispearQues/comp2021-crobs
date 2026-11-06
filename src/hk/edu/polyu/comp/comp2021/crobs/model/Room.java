package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Set;

/** A bookable room. */
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

    /** REQ8: modify a single field. */
    public void modify(String field, String newValue) {
        // TODO(REQ8): validate field name and value, then update
        throw new UnsupportedOperationException("TODO: Room.modify");
    }
}
