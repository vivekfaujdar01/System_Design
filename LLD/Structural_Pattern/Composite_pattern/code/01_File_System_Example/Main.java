public class Main {
    public static void main(String[] args) {
        FileSystemComponent file1 = new FileLeaf("resume.pdf", 500_000);
        FileSystemComponent file2 = new FileLeaf("cover_letter.docx", 200_000);
        FileSystemComponent file3 = new FileLeaf("profile_pic.png", 1_500_000);
        FileSystemComponent file4 = new FileLeaf("project_demo.mp4", 15_000_000);

        DirectoryComposite docsDirectory = new DirectoryComposite("My_Documents");
        docsDirectory.addComponent(file1);
        docsDirectory.addComponent(file2);

        DirectoryComposite rootDirectory = new DirectoryComposite("Root_Folder");
        rootDirectory.addComponent(docsDirectory);
        rootDirectory.addComponent(file3);
        rootDirectory.addComponent(file4);

        System.out.println("=== FILE SYSTEM HIERARCHY ===");
        rootDirectory.showDetails();

        System.out.println("\n=== TOTAL CALCULATED SIZE ===");
        System.out.println("Total Root Directory Size: " + rootDirectory.getSize() + " bytes");
        System.out.println("Documents Sub-Directory Size: " + docsDirectory.getSize() + " bytes");
    }
}
