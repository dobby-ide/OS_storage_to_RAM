import java.util.ArrayList;
import java.util.List;

public class Job {
    int jobId;
    int startTime;
    int duration;
    int remainingTime;
    int size;

    State currentState;   // NEW, RUNNING, SLEEP, END
    State finalState;     // SLEEP or END (what happens when interval finishes)

    List<Integer> allocatedFrames;

    public Job(int jobId, int startTime, int duration, int size, State finalState) {
        this.jobId = jobId;
        this.startTime = startTime;
        this.duration = duration;
        this.remainingTime = duration;
        this.size = size;
        this.currentState = State.NEW;
        this.finalState = finalState;
        this.allocatedFrames = new ArrayList<>();
    }

    // Memory methods
    public void allocateFrames(List<Integer> frames) {
        allocatedFrames.clear();
        allocatedFrames.addAll(frames);
    }

    public void deallocateFrames() {
        allocatedFrames.clear();
    }

    public void tick() {
        if (currentState == State.RUNNING) {
            remainingTime--;
        }
    }

    public boolean isFinished() {
        return remainingTime <= 0;
    }

}
