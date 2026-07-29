public class TreeType {
    private final String name;
    private final String color;
    private final String textureData;
    private final String meshModelData;

    public TreeType(String name, String color, String textureData, String meshModelData) {
        this.name = name;
        this.color = color;
        this.textureData = textureData;
        this.meshModelData = meshModelData;
        System.out.println("  [Flyweight Created] New TreeType loaded into memory: " + name + " (" + color + ")");
    }

    public void draw(int x, int y) {
        System.out.println("Drawing '" + name + "' tree at (" + x + ", " + y + ") [Color: " + color + ", Texture: " + textureData + "]");
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getTextureData() {
        return textureData;
    }

    public String getMeshModelData() {
        return meshModelData;
    }
}
