import java.util.HashMap;
import java.util.Map;

public class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String textureData, String meshModelData) {
        String key = name + "_" + color;
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, textureData, meshModelData));
        }
        return treeTypes.get(key);
    }

    public static int getCacheSize() {
        return treeTypes.size();
    }
}
