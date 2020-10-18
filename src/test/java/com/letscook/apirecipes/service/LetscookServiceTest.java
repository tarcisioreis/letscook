package com.letscook.apirecipes.service;

import com.letscook.apirecipes.config.ConfigProperties;
import com.letscook.apirecipes.dto.DataDTO;
import jdk.jfr.events.ExceptionThrownEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConfigProperties.class)
@TestPropertySource(value = "file:./src/main/resources/application.properties")
public class LetscookServiceTest {

    @InjectMocks
    private LetscookService service;

    @Autowired
    private ConfigProperties configProperties;

    @Before
    public void setUp() throws Exception {
        service = new LetscookService(configProperties);
    }

    @Test
    public void success() {
        String ingredientes = "onio, tomato, garlic";

        try {
            List<DataDTO> lista = service.getRecipes(ingredientes);
            assertNotNull(lista);
            assertEquals(1, lista.size());
        } catch (Exception e) {
            assertEquals(configProperties.getERROR_RETURN_ENDPOINT(), e.getMessage());
        }

    }

    @Test
    public void fail() {
        String ingredientes = null;

        try {
            List<DataDTO> lista = service.getRecipes(ingredientes);
            assertNull(lista);
        } catch (Exception e) {
            assertEquals(configProperties.getERROR_RETURN_ENDPOINT(), e.getMessage());
        }

    }

}