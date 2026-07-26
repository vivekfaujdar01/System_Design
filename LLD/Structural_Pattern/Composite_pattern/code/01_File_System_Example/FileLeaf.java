public class FileLeaf implements FileSystemComponent {
    private final String fileName;
    private final long sizeInBytes;

    public FileLeaf(String fileName, long sizeInBytes) {
        this.fileName = fileName;
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public void showDetails() {
        System.out.println("  📄 File: " + fileName + " (" + sizeInBytes + " bytes)");
    }

    @Override
    public long getSize() {
        return sizeInBytes;
    }
}
