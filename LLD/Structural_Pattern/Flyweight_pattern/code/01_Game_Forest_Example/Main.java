public class Main {
    public static void main(String[] args) {
        Forest forest = new Forest();

        System.out.println("=== PLANTING 1,000,000 TREES IN FOREST ===");

        // Plant 500,000 Oak Trees
        for (int i = 0; i < 500000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Oak", "Green", "oak_texture_4k.png", "oak_mesh_3d.obj");
        }

        // Plant 300,000 Pine Trees
        for (int i = 0; i < 300000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Pine", "Dark Green", "pine_texture_4k.png", "pine_mesh_3d.obj");
        }

        // Plant 200,000 Cherry Trees
        for (int i = 0; i < 200000; i++) {
            forest.plantTree(i % 1000, i / 1000, "Cherry", "Pink", "cherry_texture_4k.png", "cherry_mesh_3d.obj");
        }

        System.out.println("\n=== MEMORY SAVINGS SUMMARY ===");
        System.out.println("Total Tree Context Objects Created : " + forest.getTotalTrees());
        System.out.println("Total Flyweight TreeType Objects Shared: " + TreeFactory.getCacheSize());

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println("Total Heap Memory Used            : ~" + usedMemory + " MB");
    }
}
