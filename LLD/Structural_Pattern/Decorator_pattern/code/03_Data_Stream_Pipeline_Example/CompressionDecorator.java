public class CompressionDecorator extends DataSourceDecorator {
    public CompressionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        System.out.println("  [Compression 🗜️] Compressing data string...");
        String compressed = "COMPRESSED(" + data + ")";
        super.writeData(compressed);
    }

    @Override
    public String readData() {
        String compressed = super.readData();
        System.out.println("  [Decompression 🔓] Decompressing data string...");
        return compressed.replace("COMPRESSED(", "").replace(")", "");
    }
}
