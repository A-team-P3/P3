package application.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(HTMLController.class)   // Only load the HTMLController class
class HTMLControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void devEndPointShouldReturnCorrectHtmlString() throws Exception {
        // Simulate a GET request to the "/dev" end-point
        mockMvc.perform(MockMvcRequestBuilders.get("/dev"))
                // Assert a 200 (OK) status code       
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Assert the view name is "leaderboardManager.html" (the string returned by the end-point)
                .andExpect(MockMvcResultMatchers.view().name("leaderboardManager.html"));
    }

    @Test
    public void errorEndPointShouldReturnWhoops() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("Whoops!"));
    }
}