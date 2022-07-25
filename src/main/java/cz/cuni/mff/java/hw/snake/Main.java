package cz.cuni.mff.java.hw.snake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


//Snake Game using JavaFX
public class Main extends Application {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 1200;
    private static int SNAKE_SIZE = 1;
    private static int SNAKE_SPEED = 150;
    private static final int SNAKE_DIRECTION = 0;
    private static int SNAKE_GROW = 1;

    private static Color SNAKE_COLOR = Color.BLUE;
    private static Color FOOD_COLOR = Color.RED;

    private static int amountOfFood = 3;

    private static final int amountOfXYBoxes = 30;
    private static final int blockSize = WIDTH / amountOfXYBoxes;

    private static final int buildBlockSize = WIDTH / 10;

    private static int pointMultiplier = 100;
    private static final Background defaultBackground = new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, null, null));

    //direction
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    static int score = 0;

    static int[][] playingField;

    @Override
    public void start(Stage stage) throws ParserConfigurationException {


        //load the values from the xml file
        loadOptions("options.xml");
        score = 0;

        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        //disable the resize option
        stage.setResizable(false);

        Scene menuScene = createMainMenuScene(stage);

        //startTheGame(gameScene, gridPanel);

        //add pane to stage
        stage.setScene(menuScene);

        stage.setTitle("Snake Game");
        stage.show();
    }

    //get a random number between 0 and max
    private static int getRandomNumber() {
        return (int) (Math.random() * Main.amountOfXYBoxes);
    }

    //this methode returns a random point on the playing field that is not a wall
    private static Point getRandomPoint() {
        int x = getRandomNumber();
        int y = getRandomNumber();
        while (playingField[x][y] == 1) {
            x = getRandomNumber();
            y = getRandomNumber();
        }
        return new Point(x, y);
    }

    //count amount of food on the playing field
    private static int countFood(Food food) {
        int count = 0;
        for (int i = 0; i < Main.amountOfXYBoxes; i++) {
            for (int j = 0; j < Main.amountOfXYBoxes; j++) {
                if (food.isFood(new Point(i, j))) {
                    count++;
                }
            }
        }
        return count;
    }

    //this method get n amount of random numbers
    private static Point[] getRandomPoints(Integer n){
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = getRandomPoint();
        }
        //print Point array
        for (Point point : points) {
            System.out.println(point.getX() + " " + point.getY());
        }

        return points;
    }

    private static void startTheGame(GridPane gridPanel, Stage primaryStage) {

        score = 0;

        Point snakePoint = getRandomPoint();
        //create the snake
        Snake snake = new Snake(snakePoint.getX(), snakePoint.getY(), SNAKE_DIRECTION, SNAKE_SIZE, SNAKE_GROW, SNAKE_COLOR);

        Point[] foodPoints = getRandomPoints(amountOfFood);
        //create the food
        Food food = new Food(FOOD_COLOR);

        food.createNewFood(foodPoints);


        drawPlayingField(gridPanel, snake, food);


        startGameLoop(snake, food, gridPanel, primaryStage);
    }

    private static void startGameLoop(Snake snake, Food food, GridPane gridPanel, Stage primaryStage) {
        //start the game loop
        new Thread((new Runnable() {
            @Override
            public void run() {

                Scene scene = primaryStage.getScene();
                //scene event handler for the key pressed
                scene.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case UP -> snake.setDirection(UP);
                        case DOWN -> snake.setDirection(DOWN);
                        case LEFT -> snake.setDirection(LEFT);
                        case RIGHT -> snake.setDirection(RIGHT);
                    }
                });

                while (gameLoop(snake, food)) {
                    try {
                        Thread.sleep(SNAKE_SPEED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        drawPlayingField(gridPanel, snake, food);
                    });
                }
                Platform.runLater(() -> {
                    //startTheGame(gridPanel.getScene(), gridPanel);
                    changeScene(gameOverScene(primaryStage), primaryStage);

                });
            }
        })).start();

    }

    //game loop
    private static boolean gameLoop(Snake snake, Food food) {
        //check if the snake is dead
        if (snake.isDead(amountOfXYBoxes, amountOfXYBoxes)) {
            //if the snake is dead, show the game over label
            return false;
        } else {
            //if the snake is not dead, check if the snake ate the food
            Point isSnakeEatingFood = snake.isEating(food);
            if (isSnakeEatingFood != null) {
                //if the snake ate the food, create a new food
                food.eaten(isSnakeEatingFood.getX(), isSnakeEatingFood.getY());
                Integer missingFood = amountOfFood - countFood(food);
                food.createNewFood(getRandomPoints(missingFood));
                snake.grow();
                score = score + SNAKE_GROW * pointMultiplier;
            }
        }
        snake.move();
        return true;
    }


    //method to create the playingField (matrix)
    public static int[][] createPlayingField(int width, int height) {
        int[][] playingField = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                playingField[i][j] = 0;
            }
        }
        return playingField;
    }

    //draw the playingField in the pane as a matrix the sing point are blockSize by blockSize pixels
    public static void drawPlayingField(GridPane root, Snake snake, Food food) {
        //clear the pane
        root.getChildren().clear();

        for (int i = 0; i < playingField.length; i++) {
            for (int j = 0; j < playingField[i].length; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(blockSize, blockSize);
                if (snake.isSnake(i, j)) {
                    //set the color of the snake
                    pane.setBackground(snake.getBackgroundColor());
                } else if (food.isFood(new Point(i, j))) {
                    //set the color of the food
                    pane.setBackground(food.getBackgroundColor());
                } else if (playingField[i][j] == 1) {
                    pane.setBackground(defaultBackground);
                    pane.setStyle("-fx-border-color: #2661f8");
                } else {
                    pane.setBackground(defaultBackground);
                    pane.setStyle("-fx-border-color: #1c1c1c");
                }
                //set border color to white
                root.add(pane, i, j);
            }
        }
    }

    public static Scene gameScene(Stage primaryStage) {
        GridPane gridPanel = new GridPane();
        Scene gameScene = new Scene(gridPanel, primaryStage.getMinWidth(), primaryStage.getMinHeight());
        startTheGame(gridPanel, primaryStage);


        return gameScene;
    }

    //this function switches the scene to the options scene
    public static void changeScene(Scene scene, Stage primaryStage) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    //------------------------------Different Scenes---------------------------------


    //this function creates the options' scene
    //the options' scene has the following buttons:
    // - Snake speed (slider) - This changes the sleep interval lover means faster
    // - Snake size (slider) - This changes the snake size
    // - Point multiplier (slider) - This changes the point multiplier
    // - Amount of food (slider) - This changes the amount of food
    // - Snake Grow (slider) - This changes the amount of points the snake grows
    // - Snake Color (button) - This changes the snake color
    // - Food Color (button) - This changes the food color
    // - Save - Saves Everything to an XML file


    public static Scene optionsScene(Stage primaryStage) {
        //create the gridPane
        GridPane gridPanel = new GridPane();

        //create the buttons
        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");

        //create the sliders
        Slider snakeSpeedSlider = new Slider(1, 1000, SNAKE_SPEED);
        Slider snakeSizeSlider = new Slider(1, 10, SNAKE_SIZE);
        Slider pointMultiplierSlider = new Slider(1, 1000, pointMultiplier);
        Slider amountOfFoodSlider = new Slider(1, 10, amountOfFood);
        Slider snakeGrowSlider = new Slider(1, 10, SNAKE_GROW);

        //create the labels
        Label snakeSpeedLabel = new Label("Snake Speed: " + SNAKE_SPEED);
        Label snakeSizeLabel = new Label("Snake Size: " + SNAKE_SIZE);
        Label pointMultiplierLabel = new Label("Point Multiplier: " + pointMultiplier);
        Label amountOfFoodLabel = new Label("Amount of Food: " + amountOfFood);
        Label snakeGrowLabel = new Label("Snake Grow: " + SNAKE_GROW);

        //set the labels
        snakeSpeedLabel.setStyle("-fx-font-size: 20");
        snakeSizeLabel.setStyle("-fx-font-size: 20");
        pointMultiplierLabel.setStyle("-fx-font-size: 20");
        amountOfFoodLabel.setStyle("-fx-font-size: 20");
        snakeGrowLabel.setStyle("-fx-font-size: 20");


        /*snakeColorButton.setTextFill(Color.WHITE);
        snakeColorButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        foodColorButton.setTextFill(Color.WHITE);
        foodColorButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));*/

        saveButton.setTextFill(Color.WHITE);
        saveButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        backButton.setTextFill(Color.WHITE);
        backButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        //change the color of the buttons
        saveButton.setStyle("-fx-background-color: Green");
        backButton.setStyle("-fx-background-color: Green");


        //set the gridPane
        gridPanel.setAlignment(Pos.CENTER);
        gridPanel.setHgap(10);
        gridPanel.setVgap(10);
        gridPanel.setPadding(new Insets(25, 25, 25, 25));

        //add the labels
        gridPanel.add(snakeSpeedLabel, 0, 0);
        gridPanel.add(snakeSizeLabel, 0, 1);
        gridPanel.add(pointMultiplierLabel, 0, 2);
        gridPanel.add(amountOfFoodLabel, 0, 3);
        gridPanel.add(snakeGrowLabel, 0, 4);

        //add the sliders
        gridPanel.add(snakeSpeedSlider, 1, 0);
        gridPanel.add(snakeSizeSlider, 1, 1);
        gridPanel.add(pointMultiplierSlider, 1, 2);
        gridPanel.add(amountOfFoodSlider, 1, 3);
        gridPanel.add(snakeGrowSlider, 1, 4);

        //add the buttons
        gridPanel.add(saveButton, 0, 6);
        gridPanel.add(backButton, 1, 6);

        //set the scene
        Scene optionsScene = new Scene(gridPanel, primaryStage.getMinWidth(), primaryStage.getMinHeight());

        //set the background color
        optionsScene.setFill(Color.BLACK);
        gridPanel.setStyle("-fx-background-color: black");

        //set the font color to white
        snakeSpeedLabel.setTextFill(Color.WHITE);
        snakeSizeLabel.setTextFill(Color.WHITE);
        pointMultiplierLabel.setTextFill(Color.WHITE);
        amountOfFoodLabel.setTextFill(Color.WHITE);
        snakeGrowLabel.setTextFill(Color.WHITE);


        //set the action for the save button
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveOptions(primaryStage);
            }
        });

        //set the action for the back button
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene(createMainMenuScene(primaryStage), primaryStage);
            }
        });

        //set the action for the sliders
        snakeSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SNAKE_SPEED = (int) newValue.doubleValue();
                //updatet the label with the new value
                snakeSpeedLabel.setText("Snake Speed: " + SNAKE_SPEED);
            }
        });

        snakeSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SNAKE_SIZE = (int) newValue.doubleValue();
                //updatet the label with the new value
                snakeSizeLabel.setText("Snake Size: " + SNAKE_SIZE);
            }
        });

        pointMultiplierSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                pointMultiplier = (int) newValue.doubleValue();
                //updatet the label with the new value
                pointMultiplierLabel.setText("Point Multiplier: " + pointMultiplier);
            }
        });

        amountOfFoodSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                amountOfFood = (int) newValue.doubleValue();
                //updatet the label with the new value
                amountOfFoodLabel.setText("Amount of Food: " + amountOfFood);
            }
        });

        snakeGrowSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SNAKE_GROW = (int) newValue.doubleValue();
                //updatet the label with the new value
                snakeGrowLabel.setText("Snake Grow: " + SNAKE_GROW);
            }
        });


        return optionsScene;
    }


    //create main menu scene with 3 buttons (start, options, exit)
    public static Scene createMainMenuScene(Stage primaryStage) {
        //create a new scene
        Scene scene = new Scene(new Group(), blockSize * amountOfXYBoxes, blockSize * amountOfXYBoxes);
        //create a new button
        Button startButton = new Button("Start");
        Button optionsButton = new Button("Options");
        Button mapEditorButton = new Button("Map Editor");
        Button loadMapButton = new Button("Load Map");
        Button exitButton = new Button("Exit");

        Label title = new Label("Snake Game");

        //set scene background color to black
        scene.setFill(Color.BLACK);

        //set the font of the title
        title.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));

        //font color
        title.setTextFill(Color.WHITE);

        title.setPrefSize(WIDTH, buildBlockSize);
        //position the title in the center of the screen
        title.setLayoutX(0);
        title.setLayoutY(buildBlockSize);
        //text alignment center
        title.setAlignment(Pos.CENTER);

        //set the button size
        startButton.setPrefSize(WIDTH, buildBlockSize);
        optionsButton.setPrefSize(WIDTH, buildBlockSize);
        exitButton.setPrefSize(WIDTH, buildBlockSize);
        mapEditorButton.setPrefSize(WIDTH, buildBlockSize);
        loadMapButton.setPrefSize(WIDTH, buildBlockSize);

        //set the button background color
        startButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        optionsButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        exitButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        mapEditorButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        loadMapButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        //set the button text color
        startButton.setTextFill(Color.WHITE);
        optionsButton.setTextFill(Color.WHITE);
        exitButton.setTextFill(Color.WHITE);
        mapEditorButton.setTextFill(Color.WHITE);
        loadMapButton.setTextFill(Color.WHITE);

        //set the button font size
        startButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        optionsButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        exitButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        mapEditorButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        loadMapButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        //set the button text
        startButton.setText("Start");
        optionsButton.setText("Options");
        exitButton.setText("Exit");
        mapEditorButton.setText("Map Editor");
        loadMapButton.setText("Load Map");

        //position the buttons in the scene
        startButton.setLayoutX(0);
        startButton.setLayoutY(WIDTH / 2 - buildBlockSize * 2);
        optionsButton.setLayoutX(0);
        optionsButton.setLayoutY(WIDTH / 2 - buildBlockSize + 10);
        mapEditorButton.setLayoutX(0);
        mapEditorButton.setLayoutY(WIDTH / 2 + 20);
        loadMapButton.setLayoutX(0);
        loadMapButton.setLayoutY(WIDTH / 2 + buildBlockSize + 30);
        exitButton.setLayoutX(0);
        exitButton.setLayoutY(WIDTH / 2 + buildBlockSize * 2 + 40);


        //set the button onAction
        startButton.setOnAction(e -> {
            changeScene(gameScene(primaryStage), primaryStage);
        });
        optionsButton.setOnAction(e -> {
            changeScene(optionsScene(primaryStage), primaryStage);
        });
        mapEditorButton.setOnAction(e -> {
            changeScene(getMapEditorScene(primaryStage), primaryStage);
        });

        loadMapButton.setOnAction(e -> {
            //open a file chooser
            FileChooser fileChooser = new FileChooser();
            //set the title of the file chooser
            fileChooser.setTitle("Open Map");
            //set the extension filter
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
            //get the path of the file
            File file = fileChooser.showOpenDialog(primaryStage);
            //if the file is not null
            if (file != null) {
                try {
                    loadOptions(file.getPath());
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                }finally {
                    //success message alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Map Loader Information");
                    alert.setHeaderText("Map Loaded Successfully");
                    alert.setContentText("You can close this window now");
                    alert.showAndWait();
                }
            }
        });

        exitButton.setOnAction(e -> {
            primaryStage.close();
        });
        //add the buttons to the scene
        ((Group) scene.getRoot()).getChildren().addAll(title, startButton, optionsButton, mapEditorButton, exitButton, loadMapButton);
        return scene;
    }


    //game Over scene w label with the score and restart button
    public static Scene gameOverScene(Stage primaryStage) {
        //create the scene
        Scene gameOverScene = new Scene(new Group(), primaryStage.getMaxWidth(), primaryStage.getMaxHeight());

        //background color of the scene black
        gameOverScene.setFill(Color.BLACK);

        //create the label
        Label gameOverLabel = new Label("Game Over! Your score is: " + score);
        gameOverLabel.setTextFill(Color.WHITE);
        gameOverLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        gameOverLabel.setPrefSize(WIDTH, HEIGHT);
        gameOverLabel.setLayoutY(HEIGHT / 3);
        //text align center
        gameOverLabel.setAlignment(Pos.CENTER);

        //create the button
        Button restartButton = new Button("Restart");
        restartButton.setPrefSize(WIDTH, HEIGHT / 10);
        restartButton.setLayoutY(HEIGHT / 2 - 10);
        restartButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        restartButton.setTextFill(Color.WHITE);
        restartButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        //set the button onAction
        restartButton.setOnAction(e -> {
            changeScene(gameScene(primaryStage), primaryStage);
        });

        // add options button
        Button optionsButton = new Button("Options");
        optionsButton.setPrefSize(WIDTH, HEIGHT / 10);
        optionsButton.setLayoutY(HEIGHT / 2 + HEIGHT / 10);
        optionsButton.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        optionsButton.setTextFill(Color.WHITE);
        optionsButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        //set the button onAction
        optionsButton.setOnAction(e -> {
            changeScene(optionsScene(primaryStage), primaryStage);
        });

        // add Main menu button
        Button mainMenu = new Button("Main Menu");
        mainMenu.setPrefSize(WIDTH, HEIGHT / 10);
        mainMenu.setLayoutY(HEIGHT / 2 + HEIGHT / 10 + HEIGHT / 10 + 10);
        mainMenu.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        mainMenu.setTextFill(Color.WHITE);
        mainMenu.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        //set the button onAction
        mainMenu.setOnAction(e -> {
            changeScene(createMainMenuScene(primaryStage), primaryStage);
        });


        //add the label and button to the scene
        ((Group) gameOverScene.getRoot()).getChildren().addAll(gameOverLabel, restartButton, optionsButton, mainMenu);
        return gameOverScene;
    }

    //generate an xml file with all the current configurations
    //Snake speed (slider) - This changes the sleep interval lover means faster
    // - Snake size (slider) - This changes the snake size
    // - Point multiplier (slider) - This changes the point multiplier
    // - Amount of food (slider) - This changes the amount of food
    // - Snake Grow (slider) - This changes the amount of points the snake grows
    // - Snake Color (button) - This changes the snake color
    // - Food Color (button) - This changes the food color

    public static void saveOptions(Stage s) {
        //check if playingField is null
        if (playingField == null) {

            playingField = createPlayingField(amountOfXYBoxes, amountOfXYBoxes);

        } else if (playingField.length != amountOfXYBoxes) {
            playingField = createPlayingField(amountOfXYBoxes, amountOfXYBoxes);
        }

        //create the file
        File file = new File("options.xml");
        try {
            //create the document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            //create the root element
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("options");
            document.appendChild(root);

            //create the elements
            Element snakeSpeed = document.createElement("snakeSpeed");
            Element snakeSize = document.createElement("snakeSize");
            Element pointMultiplier = document.createElement("pointMultiplier");
            Element foodAmount = document.createElement("foodAmount");
            Element snakeGrow = document.createElement("snakeGrow");
            Element snakeColor = document.createElement("snakeColor");
            Element foodColor = document.createElement("foodColor");

            Element map = document.createElement("map");

            //set the attributes
            snakeSpeed.setAttribute("value", String.valueOf(SNAKE_SPEED));
            snakeSize.setAttribute("value", String.valueOf(SNAKE_SIZE));
            pointMultiplier.setAttribute("value", String.valueOf(100));
            foodAmount.setAttribute("value", String.valueOf(amountOfFood));
            snakeGrow.setAttribute("value", String.valueOf(SNAKE_GROW));
            snakeColor.setAttribute("value", String.valueOf(SNAKE_COLOR));
            foodColor.setAttribute("value", String.valueOf(FOOD_COLOR));


            //convert the int[][] to xml
            for (int[] ints : playingField) {
                Element row = document.createElement("row");
                Element cell = null;
                for (int anInt : ints) {
                    cell = document.createElement("cell");
                    cell.setAttribute("value", String.valueOf(anInt));
                    row.appendChild(cell);
                }
                map.appendChild(row);
            }

            //add the elements to the root
            root.appendChild(snakeSpeed);
            root.appendChild(snakeSize);
            root.appendChild(pointMultiplier);
            root.appendChild(foodAmount);
            root.appendChild(snakeGrow);
            root.appendChild(snakeColor);
            root.appendChild(foodColor);
            root.appendChild(map);


            //write the document to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            //createNotification("The options have been saved", s);
            //success message alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Options");
            alert.setHeaderText("Options Successfully Saved");
            alert.setContentText("You can close this window now");
            alert.showAndWait();

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void loadOptions(String path) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        try {
            Document document = documentBuilder.parse(path);
            Element root = document.getDocumentElement();

            //get the elements
            Element snakeSpeed = (Element) root.getElementsByTagName("snakeSpeed").item(0);
            Element snakeSize = (Element) root.getElementsByTagName("snakeSize").item(0);
            Element pointMultiplier2 = (Element) root.getElementsByTagName("pointMultiplier").item(0);
            Element foodAmount = (Element) root.getElementsByTagName("foodAmount").item(0);
            Element snakeGrow = (Element) root.getElementsByTagName("snakeGrow").item(0);
            Element snakeColor = (Element) root.getElementsByTagName("snakeColor").item(0);
            Element foodColor = (Element) root.getElementsByTagName("foodColor").item(0);


            //get the values
            SNAKE_SPEED = Integer.parseInt(snakeSpeed.getAttribute("value"));
            SNAKE_SIZE = Integer.parseInt(snakeSize.getAttribute("value"));
            pointMultiplier = Integer.parseInt(pointMultiplier2.getAttribute("value"));
            amountOfFood = Integer.parseInt(foodAmount.getAttribute("value"));
            SNAKE_GROW = Integer.parseInt(snakeGrow.getAttribute("value"));
            SNAKE_COLOR = Color.valueOf(snakeColor.getAttribute("value"));
            FOOD_COLOR = Color.valueOf(foodColor.getAttribute("value"));

            //get the map
            Element map = (Element) root.getElementsByTagName("map").item(0);
            NodeList rows = map.getElementsByTagName("row");
            int[][] playingField2 = new int[rows.getLength()][];

            if (rows.getLength() != amountOfXYBoxes) {
                playingField2 = createPlayingField(amountOfXYBoxes, amountOfXYBoxes);
            } else {
                for (int i = 0; i < rows.getLength(); i++) {
                    Element row = (Element) rows.item(i);
                    NodeList cells = row.getElementsByTagName("cell");
                    playingField2[i] = new int[cells.getLength()];
                    for (int j = 0; j < cells.getLength(); j++) {
                        Element cell = (Element) cells.item(j);
                        playingField2[i][j] = Integer.parseInt(cell.getAttribute("value"));
                    }
                }
            }
            playingField = playingField2;

        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    //scene for the MapEditor
    public static Scene getMapEditorScene(Stage primaryStage) {
        GridPane gridPanel = new GridPane();
        Scene gameScene = new Scene(new Group(), primaryStage.getMaxWidth(), primaryStage.getMinWidth());

        //set the background of the scene
        gameScene.setFill(Color.BLACK);

        double blockSizeH = (blockSize / 1.5);

        //center the grid
        gridPanel.setLayoutY(0);
        gridPanel.setLayoutX((WIDTH - (playingField.length * blockSizeH)) / 2);

        //create one button field in playingField
        for (int i = 0; i < playingField.length; i++) {
            for (int j = 0; j < playingField[i].length; j++) {
                Button button = new Button();
                button.setPrefSize(blockSizeH, blockSizeH);

                if (playingField[j][i] == 1) {
                    button.setStyle("-fx-background-color: #135ce5");
                }

                //action for the button to change the value of the field to 1
                int finalI = j;
                int finalJ = i;
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (playingField[finalI][finalJ] == 0) {
                            playingField[finalI][finalJ] = 1;
                            button.setStyle("-fx-background-color: #135ce5");
                        } else {
                            playingField[finalI][finalJ] = 0;
                            button.setStyle("-fx-background-color: #ffffff");
                        }
                    }
                });

                gridPanel.add(button, j, i);
            }
        }

        //create the save button
        Button saveButton = new Button("Save");
        saveButton.setPrefSize(WIDTH, buildBlockSize);

        //position the save button
        saveButton.setLayoutY(HEIGHT / 2 + buildBlockSize * 2);
        saveButton.setLayoutX(0);

        saveButton.setStyle("-fx-background-color: GREEN");
        saveButton.setTextFill(Color.WHITE);
        saveButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveOptions(primaryStage);
            }
        });

        //create the back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(WIDTH, buildBlockSize);

        //position the buttons
        backButton.setLayoutY(HEIGHT / 2 + buildBlockSize * 3);
        backButton.setLayoutX(0);

        //change button color
        backButton.setStyle("-fx-background-color: GREEN");
        backButton.setTextFill(Color.WHITE);
        backButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));


        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeScene(createMainMenuScene(primaryStage), primaryStage);
            }
        });

        //create the reset button
        Button resetButton = new Button("Reset");
        resetButton.setPrefSize(WIDTH, buildBlockSize);

        //position the buttons
        resetButton.setLayoutY(HEIGHT / 2 + buildBlockSize * 4);
        resetButton.setLayoutX(0);

        //change button color
        resetButton.setStyle("-fx-background-color: GREEN");
        resetButton.setTextFill(Color.WHITE);
        resetButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //set all fields to 0
                for (int i = 0; i < playingField.length; i++) {
                    for (int j = 0; j < playingField[i].length; j++) {
                        playingField[j][i] = 0;
                        //update the button color
                        Button button = (Button) gridPanel.getChildren().get(i * playingField.length + j);
                        button.setStyle("-fx-background-color: #ffffff");
                    }
                }
            }
        });

        ((Group) gameScene.getRoot()).getChildren().addAll(resetButton, gridPanel, saveButton, backButton);
        return gameScene;
    }

    //This function is used to create notification that can be called in any stage and display a specific message
    public static void createNotification(String message, Stage primaryStage) {
        //create a LABLE to display the message
        Label label = new Label(message);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);

        //create a VBOX to hold the label
        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        vBox.setAlignment(Pos.CENTER);
        //set green background
        vBox.setStyle("-fx-background-color: Black");

        //button to close the stage
        Button closeButton = new Button("Close");
        closeButton.setPrefSize(WIDTH, buildBlockSize);

        //position the button
        closeButton.setLayoutY(HEIGHT / 2 + buildBlockSize * 6 + 20);
        closeButton.setLayoutX(0);

        //change button color
        closeButton.setStyle("-fx-background-color: GREEN");
        closeButton.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        closeButton.setTextFill(Color.WHITE);


        //create a scene to hold the vbox
        Scene notificationScene = new Scene(vBox, WIDTH, HEIGHT);
        vBox.getChildren().add(closeButton);

        //create a stage to hold the scene
        Stage notificationStage = new Stage();
        notificationStage.setScene(notificationScene);
        notificationStage.setTitle("Notification");
        notificationStage.initModality(Modality.APPLICATION_MODAL);
        notificationStage.show();

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                notificationStage.close();
            }
        });
    }
}


