package hk.edu.polyu.comp.comp2021.crobs.model;

/** The four room types supported by Crobs (REQ7). */
public enum RoomType {

    /** A large hall with fixed seating. */
    LECTURE_THEATRE("lectureTheatre"),

    /** A room full of workstations. */
    COMPUTER_LAB("computerLab"),

    /** A room for small group teaching. */
    SEMINAR_ROOM("seminarRoom"),

    /** A room for meetings. */
    MEETING_ROOM("meetingRoom");

    private final String label;

    RoomType(String label) { this.label = label; }

    /** @return the text used on the command line and in the output */
    public String getLabel() { return label; }

    /**
     * Maps the text used on the command line back to the enum constant.
     *
     * @param label the room type as it was typed
     * @return the matching constant
     * @throws CrobsException.MalformedCommandException when the label is not a supported room type
     */
    public static RoomType fromLabel(String label) {
        for (RoomType type : values()) {
            if (type.label.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.INVALID_ROOM_TYPE, label));
    }
}
