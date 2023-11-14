package application.utils;

import java.util.Random;

public class DatabasePopulator {

    private String userIdGenerator() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        final int USER_ID_LENGTH = 7;
        final Random random = new Random();
        StringBuilder userId = new StringBuilder(USER_ID_LENGTH);

        for (int i = 0; i < USER_ID_LENGTH; i++) {
            userId.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return userId.toString();
    }
}
