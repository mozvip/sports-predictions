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

            $scope.refreshPredictions = function (predictions) {
                $linq.Enumerable()
                    .From($scope.games)
                    .ForEach(function (element) {
                        var gameID = element.matchNum;
                        var prediction = $linq.Enumerable()
                            .From(predictions)
                            .FirstOrDefault(null, function (prediction) {
                                return prediction.match_id === gameID;
                            });

                        if (prediction != null) {
                            element.predictionHome_Score = prediction.home_score;
                            element.predictionAway_Score = prediction.away_score;
                            element.predictionScore = prediction.score;
                            element.home_winner = prediction.home_winner;

                            $scope.watchMatch(element);
                        }
                        else {
                            element.predictionHome_Score = 0;
                            element.predictionAway_Score = 0;
                            element.predictionScore = 0;
                            element.home_winner = false;
                        }

                        if (element.done) {
                            element.predictionHomeTeam = prediction.home_team_name;
                            element.predictionAwayTeam = prediction.away_team_name;
                        }

                        // Display real team if possible
                        if (element.homeTeamWinnerFrom && element.awayTeamWinnerFrom) {
                            var gameFrom = $linq.Enumerable().From($scope.games).First(function (g) {
                                return element.homeTeamWinnerFrom == g.matchNum
                            });
                            var gameAway = $linq.Enumerable().From($scope.games).First(function (g) {
                                return element.awayTeamWinnerFrom == g.matchNum
                            });
                            if (gameFrom.done)
                                element.trueTeamFrom = gameFrom.winningTeam;
                            if (gameAway.done)
                                element.trueTeamAway = gameAway.winningTeam;
                        }

                    });

            }

            $scope.delegateUserChanged = function () {
                if ($scope.delegateEmail) {
                    PredictionService.getPredictions($scope.delegateEmail).then(
                        function (response) {
                            $scope.refreshPredictions(response.data.match_predictions_attributes);
                        }, function (response) {

                        }
                    )
                }
            }


            $scope.submitPronostic = function () {
                var predictions = [];

                $linq.Enumerable()
                    .From($scope.games)
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

            $scope.submitPronosticsImpersonate = function () {
                var predictions = [];

                $linq.Enumerable()
                    .From($scope.games)
                    .ForEach(function (game) {
                        predictions.push(
                            createPrediction(game)
                        );
                    });

                PredictionService.savePredictions({
                    email: $scope.delegateEmail,
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


            $scope.watchMatch = function (match) {

                var winningTeamName = $scope.winnerMatch(match);

                if (winningTeamName === undefined) {
                    return;
                }

                for (var i = 0; i < $scope.games.length; i++) {
                    if ($scope.games[i].homeTeamWinnerFrom === match.matchNum) {
                        $scope.games[i].homeTeamName = winningTeamName;
                    } else if ($scope.games[i].awayTeamWinnerFrom === match.matchNum) {
                        $scope.games[i].awayTeamName = winningTeamName;
                    }
                }
            }


            $scope.winnerMatch = function (match) {
                if (match.predictionHome_Score != undefined && match.predictionAway_Score != undefined) {
                    var homeWinner = (typeof match.home_winner == 'boolean' ? match.home_winner : Boolean(match.home_winner));
                    return (match.predictionHome_Score > match.predictionAway_Score || (match.predictionHome_Score == match.predictionAway_Score && homeWinner)) ? match.homeTeamName : match.awayTeamName;
                }
                else
                    return false;
            }
        }]);