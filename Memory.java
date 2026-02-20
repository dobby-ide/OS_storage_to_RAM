import java.util.ArrayList;
import java.util.List;

public class Memory {

    private int totalSize;
    private boolean[] occupied;

    public Memory(int totalSize){
        this.totalSize = totalSize;
        this.occupied = new boolean[totalSize];
    }

    // checks if memory has space for a file
    public boolean hasSpace(int size){
        int count = 0;
        for (boolean b : occupied ){
            if(!b) count++;
        }
        return count >= size;
    }

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

    public void free(List<Integer> blocks){
        for (int b : blocks) occupied[b] = false;
    }
}
