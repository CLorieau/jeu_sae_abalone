package abalone.model;

public enum Color {
    BLACK,
    WHITE;
    
    public Color opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}
