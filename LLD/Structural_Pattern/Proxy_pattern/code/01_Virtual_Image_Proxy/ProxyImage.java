public class ProxyImage implements Image {
    private final String fileName;
    private RealImage realImage;

    public ProxyImage(String fileName) {
        this.fileName = fileName;
        // Notice: RealImage is NOT initialized here! Heavy disk load is postponed.
        System.out.println("[ProxyImage] Created proxy instance for: " + fileName + " (Zero disk I/O cost)");
    }

    @Override
    public void display() {
        if (realImage == null) {
            System.out.println("[ProxyImage] First display request received. Triggering lazy initialization...");
            realImage = new RealImage(fileName);
        } else {
            System.out.println("[ProxyImage] Reusing cached RealImage instance for: " + fileName);
        }
        realImage.display();
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
