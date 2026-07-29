public class TextCharacter {
    private final char symbol;
    private final int row;
    private final int col;
    private final CharacterStyle style;

    public TextCharacter(char symbol, int row, int col, CharacterStyle style) {
        this.symbol = symbol;
        this.row = row;
        this.col = col;
        this.style = style;
    }

    public void render() {
        style.render(symbol, row, col);
    }
}
