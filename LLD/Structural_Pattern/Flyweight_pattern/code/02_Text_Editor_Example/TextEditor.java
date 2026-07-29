import java.util.ArrayList;
import java.util.List;

public class TextEditor {
    private final List<TextCharacter> characters = new ArrayList<>();

    public void insertCharacter(char symbol, int row, int col, String font, int size, boolean isBold, String color) {
        CharacterStyle style = StyleFactory.getStyle(font, size, isBold, color);
        TextCharacter character = new TextCharacter(symbol, row, col, style);
        characters.add(character);
    }

    public void renderDocument() {
        for (TextCharacter character : characters) {
            character.render();
        }
    }

    public int getTotalCharacters() {
        return characters.size();
    }
}
