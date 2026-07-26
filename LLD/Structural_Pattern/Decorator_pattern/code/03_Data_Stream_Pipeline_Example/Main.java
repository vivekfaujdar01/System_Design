public class Main {
    public static void main(String[] args) {
        String sensitivePayload = "Confidential Banking Secret Key: 9876-ABCD-4321";

        System.out.println("=== 1. PLAIN FILE WRITE ===");
        DataSource plainSource = new FileDataSource();
        plainSource.writeData(sensitivePayload);

        System.out.println("\n=== 2. ENCRYPTED + COMPRESSED PIPELINE ===");
        DataSource secureSource = new EncryptionDecorator(
                                       new CompressionDecorator(
                                           new FileDataSource()
                                       )
                                   );

        System.out.println("--> Executing Pipeline Write:");
        secureSource.writeData(sensitivePayload);

        System.out.println("\n--> Executing Pipeline Read:");
        String recoveredData = secureSource.readData();
        System.out.println("Final Recovered Data: " + recoveredData);
    }
}
