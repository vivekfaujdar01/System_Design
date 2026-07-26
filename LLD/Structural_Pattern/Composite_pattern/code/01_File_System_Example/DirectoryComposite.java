import java.util.ArrayList;
import java.util.List;

public class DirectoryComposite implements FileSystemComponent {
    private final String directoryName;
    private final List<FileSystemComponent> children = new ArrayList<>();

    public DirectoryComposite(String directoryName) {
        this.directoryName = directoryName;
    }

    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public void showDetails() {
        System.out.println("📁 Directory: " + directoryName);
        for (FileSystemComponent child : children) {
            child.showDetails();
        }
    }

    @Override
    public long getSize() {
        long totalSize = 0;
        for (FileSystemComponent child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
}
