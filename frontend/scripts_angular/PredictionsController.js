/**
* Angular Controller -> PredictionsController  
* Save and Get prediction data in euro2016 Predictor
* init() * Get data prediction with games data
* submitPronostic() * Save predictions
**/
angular.module('sports-predictions').controller('PredictionsController', ['$scope', '$location', 'UserService', 'PredictionService', 'GamesService', 'Notification', '$linq', 'currentUser', function ($scope, $location, UserService, PredictionService, GamesService, Notification, $linq, currentUser) {

    $scope.games = [];

    $scope.tabs = [{ title: 'Groupe A', content: '<div class="item active"><h3 class="group-name">Groupe A</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe A\'" match="match"></pronostic></div>' },
        { title: 'Groupe B', content: '<div class="item"><h3 class="group-name">Groupe B</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe B\'" match="match"></pronostic></div>' },
        { title: 'Groupe C', content: '<div class="item"><h3 class="group-name">Groupe C</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe C\'" match="match"></pronostic></div>' },
        { title: 'Groupe D', content: '<div class="item"><h3 class="group-name">Groupe D</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe D\'" match="match"></pronostic></div>' },
        { title: 'Groupe E', content: '<div class="item"><h3 class="group-name">Groupe E</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe E\'" match="match"></pronostic></div>' },
        { title: 'Groupe F', content: '<div class="item"><h3 class="group-name">Groupe F</h3><hr/><pronostic ng-repeat="match in games | filter:\'Groupe F\'" match="match"></pronostic></div>' }];

    $scope.init = function () {

        var error = false;
        $scope.games = [];
        var res = GamesService.getGroupGames();
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
                        }
                        else {
                            element.predictionHome_Score = 0;
                            element.predictionAway_Score = 0;
                        }
                        element.home_winner = false;
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
        var community = $location.host() == 'localhost' ? 'test' : $location.host();
        var predictions = [];

        $linq.Enumerable()
            .From($scope.games)
            .ForEach(function (element) {
                predictions.push(createPrediction(community, element));
            });

        PredictionService.savePredictions({
            match_predictions_attributes: predictions
        })
            .then(function (result) {
                if (result.status == 'success') {
                    Notification.success(result.message);
                    UserService.refreshProfile();
                }
                else
                    Notification.error(result.message);
            });
    }

    var createPrediction = function (host, game) {
        return {
            community: host,
            email: UserService.getCurrentLogin(),
            match_id: game.matchNum,
            away_score: game.predictionAway_Score,
            away_team_id: game.awayTeam,
            home_score: game.predictionHome_Score,
            home_team_id: game.homeTeam,
            home_winner: (game.predictionHome_Score > game.predictionAway_Score)
        };
    }
}]);
