package controllers;

import java.awt.*;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private MainController mainController;
    public int[] BWArray = {-1,-1,2,2,2,-1,-1,7,7,-1,10};



    @BeforeEach
    void setUp() {
        mainController = new MainController();
    }


    @Test
    public void testCountStars() {
        assertEquals(3,mainController.countStars(BWArray));
    }

    @Test
    public void findFunctionTest() {
        assertEquals(2,mainController.find(BWArray,4));
    }

    @Test
    public void setOfRootsTest() {
        mainController.getSetOfRoots(BWArray);
        assertTrue(mainController.set.contains(2));
    }



    @AfterEach
    void tearDown() {
    }





}