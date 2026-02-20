import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobScheduler {
    private List<Job> allJobs = new ArrayList<>();
    private List<Job> newJobs = new ArrayList<>();
    private List<Job> runningJobs = new ArrayList<>();
    private List<Job> finishedJobs = new ArrayList<>();
    private List<Job> sleepingJobs = new ArrayList<>();
    private Memory memory;
    private long tickCounter = 0;

    public JobScheduler(Memory memory) {
        this.memory = memory;
    }

    public void addJob(Job job) {
        allJobs.add(job);
        newJobs.add(job);
    }

    public void tick() {
        tickCounter++;

        // 1. Move NEW → RUNNING
        Iterator<Job> newIter = newJobs.iterator();
        while (newIter.hasNext()) {
            Job job = newIter.next();

            if (job.startTime <= tickCounter) {

                boolean allocated = memory.allocate(job, tickCounter);

                while (!allocated) {
                    Job evicted = memory.evictLRU();
                    if (evicted == null) break;

                    sleepingJobs.remove(evicted);
                    allocated = memory.allocate(job, tickCounter);
                }

                if (allocated) {
                    job.currentState = State.RUNNING;
                    runningJobs.add(job);
                    newIter.remove();
                }
            }
        }

        // 2. Update RUNNING jobs
        Iterator<Job> runIter = runningJobs.iterator();
        while (runIter.hasNext()) {
            Job job = runIter.next();

            job.tick();
            memory.touch(job, tickCounter);

            if (job.isFinished()) {

                if (job.finalState == State.END) {
                    job.currentState = State.END;
                    memory.deallocate(job);
                    finishedJobs.add(job);
                }
                else if (job.finalState == State.SLEEP) {
                    job.currentState = State.SLEEP;
                    sleepingJobs.add(job);   // keep memory
                    // DO NOT deallocate
                }

                runIter.remove();
            }
        }
    }


    public void printStatus() {
        System.out.println("Tick: " + tickCounter);
        System.out.println("Running jobs: " + runningJobs.stream().map(j -> j.jobId).toList());
        System.out.println("New jobs: " + newJobs.stream().map(j -> j.jobId).toList());
        System.out.println("Sleeping jobs: " +
                sleepingJobs.stream().map(j -> j.jobId).toList());
        System.out.println("Finished jobs: " + finishedJobs.stream().map(j -> j.jobId).toList());
        System.out.println("Free memory blocks: " + memory.getFreeBlocks());
        System.out.println("-----------");
    }
}
