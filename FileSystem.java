import java.util.*;

//
// to force the linked allocation a disk will be initialized with some scattered files in it*/
public class FileSystem {
    private Disk disk;
    private Memory memory;   // reference to memory
    private Map<String, File> files;

    public FileSystem(Disk disk, Memory memory){
        this.disk = disk;
        this.memory = memory;
        this.files = new HashMap<>();

        // reconstructs files from disk logic
        Map<String, List<Integer>> temp = new HashMap<>();
        for (Map.Entry<Integer, String> entry : disk.blockToFile.entrySet()) {
            int block = entry.getKey();
            String fileName = entry.getValue();
            temp.computeIfAbsent(fileName, k -> new ArrayList<>()).add(block);
        }

        for (Map.Entry<String, List<Integer>> entry : temp.entrySet()) {
            String name = entry.getKey();
            List<Integer> blocks = entry.getValue();

            // find starting block (the one not pointed to by any other block)
            Set<Integer> pointed = new HashSet<>(disk.blockChain.values());
            Integer start = null;
            for (Integer b : blocks) {
                if (!pointed.contains(b)) {
                    start = b;
                    break;
                }
            }

            // create File object
            if (start != null) {
                File file = new File(name, blocks.size(), start);
                files.put(name, file);
            }
        }
    }

    // create a file: allocate disk blocks + memory
    public File createFile(String name, int size, long tickCounter){
        // check disk space
        if(!disk.hasFreeBlocks(size)){
            return null;
        }

        // allocate disk blocks (linked allocation)
        List<Integer> blocks = disk.allocateBlocks(size);
        disk.linkBlocks(blocks, name);

        // allocate memory for the file, evict sleeping jobs if needed
        List<Integer> memBlocks = memory.allocateFileWithEviction(size, tickCounter);
        if(memBlocks == null){
            // memory allocation failed even after eviction
            return null;
        }

        // assign memory blocks to file
        memory.assignFileBlocks(name, memBlocks);

        // create file object
        File file = new File(name, size, blocks.get(0));
        files.put(name, file);

        return file;
    }

    // get all blocks of a file (disk linked chain)
    public List<Integer> getFileBlocks(String fileName){
        File file = files.get(fileName);
        if(file == null) return null;

        List<Integer> result = new ArrayList<>();
        Integer current = file.getStartingBlock();

        while(current != null){
            result.add(current);
            current = disk.blockChain.get(current);
        }
        return result;
    }
}