import java.util.*;

public class Memory {

    private int totalSize;
    private boolean[] occupied;
    private Map<Integer, Job> blockToJob;             // tracks which job occupies each frame
    private LinkedHashMap<Job, Long> usageTracker;    // LRU tracking for jobs (last access tick)

    public Memory(int totalSize){
        this.totalSize = totalSize;
        this.occupied = new boolean[totalSize];
        this.blockToJob = new HashMap<>();
        this.usageTracker = new LinkedHashMap<>();
    }

    // ----------------- JOB METHODS -----------------

    // allocate memory for a job; returns true if successful
    public boolean allocate(Job job, long currentTick){
        int size = job.size;
        List<Integer> allocated = new ArrayList<>();

        for (int i = 0; i < totalSize && allocated.size() < size; i++){
            if(!occupied[i]){
                occupied[i] = true;
                allocated.add(i);
                blockToJob.put(i, job);
            }
        }

        if(allocated.size() != size){
            free(allocated);  // rollback
            return false;
        }

        job.allocateFrames(allocated);
        touch(job, currentTick);  // update LRU
        return true;
    }

    // deallocate memory for a job
    public void deallocate(Job job){
        for (int b: job.allocatedFrames){
            occupied[b] = false;
            blockToJob.remove(b);
        }
        job.deallocateFrames();
        usageTracker.remove(job);
    }

    // update LRU timestamp
    public void touch(Job job, long tick){
        if(usageTracker.containsKey(job)){
            usageTracker.put(job, tick);
        }
    }

    // evict the least recently used sleeping job
    public Job evictLRU(){
        for(Map.Entry<Job, Long> entry : usageTracker.entrySet()){
            Job job = entry.getKey();
            if(job.currentState == State.SLEEP){
                deallocate(job);
                return job;
            }
        }
        return null;
    }

    public int getFreeBlocks(){
        int count = 0;
        for (boolean b : occupied) if (!b) count++;
        return count;
    }

    // ----------------- FILE METHODS -----------------

    // simple allocation without eviction
    public List<Integer> allocate(int size){
        List<Integer> allocated = new ArrayList<>();
        for (int i = 0; i < totalSize && allocated.size() < size; i++){
            if(!occupied[i]){
                occupied[i] = true;
                allocated.add(i);
            }
        }
        return allocated.size() == size ? allocated : null;
    }

    // checks if memory has enough free space
    public boolean hasSpace(int size){
        int count = 0;
        for (boolean b : occupied) if(!b) count++;
        return count >= size;
    }

    // free a set of frames (used by files)
    public void free(List<Integer> blocks){
        for(int b : blocks) occupied[b] = false;
    }

    // ----------------- FILE ALLOCATION WITH EVICTION -----------------

    // allocate memory for a file; evict sleeping jobs if needed
    public List<Integer> allocateWithEviction(int size){
        List<Integer> allocated = allocate(size);

        while(allocated == null){
            Job evicted = evictLRU();
            if(evicted == null) return null;  // nothing to evict, allocation fails
            allocated = allocate(size);
        }

        return allocated;
    }
}
