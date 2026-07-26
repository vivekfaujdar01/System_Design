import java.util.Base64;

public class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        System.out.println("  [Encryption 🔒] Encrypting plain data using Base64...");
        String encrypted = Base64.getEncoder().encodeToString(data.getBytes());
        super.writeData(encrypted);
    }

    @Override
    public String readData() {
        String encrypted = super.readData();
        System.out.println("  [Decryption 🔓] Decrypting Base64 data back to plain text...");
        return new String(Base64.getDecoder().decode(encrypted));
    }
}
