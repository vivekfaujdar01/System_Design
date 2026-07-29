public class CharacterStyle {
    private final String fontFamily;
    private final int fontSize;
    private final boolean isBold;
    private final String colorHex;

    public CharacterStyle(String fontFamily, int fontSize, boolean isBold, String colorHex) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.colorHex = colorHex;
        System.out.println("  [Flyweight Created] New CharacterStyle loaded: " + fontFamily + " " + fontSize + "pt " + (isBold ? "Bold" : "Regular") + " [" + colorHex + "]");
    }

    public void render(char symbol, int row, int col) {
        System.out.println("Render '" + symbol + "' at (" + row + "," + col + ") using Style: [" + fontFamily + ", " + fontSize + "pt, " + colorHex + "]");
    }

    public String getFontFamily() { return fontFamily; }
    public int getFontSize() { return fontSize; }
    public boolean isBold() { return isBold; }
    public String getColorHex() { return colorHex; }
}
