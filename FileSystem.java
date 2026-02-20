import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
// to force the linked allocation a disk will be initialized with some scattered files in it*/
public class FileSystem {
    private Disk disk;
    private Map<String, File> files;

    public FileSystem(Disk disk){
        this.disk = disk;
        this.files = new HashMap<>();
    }

    public File createFile(String name, int size){
        if(!disk.hasFreeBlocks(size)){
            return null;
        }

        List<Integer> blocks = disk.allocateBlocks(size);
        disk.linkBlocks(blocks, name);

        File file = new File(name, size,blocks.get(0));
        files.put(name,file);
        return file;

    }

    // given a file name, what are its meta-data and blocks.
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
