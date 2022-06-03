package cz.cuni.mff.java.hw.snake;

import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.util.ArrayList;

public class Food extends Node {
    ArrayList<Point> foodLocation = new ArrayList<>();

    public Food(int x, int y) {
        foodLocation.add(new Point(x, y));
    }

    //check if is food
    public boolean isFood(Point point) {
        for (Point p : foodLocation) {
            if (p.equals(point)) {
                return true;
            }
        }
        return false;
    }

    public Background getBackgroundColor() {
        return new Background(new BackgroundFill(javafx.scene.paint.Color.RED, null, null));
    }

    //food has been eaten
    public void eaten(int x, int y) {
        //remove food from list
        for (Point p : foodLocation) {
            if (p.equals(new Point(x, y))) {
                foodLocation.remove(p);
                break;
            }
        }
    }

    public void createNewFood(Point point) {
        foodLocation.add(point);
    }

    public ArrayList<Point> getFoodLocation() {
        return foodLocation;
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
