public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();

        System.out.println("=== TYPING DOCUMENT IN TEXT EDITOR ===");

        String text = "Flyweight Pattern in Java!";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // First 9 chars: Arial 14pt Bold Blue
            if (i < 9) {
                editor.insertCharacter(c, 1, i, "Arial", 14, true, "#0000FF");
            } else {
                // Remaining chars: Times New Roman 12pt Regular Black
                editor.insertCharacter(c, 1, i, "Times New Roman", 12, false, "#000000");
            }
        }

        System.out.println("\n=== RENDERING DOCUMENT ===");
        editor.renderDocument();

        System.out.println("\n=== MEMORY SAVINGS SUMMARY ===");
        System.out.println("Total Characters Rendered      : " + editor.getTotalCharacters());
        System.out.println("Total Flyweight Styles Created  : " + StyleFactory.getCacheSize());
    }
}
