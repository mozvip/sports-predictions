package predictions.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.model.db.ActualResult;
import predictions.model.db.ActualResultDAO;
import predictions.model.db.MatchPrediction;
import predictions.model.db.MatchPredictionDAO;

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
    private MatchPredictionDAO matchPredictionDAO;

    public GamesManager(ActualResultDAO actualResultDAO, MatchPredictionDAO matchPredictionDAO) {
        this.actualResultDAO = actualResultDAO;
        this.matchPredictionDAO = matchPredictionDAO;
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

    public void refreshScore(int gameNum) {
        refreshScore(gameNum, actualResultDAO.find(gameNum));
    }

    public void refreshScore(int gameNum, ActualResult actualResult) {

        Game game = getGame(gameNum);

        int homeScore = actualResult.getHome_score();
        int awayScore = actualResult.getAway_score();

        String homeTeam = actualResult.getHome_team_id();
        String awayTeam = actualResult.getAway_team_id();

        boolean homeWinning = actualResult.isHome_winner();

        String actualWinner;
        if (homeScore > awayScore) {
            actualWinner = homeTeam;
        } else if (awayScore > homeScore) {
            actualWinner = awayTeam;
        } else {
            actualWinner = homeWinning ? homeTeam : awayTeam;
        }

        List<MatchPrediction> predictions = matchPredictionDAO.findPredictions( gameNum );
        for (MatchPrediction prediction : predictions) {

            String predictionWinner;
            if (prediction.getHome_score() > prediction.getAway_score()) {
                predictionWinner = prediction.getHome_team_name();
            } else if (prediction.getAway_score() > prediction.getHome_score()) {
                predictionWinner = prediction.getAway_team_name();
            } else {
                predictionWinner = prediction.isHome_winner() ? prediction.getHome_team_name() : prediction.getAway_team_name();
            }

            int matchScore = 0;

            if ( prediction.getCommunity().startsWith("michelin-solutions")) {


                if (!game.getGroup().startsWith("Groupe ")) {

                    if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore && prediction.getHome_team_name().equals(game.getHomeTeamName()) && prediction.getAway_team_name().equals(game.getAwayTeamName())) {
                        matchScore = 5;
                        if (homeScore == awayScore) { // draw
                            if (prediction.isHome_winner() != homeWinning) { // wrong qualified team
                                matchScore = 2;
                            }
                        }
                    } else if ( actualWinner.equals( predictionWinner )) {
                        matchScore = 3;
                    } else if ((prediction.getHome_score() == homeScore && prediction.getHome_team_name().equals(game.getHomeTeamName())) || (prediction.getAway_score() == awayScore && prediction.getAway_team_name().equals(game.getAwayTeamName()))) {
                        matchScore = 1;
                    }

                } else {

                    if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore) {
                        // perfect score
                        matchScore = 3;
                    } else if (
                            ( prediction.getHome_score() == prediction.getAway_score() && homeScore == awayScore ) ||
                                    ( prediction.getHome_score() > prediction.getAway_score() && homeScore > awayScore ) ||
                                    ( prediction.getHome_score() < prediction.getAway_score() && homeScore < awayScore ) )
                    {

                        matchScore = 2;

                    } else if ((prediction.getHome_score() == homeScore && prediction.getHome_team_name().equals(game.getHomeTeamName())) || (prediction.getAway_score() == awayScore && prediction.getAway_team_name().equals(game.getAwayTeamName()))) {
                        matchScore = 1;
                    }

                }


            } else {


                if (game.getGroup() == null) {

                    if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore && prediction.getHome_team_name().equals(game.getHomeTeamName()) && prediction.getAway_team_name().equals(game.getAwayTeamName())) {
                        matchScore = 5;
                        if (homeScore == awayScore) { // draw
                            if (prediction.isHome_winner() != homeWinning) { // wrong qualified team
                                matchScore = 1;
                            }
                        }
                    } else if ( actualWinner.equals( predictionWinner )) {
                        matchScore = 3;
                    }

                } else {

                    if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore) {
                        // perfect score
                        matchScore = 3;
                    } else if (
                            ( prediction.getHome_score() == prediction.getAway_score() && homeScore == awayScore ) ||
                                    ( prediction.getHome_score() > prediction.getAway_score() && homeScore > awayScore ) ||
                                    ( prediction.getHome_score() < prediction.getAway_score() && homeScore < awayScore ) )
                    {

                        matchScore = 1;
                    }

                }

            }

            matchPredictionDAO.updateScore( prediction.getCommunity(), prediction.getEmail(), prediction.getMatch_id(), matchScore);
        }
    }
}
