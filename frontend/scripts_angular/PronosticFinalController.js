/**
 * Angular Controller -> LoginController  
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions')
	.controller('PronosticFinalController', ['$scope', '$location', 'UserService', 'BackendService', 'PredictionService', 'GamesService', 'Notification', '$linq', 'currentUser', 'community',
		function ($scope, $location, UserService, BackendService, PredictionService, GamesService, Notification, $linq, currentUser, community) {

			$scope.tabs = [
				{ title: '8èmes de finale', content: '<div class="item active"><h3 class="group-name">8èmes de finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'8èmes de finale\'}:true" match="match" access="community.finalsAccess"></pronostic-final></div>' },
				{ title: 'Quarts de finale', content: '<div class="item"><h3 class="group-name">Quarts de finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Quarts de finale\'}:true" match="match" access="community.finalsAccess"></pronostic-final></div>' },
				{ title: 'Demi-finales', content: '<div class="item"><h3 class="group-name">Demi-finales</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Demi-finales\'}:true" match="match" access="community.finalsAccess"></pronostic-final></div>' },
				{ title: 'Finale', content: '<div class="item"><h3 class="group-name">Finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Finale\'}:true" match="match" access="community.finalsAccess"></pronostic-final></div>' }];

			$scope.init = function () {
				var error = false;
				$scope.games = [];
				var res = GamesService.getFinalGames();
				res.then(function (result) {
					if (result.Games != null && result.Games != undefined) {
						$scope.games = result.Games;

						$linq.Enumerable()
							.From($scope.games)
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

									$scope.watchMatch( element );
								}
								else {
									element.predictionHome_Score = 0;
									element.predictionAway_Score = 0;
									element.predictionScore = 0;
									element.home_winner = false;
								}
								
								if(element.done){
									element.predictionHomeTeam = prediction.home_team_id;
									element.predictionAwayTeam = prediction.away_team_id;
								}

								// Display real team after 8eme final 
								if(element.group.indexOf("8èmes de finale") == -1){
									var gameFrom = $linq.Enumerable().From($scope.games).First(function(g){return element.homeTeamWinnerFrom == g.matchNum});
									var gameAway = $linq.Enumerable().From($scope.games).First(function(g){return element.awayTeamWinnerFrom == g.matchNum});
									if(gameFrom.done)
										element.trueTeamFrom = gameFrom.winningTeam;
									if(gameAway.done)
										element.trueTeamAway = gameAway.winningTeam;
								}

							});
					} else {
						Notification.error({ message: 'Les scores des matches n\'ont pu être récupérés. Un problème technique est à l\'origine du problème.', title: 'Erreur' });
						error = true;
					}
				});

				if (error) {
					$scope.games = [];
					return;
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
					away_team_id: game.awayTeam,
					home_score: game.predictionHome_Score,
					home_team_id: game.homeTeam,
					home_winner: game.home_winner
				};
			}


			$scope.watchMatch = function (match) {

				var winningTeamName = $scope.winnerMatch(match);

				if (winningTeamName === undefined) {
					return;
				}

				for (var i=0; i<$scope.games.length; i++) {
					if ($scope.games[i].homeTeamWinnerFrom === match.matchNum) {
						$scope.games[i].homeTeam = winningTeamName;
					} else if ($scope.games[i].awayTeamWinnerFrom === match.matchNum) {
						$scope.games[i].awayTeam = winningTeamName;
					}
				}
			}



			$scope.winnerMatch = function (match) {
				if (match.predictionHome_Score != undefined && match.predictionAway_Score != undefined) {
					var homeWinner = (typeof match.home_winner == 'boolean' ? match.home_winner : Boolean( match.home_winner ));
					return (match.predictionHome_Score > match.predictionAway_Score || (match.predictionHome_Score == match.predictionAway_Score && homeWinner)) ? match.homeTeam : match.awayTeam;
				}
				else
					return false;
			}
		}]);