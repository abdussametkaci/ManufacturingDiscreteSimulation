public class Event implements Comparable<Event> {

    public final int id;
    public int arrivalTime;
    public int startTime;
    public int endTime;
    public EventType eventType;

    public Event(int id, int arrivalTime, int startTime, int endTime, EventType eventType) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event(id: " + this.id +
                ", arrivalTime: " + this.arrivalTime +
                ", startTime: " + this.startTime +
                ", endTime: " + this.endTime +
                ", eventType: " + eventType + ")";
    }

    @Override
    public int compareTo(Event other) {
        int thisCmpTime;
        int otherCmpTime;

        if (this.eventType == EventType.ARRIVAL) thisCmpTime = this.arrivalTime;
        else if (this.eventType == EventType.IN_QUEUE) thisCmpTime = this.startTime;
        else thisCmpTime = this.endTime;

        if (other.eventType == EventType.ARRIVAL) otherCmpTime = other.arrivalTime;
        else if (other.eventType == EventType.IN_QUEUE) otherCmpTime = other.startTime;
        else otherCmpTime = other.endTime;

        int priority = Integer.compare(thisCmpTime, otherCmpTime);
        return priority == 0 ? Integer.compare(other.eventType.ordinal(), this.eventType.ordinal()) : priority;
    }
}
