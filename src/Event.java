public class Event implements Comparable<Event> {

    public final int id;
    public int scheduleTime;
    public EventType eventType;

    public Event(int id, int scheduleTime, EventType eventType) {
        this.id = id;
        this.scheduleTime = scheduleTime;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event(id: " + this.id + ", scheduleTime: " + this.scheduleTime + ", eventType: " + eventType + ")";
    }

    @Override
    public int compareTo(Event o) {
        int priority = Integer.compare(this.scheduleTime, o.scheduleTime);
        return priority == 0 ? Integer.compare(o.eventType.ordinal(), this.eventType.ordinal()) : priority;
    }
}
