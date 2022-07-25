package cz.cuni.mff.java.hw.snake;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Snake extends Node {
    private int direction;
    private int nextDirection;
    private int length;
    private Color snakeColor = Color.GREEN;
    private final int growStep;
    //arraylist of points of snake
    ArrayList<Point> snakeLocation;



    //get Snake head point
    public Point getHead() {
        return snakeLocation.get(0);
    }

    public Snake(int x, int y, int direction, int length, int growStep, Color c) {
        snakeLocation = new ArrayList<>();
        this.direction = direction;
        snakeLocation.add(new Point(x, y));
        this.length = length;
        this.growStep = growStep;
        this.snakeColor = c;
    }

    //this function checks a point of coordinates is a part of snake
    public boolean isSnake(int x, int y) {
        for (Point p : snakeLocation) {
            if (p.getX() == x && p.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(int direction) {
       //check if new direction is not opposite to current
        if (this.direction != (direction + 2) % 4) {
            this.nextDirection = direction;
        }
    }

    //this function moves the entire snake
    public void move() {
        this.direction = this.nextDirection;

        Point head = snakeLocation.get(0);
        Point newHead = new Point(head.getX(), head.getY());
        switch (direction) {
            case 0 -> newHead.setY(head.getY() - 1);
            case 1 -> newHead.setX(head.getX() + 1);
            case 2 -> newHead.setY(head.getY() + 1);
            case 3 -> newHead.setX(head.getX() - 1);
        }
        snakeLocation.add(0, newHead);
        if (snakeLocation.size() > length) {
            snakeLocation.remove(snakeLocation.size() - 1);
        }
    }

    //this function makes snake grow
    public void grow() {
        length = length + growStep;
    }

    //is snake overlapping with itself
    public boolean isOverlapping() {
        for (int i = 1; i < snakeLocation.size(); i++) {
            if (snakeLocation.get(0).getX() == snakeLocation.get(i).getX() &&
                    snakeLocation.get(0).getY() == snakeLocation.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSnakeOutOdMap(int maxWidth, int maxHeight) {
    	return getHead().getX() < 0 || getHead().getX() >= maxWidth || getHead().getY() < 0 || getHead().getY() >= maxHeight;
    }


    public boolean isDead(int maxWidth, int maxHeight) {
        return  isSnakeOutOdMap(maxWidth, maxHeight) || isOverlapping() || isSnakeInWall(Main.playingField);
    }

    public boolean isSnakeInWall(int[][] playingField){
        //wall is represented by 1
        return playingField[getHead().getX()][getHead().getY()] == 1;
    }


    //check if any point of snake is on food point
    public Point isEating(Food food) {
        for (Point p : snakeLocation) {
           for (Point p1 : food.getFoodLocation()) {
               if (p.getX() == p1.getX() && p.getY() == p1.getY()) {
                   return p;
               }
           }
        }
        return null;
    }

    public Background getBackgroundColor() {
        return new Background(new BackgroundFill(snakeColor, null, null));
    }



    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }

    public Node getGameOverLabel() {
        return super.getStyleableNode();
    }

}
