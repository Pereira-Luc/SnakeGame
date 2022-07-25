package cz.cuni.mff.java.hw.snake;

import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class Food extends Node {
    ArrayList<Point> foodLocation = new ArrayList<>();
    private Color foodColor = Color.GREEN;

    public Food(Color c) {
        this.foodColor = c;
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
        return new Background(new BackgroundFill(foodColor, null, null));
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

    public void createNewFood(Point[] point) {
        foodLocation.addAll(Arrays.asList(point).subList(0, point.length));
    }

    public ArrayList<Point> getFoodLocation() {
        return foodLocation;
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
