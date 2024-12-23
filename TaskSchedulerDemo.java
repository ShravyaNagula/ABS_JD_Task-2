import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;

// Task interface defining a task that can be run
interface Task {
    void run();
    int getPriority(); // Lower priority value means higher priority (1 is the highest priority)
    String getTaskName(); // For identifying the task
}

// Sample Task implementation
class ExampleTask implements Task {
    private final String taskName;
    private final int priority;

    public ExampleTask(String taskName, int priority) {
        this.taskName = taskName;
        this.priority = priority;
    }

    @Override
    public void run() {
        System.out.println("Executing task: " + taskName + " with priority: " + priority);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }
}

// Task Scheduler that schedules tasks dynamically based on priority
class DynamicTaskScheduler {
    private final PriorityQueue<Task> taskQueue;
    private final ScheduledExecutorService scheduler;

    public DynamicTaskScheduler() {
        taskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::getPriority));
        scheduler = Executors.newScheduledThreadPool(1); // Single-threaded for simplicity
    }

    // Method to add a new task to the scheduler
    public void addTask(Task task, long delay, TimeUnit timeUnit) {
        taskQueue.offer(task);
        
        // Schedule the task to run after a specific delay
        scheduler.schedule(() -> {
            // Poll the task from the queue based on priority
            Task nextTask = taskQueue.poll();
            if (nextTask != null) {
                nextTask.run(); // Run the highest-priority task
            }
        }, delay, timeUnit);
    }

    // Shutdown the scheduler gracefully
    public void shutdown() {
        scheduler.shutdown();
    }
}

public class TaskSchedulerDemo {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        DynamicTaskScheduler taskScheduler = new DynamicTaskScheduler();

        System.out.println("Enter the number of tasks you want to schedule:");
        int numTasks = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character after nextInt()

        List<Task> tasks = new ArrayList<>();
        
        // Collect all tasks from the user
        for (int i = 0; i < numTasks; i++) {
            System.out.println("Enter task name:");
            String taskName = scanner.nextLine();

            System.out.println("Enter priority (integer, lower number = higher priority):");
            int priority = scanner.nextInt();

            System.out.println("Enter delay in seconds:");
            long delay = scanner.nextLong();
            scanner.nextLine(); // Consume the newline character after nextLong()

            // Create a task and add it to the list
            ExampleTask task = new ExampleTask(taskName, priority);
            tasks.add(task);

            // Add the task to the scheduler (it will be executed after the specified delay)
            taskScheduler.addTask(task, delay, TimeUnit.SECONDS);
        }

        // Wait for all tasks to finish execution before shutting down the scheduler
        long totalTimeToWait = 0;
        for (Task task : tasks) {
            // We need to account for the longest delay plus some buffer to ensure all tasks complete.
            totalTimeToWait += 10; // Arbitrary time buffer in case tasks run longer
        }
        Thread.sleep(totalTimeToWait * 1000); // Wait for the total time buffer

        // Shutdown the scheduler
        taskScheduler.shutdown();
        scanner.close();  // Don't forget to close the scanner
    }
}
