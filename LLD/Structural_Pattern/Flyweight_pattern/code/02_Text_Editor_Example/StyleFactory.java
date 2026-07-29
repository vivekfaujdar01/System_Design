import java.util.HashMap;
import java.util.Map;

public class StyleFactory {
    private static final Map<String, CharacterStyle> styles = new HashMap<>();

    public static CharacterStyle getStyle(String fontFamily, int fontSize, boolean isBold, String colorHex) {
        String key = fontFamily + "_" + fontSize + "_" + isBold + "_" + colorHex;
        if (!styles.containsKey(key)) {
            styles.put(key, new CharacterStyle(fontFamily, fontSize, isBold, colorHex));
        }
        return styles.get(key);
    }

    public static int getCacheSize() {
        return styles.size();
    }
}
