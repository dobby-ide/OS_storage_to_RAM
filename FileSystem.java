import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    // create a file: allocate disk blocks + memory
    public File createFile(String name, int size){
        // check disk space
        if(!disk.hasFreeBlocks(size)){
            return null;
        }

        // allocate disk blocks (linked allocation)
        List<Integer> blocks = disk.allocateBlocks(size);
        disk.linkBlocks(blocks, name);

        // allocate memory for the file, evict sleeping jobs if needed
        List<Integer> memBlocks = memory.allocateWithEviction(size);
        if(memBlocks == null){
            // memory allocation failed even after eviction
            // optionally, roll back disk allocation
            return null;
        }

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