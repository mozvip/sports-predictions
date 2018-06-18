angular.module('sports-predictions')
.controller('StatsController', ['$scope', 'GamesService', '$location', '$linq', 'Highcharts', 'currentUser', function($scope, GamesService, $location, $linq, Highcharts, currentUser){
	
	$scope.statsPrediction = {perfect:0, good: 0, bad:  0 };
	
	$scope.bestpronosticman = 'test@test.com';
	$scope.lastpronosticman = 'guillaume.serre@gmail.com';
	
	$scope.init = function(){
		GamesService.getGroupGames().then(function(result){	
			angular.forEach(result.Games, function(game){
				if(game.done){
					var gameID = game.matchNum;
					var score = $linq.Enumerable()
					.From(currentUser.match_predictions_attributes)
					.FirstOrDefault(-1, function(pred){
						return pred.match_id == gameID;
					}).score;
					
					if(score == 3)
						++$scope.statsPrediction.perfect;
					else if(($location.host() == 'michelin-solutions' && score == 2) ||
							($location.host() != 'michelin-solutions' && score == 1))
						++$scope.statsPrediction.good;
					else if(score == 0)
						++$scope.statsPrediction.bad;
				}
			});
		});
	}
	
	$scope.drawStatsUser = function(){ 
	// TODO:
	}
	
	Highcharts.chart('containerUserRank', {
		chart: {
			type: 'line',
			inverted: false
		},
		title: {
            text: 'Historique de votre classement',
            x: -20
        },
		xAxis: {
			categories: ['Match 1', 'Match 2', 'Match 3', 'Match 4', 'Match 5', 'Match 6', 
				'Match 7', 'Match 8', 'Match 9', 'Match 10', 'Match 11', 'Match 12']
		},
		exporting:{
			enabled: false
		},	
		credits: {
			enabled: false
		},
		series: [{
			name: 'Classement',
		type: 'line',
		data: [5,5,2,81,88,1,8,1,8,1,8,9],
		color: '#FF0000'
		}]
	});
	
	Highcharts.chart('containerComScore', {
		chart: {
			type: 'bar',
			inverted: false
		},
		title: {
            text: 'Moyenne de points inscrits par match et par personne',
            x: -20
        },
		xAxis: {
			categories: ['Match 1', 'Match 2', 'Match 3', 'Match 4', 'Match 5', 'Match 6', 
				'Match 7', 'Match 8', 'Match 9', 'Match 10', 'Match 11', 'Match 12']
		},
		exporting:{
			enabled: false
		},	
		credits: {
			enabled: false
		},
		series: [{
			name: 'Moyenne',
		type: 'bar',
		data: [5,5,2,81,88,1,8,1,8,1,8,9],
		color: '#FF0000'
		},{
			name: 'Votre score',
		type: 'bar',
		data: [5,5,2,5,1,11,3,1,8,1,8,9],
		color: '#FF00FF'
		}]
	});
}]);