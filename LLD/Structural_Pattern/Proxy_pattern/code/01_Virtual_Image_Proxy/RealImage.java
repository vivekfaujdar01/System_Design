public class RealImage implements Image {
    private final String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("[RealImage] Loading high-resolution image file from disk: " + fileName + " (Simulating 50MB disk load)...");
        try {
            Thread.sleep(500); // Simulate heavy I/O delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[RealImage] Finished loading: " + fileName);
    }

    @Override
    public void display() {
        System.out.println("[RealImage] Displaying image on screen: " + fileName);
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
