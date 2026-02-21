import java.util.*;

public class Main {

    public static void main(String[] args) {

        Memory memory = new Memory(8);

        //  Disk: total 20 blocks, some prefilled files
        Map<String, List<Integer>> prefilledFiles = new HashMap<>();
        prefilledFiles.put("FileA", Arrays.asList(3, 6, 9, 12, 15));
        prefilledFiles.put("FileB", Arrays.asList(2, 4, 7));
        Disk disk = new Disk(20, prefilledFiles);

        // FileSystem
        FileSystem fs = new FileSystem(disk, memory);

        //  JobScheduler
        JobScheduler scheduler = new JobScheduler(memory);

        // Add jobs
        scheduler.addJob(new Job(1, 1, 3, 2, State.SLEEP));
        scheduler.addJob(new Job(2, 1, 2, 3, State.SLEEP));
        scheduler.addJob(new Job(3, 3, 4, 2, State.END));

        // ------------------ SIMULATION ------------------
        int totalTicks = 10;

        for (int i = 0; i < totalTicks; i++) {

            // advance one tick
            scheduler.tick();

            // Load a file at tick 2
            if (scheduler.getTickCounter() == 2L) {
                File fileX = fs.createFile("FileX", 3);
                if (fileX != null) {
                    System.out.println("FileX loaded into memory at tick " + scheduler.getTickCounter());
                } else {
                    System.out.println("FileX could not be loaded (memory full)");
                }
            }

            // Print status
            System.out.println("Tick: " + scheduler.getTickCounter());
            System.out.println("Running jobs: " + scheduler.getRunningJobIds());
            System.out.println("Sleeping jobs: " + scheduler.getSleepingJobIds());
            System.out.println("Finished jobs: " + scheduler.getFinishedJobIds());
            System.out.println("Free memory blocks: " + memory.getFreeBlocks());
            System.out.println("-----------");
        }

//        Map<String, List<Integer>> prefilledFiles = new HashMap<>();
//        prefilledFiles.put("FileA", Arrays.asList(3, 6, 9, 12, 15));
//        prefilledFiles.put("FileB", Arrays.asList(2, 4, 7));
//
//        Disk disk = new Disk(20, prefilledFiles);
//
//
//        List<Integer> newFileBlocks = disk.allocateBlocks(4);
//        System.out.println("Allocated blocks for new file: " + newFileBlocks);
//
//        FileSystem fs = new FileSystem(disk);
//        fs.createFile("FileC",4);
//        System.out.println("filesystem get File blocks " + fs.getFileBlocks("FileC"));
//
//      disk.linkBlocks(newFileBlocks, "FileC");
////
////
////
////
//       System.out.println("Block chain:");
//       for (int block : disk.blockChain.keySet()) {
//            System.out.println(block + " -> " + disk.blockChain.get(block));
//       }
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
