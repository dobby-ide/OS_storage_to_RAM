import java.util.*;

import java.util.*;

public class Memory {

    private int totalSize;
    private boolean[] occupied;                   // tracks occupied frames
    private Map<Integer, Job> blockToJob;         // which job occupies each block
    private Map<Integer, String> blockToFile;     // which file occupies each block
    private LinkedHashMap<Job, Long> usageTracker; // LRU tracking for jobs (last tick)

    public Memory(int totalSize) {
        this.totalSize = totalSize;
        this.occupied = new boolean[totalSize];
        this.blockToJob = new HashMap<>();
        this.blockToFile = new HashMap<>();
        this.usageTracker = new LinkedHashMap<>();
    }

    // ----------------- JOB METHODS -----------------

    // allocate memory for a job; returns true if successful
    public boolean allocate(Job job, long tick) {
        List<Integer> allocated = new ArrayList<>();

        for (int i = 0; i < totalSize && allocated.size() < job.size; i++) {
            if (!occupied[i]) {
                occupied[i] = true;
                allocated.add(i);
                blockToJob.put(i, job);
            }
        }

        if (allocated.size() != job.size) {
            freeBlocks(allocated);
            return false;
        }

        job.allocateFrames(allocated);
        touch(job, tick);
        return true;
    }

    // deallocate memory for a job
    public void deallocate(Job job) {
        System.out.println("Deallocating job " + job.jobId + ", frames: " + job.allocatedFrames);
        for (int b : job.allocatedFrames) {
            occupied[b] = false;
            blockToJob.remove(b);
        }
        job.deallocateFrames();
        usageTracker.remove(job);
    }

    // update LRU timestamp
    public void touch(Job job, long tick){

            usageTracker.remove(job);  // remove old entry

        usageTracker.put(job, tick);   // re-insert with new timestamp
    }

    // evict the least recently used sleeping job
    public Job evictLRU() {
        System.out.println("evictLRU called. usageTracker size: " + usageTracker.size());
        for (Map.Entry<Job, Long> entry : usageTracker.entrySet()) {
            Job job = entry.getKey();
            System.out.println("Checking job " + job.jobId + " in state " + job.currentState);

            if (job.currentState == State.SLEEP) {
                System.out.println("Trying to evict job: " + job.jobId + " in state " + job.currentState);
                deallocate(job);
                return job;
            }
        }
        System.out.println("No evictable job found");
        return null;
    }

    public int getFreeBlocks() {
        int count = 0;
        for (boolean b : occupied) if (!b) count++;
        return count;
    }

    // ----------------- FILE METHODS -----------------

    // allocate memory for a file (size = number of blocks), without eviction
    public List<Integer> allocateFile(int size) {
        List<Integer> allocated = new ArrayList<>();
        for (int i = 0; i < totalSize && allocated.size() < size; i++) {
            if (!occupied[i]) {
                allocated.add(i);
            }
        }

        if (allocated.size() != size) {
            // rollback partially allocated frames
            for (int b : allocated) occupied[b] = false;
            return null;
        }

        // mark frames as occupied
        for (int b : allocated) occupied[b] = true;
        return allocated;
    }

    // allocate memory for a file, evicting sleeping jobs if needed
    public List<Integer> allocateFileWithEviction(int size, long tick) {
        List<Integer> allocated = null;

        while (true) {
            allocated = allocateFile(size);
            if (allocated != null) return allocated;  // success

            // try to evict the next LRU sleeping job
            Job evicted = evictLRU();
            if (evicted == null) break;  // no more jobs to evict
            System.out.println("Evicted job " + evicted.jobId + " to free memory");
        }

        // failed after evicting all possible jobs
        return null;
    }

    // associate allocated file blocks with a file name
    public void assignFileBlocks(String fileName, List<Integer> blocks) {
        for (int b : blocks) {
            blockToFile.put(b, fileName);
        }
    }

    // free blocks used by a file
    public void freeFileBlocks(List<Integer> blocks) {
        for (int b : blocks) {
            occupied[b] = false;
            blockToFile.remove(b);
        }
    }

    // check if enough space exists for a file
    public boolean hasSpace(int size) {
        int count = 0;
        for (boolean b : occupied) if (!b) count++;
        return count >= size;
    }

    // helper to free blocks (used internally)
    private void freeBlocks(List<Integer> blocks) {
        for (int b : blocks) occupied[b] = false;
    }

    public void printMemoryState() {
        System.out.print("Memory: ");
        for (int i = 0; i < totalSize; i++) {
            if (!occupied[i]) {
                System.out.print("F");  // free block
            } else {
                Job job = blockToJob.get(i);
                System.out.print(job != null ? job.jobId : "O");  // occupied by job ID
            }
            if ((i + 1) % 10 == 0) System.out.print(" "); // optional spacing per 10 blocks
        }
        System.out.println();
    }
}
