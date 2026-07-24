public class Main {
    public static void main(String[] args) {
        System.out.println("--- Object Adapter (Composition) ---");
        SocketPlug objectAdapter = new ObjectSocketAdapter(new IndianSocket());
        objectAdapter.provideElectricity();

        System.out.println("\n--- Class Adapter (Inheritance) ---");
        SocketPlug classAdapter = new ClassSocketAdapter();
        classAdapter.provideElectricity();
    }
}
