import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class Simulation {

    private int clock = 0;
    private int count = 0;
    private final int arrivalTime = 5;
    private int stopFaultyCount = 100;
    private final int minReviewTime = 2;
    private final int maxReviewTime = 10;
    private boolean finished = false;
    private int preClock = 0;
    private int faultyCount = 0;
    private double faultPercent = 0.1;
    private int totalQueueLength = 0;
    private int maxQueueLength = 0;

    private Event lastEventById = null;
    private final Queue<Event> fel = new PriorityQueue<>();

    public void run() {
        initialize();
        while (!finished) {
            Event event = timeAdvance();
            eventHandling(event);
        }
        report();
    }

    private void initialize() {
        clock = 0;
        int serviceTime = getServiceTime(minReviewTime, maxReviewTime);
        Event e = new Event(count++, clock + arrivalTime, clock + arrivalTime, clock + arrivalTime + serviceTime, EventType.ARRIVAL);
        fel.add(e);

        lastEventById = e;
        
        System.out.println("------------------------------");
        System.out.println("t = " + clock);
        System.out.println("System initialized");
        System.out.println("Initial arrival (Part " + e.id + ") generated and scheduled for t = " + e.arrivalTime);
        System.out.println("------------------------------");
    }

    private Event timeAdvance() {
        preClock = clock;
        Event event = fel.peek();
        if (event.eventType == EventType.ARRIVAL) {
            clock = event.arrivalTime;
        } else if (event.eventType == EventType.LEAVING) {
            clock = event.endTime;
        } else {
            clock = event.startTime;
        }
        return fel.poll();
    }

    private void eventHandling(Event event) {
        if (preClock != clock) {
            System.out.println("------------------------------");
            System.out.println("t = " + clock);
        }

        int serviceTime = getServiceTime(minReviewTime, maxReviewTime);

        switch (event.eventType) {
            case ARRIVAL:
                System.out.println("Part " + event.id + " arrived for inspection");
                if (isInspectorBusy()) {
                    Event e = new Event(event.id, event.arrivalTime, event.startTime, event.endTime, EventType.IN_QUEUE);
                    fel.add(e);
                    System.out.println("Part " + event.id + " goes into queue");
                } else {
                    System.out.println("Inspection starts");
                    Event e = new Event(event.id, event.arrivalTime, event.startTime, event.endTime, EventType.LEAVING);
                    fel.add(e);
                    System.out.println("Part " + event.id + " scheduled to leave system at t = " + event.endTime);
                }
                System.out.println("Queue length = " + queueLength());
                break;
            case IN_QUEUE:
                Event e = new Event(event.id, event.arrivalTime, event.startTime, event.endTime, EventType.LEAVING);
                fel.add(e);
                System.out.println("Part " + event.id + " scheduled to leave system at t = " + event.endTime);
                break;
            case LEAVING:
                System.out.println("Inspection completed");
                if (Math.random() <= faultPercent) {
                    System.out.println("Part " + event.id + " is faulty");
                    faultyCount++;
                    if (faultyCount == stopFaultyCount) {
                        finished = true;
                    }
                } else {
                    System.out.println("Part " + event.id + " is working");
                }
                break;
        }

        if (preClock != clock) {
            totalQueueLength += queueLength();
            if (queueLength() > maxQueueLength) {
                maxQueueLength = queueLength();
            }
        }

        if (preClock != clock && clock % 5 == 0) {
            int at = clock + arrivalTime;
            int startTime = Math.max(lastEventById.endTime, at);
            Event e = new Event(count++, at, startTime, startTime + serviceTime, EventType.ARRIVAL);
            fel.add(e);

            lastEventById = e;

            System.out.println("New arrival (Part " + e.id + ") generated and scheduled for t = " + e.arrivalTime);
        }
        if (event.eventType == EventType.ARRIVAL) {
            printFEL();
        }
        System.out.println("------------------------------");
    }

    private void report() {
        System.out.println("------------------------------");
        System.out.println("### Report ###");
        printFEL();
        System.out.println("Total simulation time = " + clock);
        System.out.println("Length of FEL = " + fel.size());
        System.out.println("Average length of queue = " + ((double) totalQueueLength / count));
        System.out.println("Maximum length of queue = " + maxQueueLength);
    }

    // Including both
    private int getServiceTime(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    private void printFEL() {
        System.out.println("### Content of FEL ###");
        for (Event e : fel) {
            System.out.println(e);
        }
        System.out.println("### END ###");
    }

    private int queueLength() {
        int count = 0;
        for (Event e : fel) {
            if (e.eventType == EventType.IN_QUEUE) {
                count++;
            }
        }
        return count;
    }

    private boolean isInspectorBusy() {
        for (Event e : fel) {
            if (e.eventType == EventType.LEAVING) return true;
        }
        return false;
    }
}
