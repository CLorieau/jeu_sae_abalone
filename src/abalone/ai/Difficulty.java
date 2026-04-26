package abalone.ai;

public enum Difficulty {
    EASY("Facile", 1),
    MEDIUM("Moyen", 2),
    HARD("Difficile", 3);

    private final String label;
    private final int depth;

    Difficulty(String label, int depth) {
        this.label = label;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
