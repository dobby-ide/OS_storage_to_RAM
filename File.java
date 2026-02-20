//
// simple POJO that contains a file metadata
// startingBlock is also initialized here for a cleaner design
// *
//  the file is meant to be not contiguous in memory via linked allocation/
public class File {
    private String name;
    private int size;
    private final int startingBlock;

    public File(String name, int size, int startingBlock) {
        this.name = name;
        this.size = size;
        this.startingBlock = startingBlock;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getStartingBlock() {
        return startingBlock;
    }
}
