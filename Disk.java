

import java.util.*;

// stores block information
//
// */
public class Disk {
    public Set<Integer> freeBlocks;
    private int totalBlocks;
    public Map<Integer, Integer> blockChain;
    public Map<Integer, String> blockToFile;

    public Disk(int totalBlocks, Map<String, List<Integer>> prefilledFiles) {

        this.totalBlocks = totalBlocks;
        this.freeBlocks = new HashSet<>();
        for (int i = 0; i < totalBlocks; i++){
            freeBlocks.add(i);
        }
        this.blockChain = new HashMap<>();
        this.blockToFile = new HashMap<>();

        //pre-fill simulate fragmentation;
        if(prefilledFiles != null){
            for (String fileName : prefilledFiles.keySet()){
                List<Integer> blocks = prefilledFiles.get(fileName);

                for (int i = 0; i <blocks.size(); i++){
                    int block = blocks.get(i);

                    freeBlocks.remove(block);

                    blockChain.put(block, i < blocks.size() - 1 ? blocks.get(i + 1) : null);

                    blockToFile.put(block, fileName);

                }
            }
        }
    }

    public void linkBlocks(List<Integer> blocks, String fileName){
        if(blocks == null || blocks.isEmpty()) return;
        // creates the chain to disk


        for (int i = 0; i < blocks.size(); i++){

            int currentBlock = blocks.get(i);

            Integer nextBlock = (i < blocks.size() -1) ? blocks.get(i + 1) : null;
            blockChain.put(currentBlock, nextBlock);
            blockToFile.put(currentBlock, fileName);
        }



    }
    public List<Integer> allocateBlocks(int numBlocks){
        if(freeBlocks.size() < numBlocks){
            return null;
        }

        // Convert free blocks to list and allows random selection
        List<Integer> available = new ArrayList<>(freeBlocks);
        Collections.shuffle(available);

        List<Integer> allocated = new ArrayList<>();

        for (int i = 0; i < numBlocks; i++){
            int block = available.get(i);
            allocated.add(block);
            freeBlocks.remove(block);
        }

        return allocated;
    }
    public boolean hasFreeBlocks(int sizeOfFile){
        return freeBlocks.size() >= sizeOfFile;
    }
}
