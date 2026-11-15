package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Collection;
import java.util.Comparator;

/**
 * Temporary seam between module D's query views and the {@link Room} /
 * {@link Booking} / {@link TimeSlot} bodies owned by modules B and C.
 *
 * <p>Module D's views originally called methods that live on the B and C
 * classes ({@code Room.describe()}, {@code Booking.describeWithUser(String)},
 * {@code Booking.describeWithRoom()}, {@code Booking.chronological()} and
 * {@code TimeSlot.toString()}). The compatibility layer must not edit those
 * class bodies, so the same text is produced here instead.</p>
 *
 * <p>This class is meant to disappear: once B adds {@code Room.describe()} and
 * C adds {@code Booking.describe*()} / {@code Booking.chronological()} /
 * {@code TimeSlot.toString()}, the views can call them directly again.</p>
 */
final class CompatViews {

    private CompatViews() { }

    /**
     * Renders a room as module D does in REQ13 / REQ15 / REQ16.
     *
     * @param room the room to render
     * @return "roomId roomType capacity features", features or "-" when empty
     */
    static String describe(Room room) {
        return room.getId() + " " + room.getType().getLabel() + " " + room.getCapacity()
                + " " + renderFeatures(room.getFeatures());
    }

    private static String renderFeatures(Collection<String> features) {
        if (features.isEmpty()) {
            return CrobsMessages.NO_FEATURES;
        }
        return String.join(",", features);
    }

    /**
     * A date plus a start and an end time, as printed by REQ14 and REQ17.
     *
     * @param slot the slot to render
     * @return "date startTime-endTime"
     */
    static String slotText(TimeSlot slot) {
        return slot.getDate() + " " + slot.getStartTime() + "-" + slot.getEndTime();
    }

    /**
     * Renders a booking as module D does in REQ14.
     *
     * @param booking  the booking to render
     * @param userText the owner to print, possibly masked by the caller
     * @return "bookingId date startTime-endTime userText purpose"
     */
    static String describeWithUser(Booking booking, String userText) {
        return booking.getId() + " " + slotText(booking.getTimeSlot()) + " " + userText
                + " " + booking.getPurpose();
    }

    /**
     * Renders a booking as module D does in REQ17.
     *
     * @param booking the booking to render
     * @return "bookingId roomId date startTime-endTime purpose"
     */
    static String describeWithRoom(Booking booking) {
        return booking.getId() + " " + booking.getRoomId() + " " + slotText(booking.getTimeSlot())
                + " " + booking.getPurpose();
    }

    /**
     * Date, then start time, then ID; same ordering as module D's
     * {@code Booking.chronological()}.
     *
     * @return a comparator that orders bookings chronologically
     */
    static Comparator<Booking> chronological() {
        return Comparator
                .comparing((Booking booking) -> booking.getTimeSlot().getDate())
                .thenComparing(booking -> booking.getTimeSlot().getStartTime())
                .thenComparing(Booking::getId);
    }
}
