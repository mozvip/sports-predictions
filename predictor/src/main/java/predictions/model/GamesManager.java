package predictions.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.model.db.ActualResult;
import predictions.model.db.ActualResultDAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesManager implements Managed {

    private final static Logger logger = LoggerFactory.getLogger( GamesManager.class );

    private List<Game> games;
    private Map<Integer, Game> gamesById = new HashMap<>();
    private ActualResultDAO actualResultDAO;

    public GamesManager(ActualResultDAO actualResultDAO) {
        this.actualResultDAO = actualResultDAO;
    }

    public List<Game> getGames() {
        return games;
    }

    public Game getGame(Integer id) {
        return gamesById.get(id);
    }

    @Override
    public void start() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("games.json");
        if (input == null) {
            throw new IOException("Unable to load games.json, aborting");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            games = mapper.readValue(input, new TypeReference<List<Game>>(){});
            for (Game game : games) {
                gamesById.put(game.getMatchNum(), game);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        updateGames();
    }

    private void updateGames() {
        Map<Integer, String> winners = new HashMap<>();
        Map<Integer, String> losers = new HashMap<>();

        List<ActualResult> allResults = actualResultDAO.findAll();
        for (ActualResult actualResult : allResults) {
            int gameId = actualResult.getMatch_id();
            Game game = setFinalScore(gameId, actualResult.getHome_score(), actualResult.getAway_score(), actualResult.isHome_winner());
            winners.put(game.getMatchNum(), game.getWinner());
            losers.put(game.getMatchNum(), game.getLoser());
        }

        for (Game game : getGames()) {
                if (game.getHomeTeamName() == null || game.getAwayTeamName() == null) {
                    if (game.getHomeTeamWinnerFrom() > 0) {
                        game.setHomeTeamName(winners.get(game.getHomeTeamWinnerFrom()));
                    }
                    if (game.getAwayTeamWinnerFrom() > 0) {
                        game.setAwayTeamName(winners.get( game.getAwayTeamWinnerFrom()));
                    }
                    if (game.getHomeTeamLoserFrom() > 0) {
                        game.setHomeTeamName(winners.get(game.getHomeTeamLoserFrom()));
                    }
                    if (game.getAwayTeamLoserFrom() > 0) {
                        game.setAwayTeamName(winners.get( game.getAwayTeamLoserFrom()));
                    }
                }
        }
    }

    public Game setFinalScore(int gameId, int homeScore, int awayScore, boolean homeWinner) {
        Game game = getGame(gameId);
        game.setDone(true);
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);

        String winnerTeam;
        String loserTeam;
        if (homeScore > awayScore) {
            winnerTeam = game.getHomeTeamName();
            loserTeam = game.getAwayTeamName();
        } else if (homeScore < awayScore) {
            winnerTeam = game.getAwayTeamName();
            loserTeam = game.getHomeTeamName();
        } else {
            winnerTeam = homeWinner ? game.getHomeTeamName() : game.getAwayTeamName();
            loserTeam = homeWinner ? game.getAwayTeamName() : game.getHomeTeamName();
        }

        game.setWinner(winnerTeam);
        game.setLoser(loserTeam);
        return game;
    }

    @Override
    public void stop() throws Exception {

    }

}
