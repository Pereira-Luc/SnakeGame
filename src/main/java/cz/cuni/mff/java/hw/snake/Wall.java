package cz.cuni.mff.java.hw.snake;

import javafx.scene.Node;

public class Wall extends Node {
    private final int x;
    private final int y;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}

