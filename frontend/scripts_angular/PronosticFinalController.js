/**
 * Angular Controller -> LoginController
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions')
    .controller('PronosticFinalController', ['$scope', '$location', 'UserService', 'BackendService', 'PredictionService', 'GamesService', 'Notification', '$linq', 'currentUser', 'games', 'community',
        function ($scope, $location, UserService, BackendService, PredictionService, GamesService, Notification, $linq, currentUser, games, community) {

            $scope.games = games;
            $scope.finalGames = games.filter(game => game.round);
            $scope.teams = games.filter(game => game.group).map(game => game.homeTeamName);

            $scope.teamsAreKnown = function(game) {
                return $scope.teams.includes(game.homeTeamName) && $scope.teams.includes(game.awayTeamName);
            }

            $scope.readyGames = $scope.finalGames.filter(game => $scope.teamsAreKnown(game));

            $scope.tabs = [
                {
                    title: '8èmes de finale',
                    content: '<div class="item"><pronostic-final ng-repeat="match in finalGames | filter:{round:\'Round of 16\'}:true" match="match" ready="teamsAreKnown(match)" access="community.finalsAccess"></pronostic-final></div>'
                },
                {
                    title: 'Quarts de finale',
                    content: '<div class="item"><pronostic-final ng-repeat="match in finalGames | filter:{round:\'Quarter-finals\'}:true" match="match" ready="teamsAreKnown(match)" access="community.finalsAccess"></pronostic-final></div>'
                },
                {
                    title: 'Demi-finales',
                    content: '<div class="item"><pronostic-final ng-repeat="match in finalGames | filter:{round:\'Semi-finals\'}:true" match="match" ready="teamsAreKnown(match)" access="community.finalsAccess"></pronostic-final></div>'
                },
                {
                    title: 'Match 3ème place',
                    content: '<div class="item"><pronostic-final ng-repeat="match in finalGames | filter:{round:\'Third place play-off\'}:true" match="match" ready="teamsAreKnown(match)" access="community.finalsAccess"></pronostic-final></div>'
                },
                {
                    title: 'Finale',
                    content: '<div class="item"><pronostic-final ng-repeat="match in finalGames | filter:{round:\'Final\'}:true" match="match" ready="teamsAreKnown(match)" access="community.finalsAccess"></pronostic-final></div>'
                }
            ];

            $linq.Enumerable()
                .From($scope.finalGames)
                .ForEach(function (element) {
                    var gameID = element.matchNum;
                    var prediction = $linq.Enumerable()
                        .From(currentUser.match_predictions_attributes)
                        .FirstOrDefault(null, function (prediction) {
                            return prediction.match_id === gameID;
                        });

                    if (prediction != null) {
                        element.predictionHome_Score = prediction.home_score;
                        element.predictionAway_Score = prediction.away_score;
                        element.predictionScore = prediction.score;
                        element.home_winner = prediction.home_winner;

                    }
                    else {
                        element.predictionHome_Score = 0;
                        element.predictionAway_Score = 0;
                        element.predictionScore = 0;
                        element.home_winner = false;
                    }
                });

            $scope.submitPronostic = function () {
                var predictions = [];

                $linq.Enumerable()
                    .From($scope.readyGames)
                    .ForEach(function (element) {
                        predictions.push(createPrediction(element));
                    });

                PredictionService.savePredictions({
                    match_predictions_attributes: predictions
                })
                    .then(function (result) {
                        if (result.status == 'success') {
                            UserService.refreshProfile();
                            Notification.success(result.message);
                        }
                        else
                            Notification.error(result.message);
                    });
            }

            var createPrediction = function (game) {
                return {
                    match_id: game.matchNum,
                    away_score: game.predictionAway_Score,
                    away_team_name: game.awayTeamName,
                    home_score: game.predictionHome_Score,
                    home_team_name: game.homeTeamName,
                    home_winner: game.home_winner
                };
            }

        }]);