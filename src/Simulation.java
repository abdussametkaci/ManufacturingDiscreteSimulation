import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class Simulation {

    private int clock = 0;
    private int count = 0;
    private int arrivalTime = 5;
    private int stopFaultyCount = 100;
    private int minReviewTime = 2;
    private int maxReviewTime = 10;
    private boolean finished = false;
    private int preClock = 0;
    private int faultyCount = 0;
    private int totalQueueLength = 0;
    private int maxQueueLength = 0;
    private Queue<Event> fel = new PriorityQueue<>();

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
        Event e = new Event(count++, clock + arrivalTime, EventType.ARRIVAL);
        fel.add(e);
        System.out.println("------------------------------");
        System.out.println("t = " + clock);
        System.out.println("System initialized");
        System.out.println("Initial arrival (Part " + e.id + ") generated and scheduled for t = " + e.scheduleTime);
        System.out.println("------------------------------");
    }

    private Event timeAdvance() {
        preClock = clock;
        clock = fel.peek().scheduleTime;
        return fel.poll();
    }

    private void eventHandling(Event event) {
        if (preClock != clock) {
            System.out.println("------------------------------");
            System.out.println("t = " + clock);
        }

        switch (event.eventType) {
            case ARRIVAL:
                System.out.println("Part " + event.id + " arrived for inspection");
                int serviceTime = getServiceTime(minReviewTime, maxReviewTime);
                int scheduleTime = clock + serviceTime;
                if (isInspectorBusy()) {
                    fel.add(new Event(event.id, fel.peek().scheduleTime, EventType.IN_QUEUE));
                    System.out.println("Part " + event.id + " goes into queue");
                } else {
                    System.out.println("Inspection starts");
                    fel.add(new Event(event.id, scheduleTime, EventType.LEAVING));
                    System.out.println("Part " + event.id + " scheduled to leave system at t = " + scheduleTime);
                }
                System.out.println("Queue length = " + queueLength());
                break;
            case IN_QUEUE:
                serviceTime = getServiceTime(minReviewTime, maxReviewTime);
                scheduleTime = clock + serviceTime;
                fel.add(new Event(event.id, scheduleTime, EventType.LEAVING));
                System.out.println("Part " + event.id + " scheduled to leave system at t = " + scheduleTime);
                fixQueue(fel.peek().scheduleTime);
                break;
            case LEAVING:
                System.out.println("Inspection completed");
                if ((new Random().nextInt(101)) == 10) {
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
            int scheduleTime = clock + arrivalTime;
            Event e = new Event(count++, scheduleTime, EventType.ARRIVAL);
            fel.add(e);
            System.out.println("New arrival (Part " + e.id + ") generated and scheduled for t = " + scheduleTime);
        }
        if (event.eventType == EventType.ARRIVAL) {
            printFEL();
        }
        System.out.println("------------------------------");
    }

    private void report() {
        System.out.println("------------------------------");
        System.out.println("### Report ###");
        System.out.println("Total simulation time = " + clock);
        System.out.println("Length of FEL = " + fel.size());
        printFEL();
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

    private void fixQueue(int time) {
        for (Event e : fel) {
            if (e.eventType == EventType.IN_QUEUE) {
                e.scheduleTime = time;
            }
        }
    }

    private boolean isInspectorBusy() {
        for (Event e : fel) {
            if (e.eventType == EventType.LEAVING) return true;
        }
        return false;
    }
}
