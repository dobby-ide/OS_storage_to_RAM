import java.util.List;
//
// to force th linked allocation a disk will be initialized with some scattered files in it*/
public class FileSystem {
    private Disk disk;

    public FileSystem(Disk disk){
        this.disk = disk;
    }

    public File createFile(String name, int size){
        if(!disk.hasFreeBlocks(size)){
            return null;
        }

        List<Integer> blocks = disk.allocateBlocks(size);
        disk.linkBlocks(blocks);
        return new File(name, size, blocks.get(0));

    }
}
