import java.util.*;

public class Memory {

    private int totalSize;
    private boolean[] occupied;
    private Map<Integer, Job> blockToJob;
    private LinkedHashMap<Job, Long> usageTracker; // helper field that tracks the last time a job has been used in memory

    public Memory(int totalSize){
        this.totalSize = totalSize;
        this.occupied = new boolean[totalSize];
        this.blockToJob = new HashMap<>();
        this.usageTracker = new LinkedHashMap<>();
    }


    //
    // Methods for jobs*/

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
            // not enough memory for the job
            free(allocated);
            return false;
        }

        job.allocateFrames(allocated);
        usageTracker.put(job, currentTick);
        return true;
    }

    public void deallocate(Job job){
        for (int b: job.allocatedFrames){
            occupied[b] = false;
            blockToJob.remove(b);
        }
        job.deallocateFrames();
        usageTracker.remove(job);
    }

    //evict least recently used sleeping job
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

    //update LRU on access
    public void touch(Job job, long tick){
        if(usageTracker.containsKey(job)){
            usageTracker.put(job, tick);
        }
    }

    public int getFreeBlocks(){
        int count = 0;
        for (boolean b: occupied) if (!b) count++;
        return count;
    }


    // METHODS FOR FILES

    public List<Integer> allocate (int size) {
        List<Integer> allocated = new ArrayList<>();
        for (int i = 0; i < totalSize && allocated.size() < size; i++){
            if(!occupied[i]){
                occupied[i] = true;
                allocated.add(i);
            }
        }
        return allocated.size() == size ? allocated : null;
    }

    // checks if memory has space for a file
    public boolean hasSpace(int size){
        int count = 0;
        for (boolean b : occupied ){
            if(!b) count++;
        }
        return count >= size;
    }

    public void free(List<Integer> blocks){
        for (int b : blocks) occupied[b] = false;
    }


}
