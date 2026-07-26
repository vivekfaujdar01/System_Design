public class FileDataSource implements DataSource {
    private String storedData = "";

    @Override
    public void writeData(String data) {
        this.storedData = data;
        System.out.println("  [Disk Write 💾] Writing raw bytes to file storage: " + storedData);
    }

    @Override
    public String readData() {
        System.out.println("  [Disk Read 📖] Reading raw bytes from file storage.");
        return storedData;
    }
}
