package application.controllers;

import application.models.Player;
import application.services.DatabaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the web layer context of LeaderboardAPI
@WebMvcTest(LeaderboardAPI.class)
class LeaderboardAPITest {

    // MockMvc is a Spring class that allows us to test Spring MVC web application
    @Autowired
    private MockMvc mockMvc;

    // MockBean allows us to mock and inject a bean.
    // A bean is an object that is instantiated, assembled, and managed by a Spring IoC container.
    @MockBean
    private DatabaseService databaseService;

    // Simulates a GET request to the /findPlayer endpoint and asserts a 200 (OK) status code,
    // and the expected JSON response.
    @Test
    void findPlayerShouldReturnMatchingPlayer() throws Exception {
        // Arrange
        String name = "Bruce Wayne";
        int leaderboardId = 1;
        Player player = new Player("ABCD123", name, "1337", "NA", "1");
        List<Player> matchingPlayers = List.of(player);

        // Act
        // Mocking the databaseService.findPlayersByName() method with when() from the Mockito library
        when(databaseService.findPlayersByName(name, leaderboardId)).thenReturn(matchingPlayers);

        // Assert
        // Simulating the HTTP GET request to the /findPlayer endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/findPlayer")   // Building the request
            .param("name", name)
            .param("leaderboardId", String.valueOf(leaderboardId))
            .accept(MediaType.APPLICATION_JSON))                              // Accepting JSON response
            .andExpect(status().isOk())                                       // Asserting the 200 (OK) status code
            .andExpect(content().json("[{" +                        // Asserting the expected JSON response
                    "'id':'ABCD123'," +
                    "'name':'Bruce Wayne'," +
                    "'score':'1337'," +
                    "'region':'NA'," +
                    "'creationDate':'" + player.getCreationDate() + "', " +
                    "'rank':'1'" +
                    "}]"
            ));
    }
}