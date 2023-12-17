package application.utils;

public class DatabaseConventions {

    public String leaderboardSortedKeyString(int leaderboardId) {
        return "leaderboardSorted:" + leaderboardId;
    }

    public String leaderboardHashMapKeyString(int leaderboardId) {
        return "leaderboardHashMap:" + leaderboardId;
    }

    public String leaderboardScoreKeyString(int score, String timestamp, String playerId) {
        return score + ":" + timestamp + ":" + playerId;
    }

    public String playerObjectKeyString(String playerId) {
        return "player:" + playerId;
    }
}

