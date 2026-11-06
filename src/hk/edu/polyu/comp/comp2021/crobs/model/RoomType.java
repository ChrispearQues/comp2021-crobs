package hk.edu.polyu.comp.comp2021.crobs.model;

/** The four room types supported by Crobs (REQ7). */
public enum RoomType {
    LECTURE_THEATRE("lectureTheatre"),
    COMPUTER_LAB("computerLab"),
    SEMINAR_ROOM("seminarRoom"),
    MEETING_ROOM("meetingRoom");

    private final String label;

    RoomType(String label) { this.label = label; }

    public String getLabel() { return label; }

    /** @throws CrobsException if the label is not a supported room type. */
    public static RoomType fromLabel(String label) {
        // TODO(REQ7): map label back to the enum constant
        throw new UnsupportedOperationException("TODO: RoomType.fromLabel");
    }
}
