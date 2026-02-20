import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {


        Map<String, List<Integer>> prefilledFiles = new HashMap<>();
        prefilledFiles.put("FileA", Arrays.asList(3, 6, 9, 12, 15));
        prefilledFiles.put("FileB", Arrays.asList(2, 4, 7));

        Disk disk = new Disk(20, prefilledFiles);


        List<Integer> newFileBlocks = disk.allocateBlocks(4);
        System.out.println("Allocated blocks for new file: " + newFileBlocks);

        FileSystem fs = new FileSystem(disk);
        fs.createFile("FileC",4);
        System.out.println("filesystem get File blocks " + fs.getFileBlocks("FileC"));

      disk.linkBlocks(newFileBlocks, "FileC");
//
//
//
//
       System.out.println("Block chain:");
       for (int block : disk.blockChain.keySet()) {
            System.out.println(block + " -> " + disk.blockChain.get(block));
       }
//
//        // Check block ownership
//        System.out.println("Block to file mapping:");
//        for (int block : disk.blockToFile.keySet()) {
//            System.out.println(block + " belongs to " + disk.blockToFile.get(block));
//        }
//
//        //  Remaining free blocks
//        System.out.println("Remaining free blocks: " + disk.freeBlocks);



    }
}
