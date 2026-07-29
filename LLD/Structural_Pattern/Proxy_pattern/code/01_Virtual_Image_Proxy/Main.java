public class Main {
    public static void main(String[] args) {
        System.out.println("=== VIRTUAL PROXY LAZY LOADING DEMO ===");

        System.out.println("\n1. Initializing Image Gallery with 3 High-Res Images:");
        Image img1 = new ProxyImage("nature_8k.png");
        Image img2 = new ProxyImage("space_galaxy_8k.png");
        Image img3 = new ProxyImage("city_night_8k.png");

        System.out.println("\n2. Displaying Image 1 for the FIRST time (Triggers Heavy Disk Load):");
        img1.display();

        System.out.println("\n3. Displaying Image 1 for the SECOND time (Uses Cache):");
        img1.display();

        System.out.println("\n4. Displaying Image 2 (Triggers Heavy Disk Load):");
        img2.display();

        System.out.println("\n5. Note: Image 3 (" + img3.getFileName() + ") was NEVER displayed, so its 50MB disk load was completely avoided!");
    }
}
