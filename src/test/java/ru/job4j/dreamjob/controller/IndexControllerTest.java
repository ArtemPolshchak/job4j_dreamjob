package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    public void setUp() {
        indexController = new IndexController();
    }

    @Test
    public void testGetIndex() {
        String viewName = indexController.getIndex();

        assertEquals("index", viewName);
    }
}
