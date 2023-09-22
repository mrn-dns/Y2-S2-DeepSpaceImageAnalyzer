package controllers;

import com.example.assignment1.HelloApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import java.util.Random;

import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    public static MainController mainController;
    public Image selectedImage;
    public WritableImage mainWritableImage;
    public final FileChooser fileChooser = new FileChooser();
    public ImageView mainImageContainer = new ImageView();
    public ImageView mainWritableImageContainer = new ImageView();
    public int arraySize;
    public int[] BWArray;
    public LinkedList<Integer> orderedRootsBySize = new LinkedList<>();
    public HashMap<Integer,Integer> setSizes = new HashMap<>();
    public ArrayList<Map.Entry<Integer,Integer>> sortedBySizeList;
    Set<Integer> set = new HashSet<>();
    public Set<Integer> setOfRoots;
    public Slider brightnessSlider,sizeSlider;
    public Label imgNameLabel, sizeLabel, entityNoLabel;

    public void displayBWImage(ActionEvent mouseEvent) { // CONSTRUCT BWIMAGE
        if(selectedImage!=null) {
            ((Pane) mainImageContainer.getParent()).getChildren().removeIf(c -> c instanceof Circle); // CLEAR CIRCLES
            ((Pane) mainImageContainer.getParent()).getChildren().removeIf(c -> c instanceof Text); // CLEAR NUMBERS

            mainWritableImage = new WritableImage((int) selectedImage.getWidth(), (int) selectedImage.getHeight()); // CREATE WRITABLE IMAGE
            arraySize = (int) selectedImage.getWidth() * (int) selectedImage.getHeight(); // SET ARRAY SIZE TO NUMBER OF PIXELS IN IMAGE
            BWArray = new int[arraySize];
            orderedRootsBySize.clear(); // EMPTY ORDER ROOTS SIZE TO AVOID TEXT AND CIRCLES OVERLAPPING
            PixelReader pixelReader = selectedImage.getPixelReader();
            PixelWriter pixelWriter = mainWritableImage.getPixelWriter();
            int arrayPosition = 0; // USED TO ITERATE OVER BWARRAY

            for (int j = 0; j < selectedImage.getHeight(); j++)
                for (int i = 0; i < selectedImage.getWidth(); i++) {
                    if (pixelReader.getColor(i, j).getBrightness() < brightnessSlider.getValue()) { // READS BRIGHTNESS SLIDER VALUE AS THRESHOLD
                        pixelWriter.setColor(i, j, Color.BLACK); // ANY PIXELS WITH LOWER BRIGHTNESS VALUE THAN SLIDER ARE SET TO BLACK
                        BWArray[arrayPosition] = -1; // BLACK PIXEL VALUE IN ARRAY = -1
                    } else {
                        pixelWriter.setColor(i, j, Color.WHITE); // ANY PIXELS WITH HIGHER BRIGHTNESS VALUE THAN SLIDER ARE SET TO WHITE
                        BWArray[arrayPosition] = arrayPosition; // WHITE PIXEL VALUE INITIALLY IS ARRAYPOSITION VALUE
                    }
                    arrayPosition++;
                }

            union(); // UNITING SETS
            createHashTable(); // CREATING HASH TABLE
            sortList(); // SORTING LIST

            for (int n = 0; n < BWArray.length; n++)
                System.out.print(find(BWArray, n) + (((n + 1) % selectedImage.getWidth() == 0) ? "\n" : " ")); // MATRIX DISPLAY
            sortList();
            for (int root : getSetOfRoots(BWArray))
                System.out.println("The size of set with root " + root + " is " + setSizes.get(root)); // INDIVIDUAL SET ROOT + SIZE

            System.out.println("There is a number of " + (BWArray) + " stars."); // TOTAL AMOUNT OF SETS
            mainWritableImageContainer.setImage(mainWritableImage);
        entityNoLabel.setText("Number of entities:  " + countStars(BWArray));
        } else System.out.println("Please select an image.");
    }

    public int find(int[] a, int id) {
        if (a[id] == -1) return -1; // IF ID IS BLACK PIXEL
        if (a[id] == id) return id; // IF ID IS ROOT VALUE
        else return find(a, a[id]); // CONTINUES SEARCH FOR ROOT
    }

    public void union() {
        int width = (int) selectedImage.getWidth();
        // HORIZONTAL ASSOCIATION
        for (int n = 0; n < BWArray.length - 1; n++)
            if (BWArray[n] != -1 && BWArray[n + 1] != -1)
                BWArray[find(BWArray, n + 1)] = find(BWArray, n);
        // VERTICAL ASSOCIATION
        for (int n = 0; n < BWArray.length - width; n++)
            if (BWArray[n] != -1 && BWArray[n + width] != -1)
                BWArray[find(BWArray, n + width)] = find(BWArray, n);
        setOfRoots=getSetOfRoots(BWArray);
    }

    public int countStars(int[] a) {
        Set<Integer> set = new HashSet<>(); // NEW SET
        for (int id = 0; id <= a.length - 1; id++) {
            if (find(a, id) != -1) // IF PIXEL IS NOT BLACK
                set.add(find(a, id)); // ADDS ROOT VALUE TO SET
        }
        return set.size(); // RETURNS SET SIZE
    }

    public void drawCircles(ActionEvent actionEvent) {
        PixelWriter pixelWriter = mainWritableImage.getPixelWriter();
        createHashTable();
        sortList();
        Collections.reverse(sortedBySizeList);
        System.out.println(getSetOfRoots(BWArray));
        ((Pane) mainImageContainer.getParent()).getChildren().removeIf(c -> c instanceof Circle); // CLEAR CIRCLES
        ((Pane) mainImageContainer.getParent()).getChildren().removeIf(c -> c instanceof Text); // CLEAR NUMBERS
        int count = 1;
        for (Map.Entry entry : sortedBySizeList) {
            if(setSizes.get(entry.getKey()) >= sizeSlider.getValue() && setSizes.get(entry.getKey())!=null) {
                List<Integer> rootIndices = new ArrayList<>(); // NEW LIST CONTAINING INDEXES OF ROOTVALUE
                for (int k = 0; k < BWArray.length; k++) {
                    if (find(BWArray, k) == (int) entry.getKey()) // CYCLES THROUGH BWARRAY AND FILLS ROOTINDICES ARRAY
                        rootIndices.add(k);
                }

                int top = (int) (Collections.min(rootIndices) / selectedImage.getWidth());
                int bottom = (int) (Collections.max(rootIndices) / selectedImage.getWidth());

                int left = (int) (Collections.min(rootIndices, (a, b) -> (int) (a % selectedImage.getWidth() - b % selectedImage.getWidth())) % selectedImage.getWidth());
                int right = (int) (Collections.max(rootIndices, (a, b) -> (int) (a % selectedImage.getWidth() - b % selectedImage.getWidth())) % selectedImage.getWidth());

                Circle c = new Circle();
                c.setCenterX(left + ((right - left) / 2));
                c.setCenterY(top + (bottom - top) / 2);
                c.setRadius(Math.max(bottom - top, right - left) / 2 + 2);
                c.setFill(Color.TRANSPARENT);                                   // CIRCLE PROPERTIES
                c.setStroke(Color.BLUE);
                c.setStrokeWidth(1);
                c.setTranslateX(mainImageContainer.getLayoutX());
                c.setTranslateY(mainImageContainer.getLayoutY());
                c.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Random p = new Random();
                        double r = p.nextDouble();
                        double g = p.nextDouble();
                        double b = p.nextDouble();
                        Color randomColor = new Color(r,g,b,1.0);
                        int position = 0;
                        for(int j=0;j< selectedImage.getHeight();j++)
                            for(int i=0; i< selectedImage.getWidth();i++) {
                                //finding position using height and width
                                position = (int) (j*selectedImage.getWidth()+i);
                                if(find(BWArray,position) == (int) entry.getKey())
                                    pixelWriter.setColor(i,j,randomColor);

                            }
                    }
                });

                ((Pane) mainImageContainer.getParent()).getChildren().add(c); // ADDS CIRCLE TO IMAGE
                Text t = new Text(c.getCenterX() + c.getRadius() - 10, c.getCenterY() - c.getRadius(), "" + count); // CREATE NUMBER
                t.setStyle("-fx-font: 10 regular;");
                t.setFill(Color.PINK);
                t.setTranslateX(mainImageContainer.getLayoutX());       // TEXT PROPERTIES
                t.setTranslateY(mainImageContainer.getLayoutY());
                ((Pane) mainImageContainer.getParent()).getChildren().add(t);
                count++; // NUMBER VALUE
                Tooltip.install(c, new Tooltip("Celestial Object Number: " + (count-1) + "\nEstimated Size(pixel units): " + setSizes.get((int) entry.getKey()) +
                        "\nEstimated Sulphur: " + getSulphur((int) entry.getKey()) +
                        "\nEstimated Hydrogen: " + getHydrogen((int) entry.getKey()) +
                        "\nEstimate Oxygen: " + getOxygen((int) entry.getKey())));
            }
        }
    }

    public Set<Integer> getSetOfRoots(int[] c) {
        for (int id = 0; id <= c.length - 1; id++) {
            if (find(c, id) != -1)
                set.add(find(c, id));
        }
        return set;
    }

    public void randomColor() {
        int position = 0;
        PixelWriter pixelWriter = mainWritableImage.getPixelWriter();
        for(int j=0;j< selectedImage.getHeight();j++)
            for(int i=0; i< selectedImage.getWidth();i++) {
                //finding position using height and width
                position= (int) (j*selectedImage.getWidth()+i);
                if(BWArray[position]!=-1) {
                    //using root to assign random colors
                    int root=find(BWArray,position);
                    Random rg=new Random(root);
                    double r = rg.nextDouble();
                    double g = rg.nextDouble();
                    double b = rg.nextDouble();
                    Color randomColor = new Color(r,g,b,1.0);
                    pixelWriter.setColor(i,j,randomColor);
                }
            }

    }

    public int individualStarSize(int root) {
        int position = 0,count=0;
        for (int j = 0; j < selectedImage.getHeight(); j++)
            for (int i = 0; i < selectedImage.getWidth(); i++) {
                //finding position using height and width
                position = (int) (j * selectedImage.getWidth() + i);
                if (BWArray[position] != -1 && find(BWArray, position)==root)
                    count++;
            }
        return count;
    }

    public double getSulphur(int objectRoot) {
        PixelReader pixelReader = selectedImage.getPixelReader();
        int position;
        double redValue = 0.0;
        for (int j = 0; j < selectedImage.getHeight(); j++)
            for (int i = 0; i < selectedImage.getWidth(); i++) {
                position= (int) (j*selectedImage.getWidth()+i);
                if(BWArray[position] == objectRoot) {
                    redValue += pixelReader.getColor(i,j).getRed();
                }
            }
        redValue = setSizes.get(objectRoot)/redValue;
        return redValue;
    }

    public double getHydrogen(int objectRoot) {
        PixelReader pixelReader = selectedImage.getPixelReader();
        int position;
        double greenValue = 0.0;
        for (int j = 0; j < selectedImage.getHeight(); j++)
            for (int i = 0; i < selectedImage.getWidth(); i++) {
                position= (int) (j*selectedImage.getWidth()+i);
                if(BWArray[position] == objectRoot) {
                    greenValue += pixelReader.getColor(i,j).getGreen();
                }
            }
        greenValue = setSizes.get(objectRoot)/greenValue;
        return greenValue;
    }

    public double getOxygen(int objectRoot) {
        PixelReader pixelReader = selectedImage.getPixelReader();
        int position;
        double blueValue = 0.0;
        for (int j = 0; j < selectedImage.getHeight(); j++)
            for (int i = 0; i < selectedImage.getWidth(); i++) {
                position= (int) (j*selectedImage.getWidth()+i);
                if(BWArray[position] == objectRoot) {
                    blueValue += pixelReader.getColor(i,j).getBlue();
                }
            }
        blueValue = setSizes.get(objectRoot)/blueValue;
        return blueValue;
    }

    public void createHashTable() {
        setSizes.clear();
        for(int i=0; i< BWArray.length; i++) {
            if(BWArray[i] != -1 && !setSizes.containsKey(find(BWArray,i))) {
                    setSizes.put(find(BWArray,i),individualStarSize(find(BWArray,i)));
            }
        }
    }

    public void sortList() {
        sortedBySizeList = new ArrayList<>(setSizes.entrySet());
        sortedBySizeList.sort((a , b) ->  b.getValue() - a.getValue());
        Collections.reverse(sortedBySizeList);
        System.out.println(sortedBySizeList);
    }

    private void openNewImageWindow(File file) {
        BWArray = new int[arraySize];
        orderedRootsBySize.clear();
        ((Pane)mainImageContainer.getParent()).getChildren().removeIf(c->c instanceof Circle);
        ((Pane)mainImageContainer.getParent()).getChildren().removeIf(c->c instanceof Text);
        selectedImage = new Image(file.toURI().toString(),mainImageContainer.getFitWidth(),mainImageContainer.getFitHeight(),false,true);
        String s = selectedImage.getUrl();
        int index = s.lastIndexOf('/');
        String name = s.substring(index+1);
        imgNameLabel.setText("Image:  " + name);
        entityNoLabel.setText("Number of entities:  ");
        sizeLabel.setText("Size:  " + selectedImage.getWidth() + " X " + selectedImage.getHeight());
        mainImageContainer.setImage(selectedImage);
        mainWritableImageContainer.setImage(null);
    }
    public void getImage(ActionEvent actionEvent) {
        setExtFilters(fileChooser);
        File file = fileChooser.showOpenDialog(HelloApplication.primaryStage);
        if(file!=null) {
            openNewImageWindow(file);
        }
    }

    private void setExtFilters(FileChooser chooser){
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser.setInitialDirectory(new File("C:\\Users\\Denis\\Pictures"));
        mainController = this;
    }

    public void closeApp(ActionEvent actionEvent) {
        HelloApplication.primaryStage.close();
    }

}