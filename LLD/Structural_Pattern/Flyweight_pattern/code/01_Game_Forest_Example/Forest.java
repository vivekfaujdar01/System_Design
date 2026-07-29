import java.util.ArrayList;
import java.util.List;

public class Forest {
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String textureData, String meshModelData) {
        TreeType type = TreeFactory.getTreeType(name, color, textureData, meshModelData);
        Tree tree = new Tree(x, y, type);
        trees.add(tree);
    }

    public void drawForest() {
        for (Tree tree : trees) {
            tree.draw();
        }
    }

    public int getTotalTrees() {
        return trees.size();
    }
}
