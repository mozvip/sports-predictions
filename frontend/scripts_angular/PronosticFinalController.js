/**
 * Angular Controller -> LoginController  
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions')
.controller('PronosticFinalController', ['$scope', '$location', 'UserService', 'PredictionService', 'GamesService', 'Notification', '$linq', 
function($scope, $location, UserService, PredictionService, GamesService, Notification, $linq){

	$scope.updateEnabled = function() {
		return true;
	}

// JUST FOR TEST
	$scope.games = [
	{"matchNum":36,"dateTime":"2016-06-25T15:00", "group":"8èmes de finale","stadium":"Saint-Etienne","homeTeam":"2eme A","trueHomeTeam":"Suisse","trueAwayTeam":"Pologne", "awayTeam":"2eme C","homeScore":0,"awayScore":0},
	{"matchNum":37,"dateTime":"2016-06-25T18:00", "group":"8èmes de finale","stadium":"Paris","homeTeam":"1er B","trueHomeTeam":"Angleterre","trueAwayTeam":"Croatie","awayTeam":"3eme Acd","homeScore":0,"awayScore":0},
	{"matchNum":38,"dateTime":"2016-06-25T21:00", "group":"8èmes de finale","stadium":"Lens","homeTeam":"1er D","trueHomeTeam":"Espagne","trueAwayTeam":"Hongrie","awayTeam":"3eme Bef","homeScore":0,"awayScore":0},
	{"matchNum":39,"dateTime":"2016-06-26T15:00", "group":"8èmes de finale","stadium":"Lyon","homeTeam":"1er A","trueHomeTeam":"France","trueAwayTeam":"Suede","awayTeam":"3eme Cde","homeScore":0,"awayScore":0},
	{"matchNum":40,"dateTime":"2016-06-26T18:00", "group":"8èmes de finale","stadium":"Lille","homeTeam":"1er C","trueHomeTeam":"Allemagne","trueAwayTeam":"Slovaquie","awayTeam":"3eme Abf","homeScore":0,"awayScore":0},
	{"matchNum":41,"dateTime":"2016-06-26T21:00", "group":"8èmes de finale","stadium":"Toulouse","homeTeam":"1er F","trueHomeTeam":"Portugal","trueAwayTeam":"Italie","awayTeam":"2eme E","homeScore":0,"awayScore":0},
	{"matchNum":42,"dateTime":"2016-06-27T18:00", "group":"8èmes de finale","stadium":"Saint-Denis","homeTeam":"1er E","trueHomeTeam":"Belgique","trueAwayTeam":"Rep. Tcheque","awayTeam":"2eme D","homeScore":0,"awayScore":0},
	{"matchNum":43,"dateTime":"2016-06-27T21:00", "group":"8èmes de finale","stadium":"Nice","homeTeam":"2eme B","trueHomeTeam":"Russie","trueAwayTeam":"Autriche","awayTeam":"2eme F","homeScore":0,"awayScore":0},

	{"matchNum":44,"dateTime":"2016-06-30T21:00", "group":"Quarts de finale","stadium":"Marseille","homeTeam":"2eme A Ou 2eme C","awayTeam":"1er D Ou 3eme Bef","homeScore":0,"awayScore":0},
	{"matchNum":45,"dateTime":"2016-07-01T21:00", "group":"Quarts de finale","stadium":"Lille","homeTeam":"1er B Ou 3eme Acd","awayTeam":"1er F Ou 2eme E","homeScore":0,"awayScore":0},
	{"matchNum":46,"dateTime":"2016-07-02T21:00", "group":"Quarts de finale","stadium":"Bordeaux","homeTeam":"1er C Ou 3eme Abf","awayTeam":"1er E Ou 2eme D","homeScore":0,"awayScore":0},
	{"matchNum":47,"dateTime":"2016-07-03T21:00", "group":"Quarts de finale","stadium":"Saint-Denis","homeTeam":"1er A Ou 3eme Cde","awayTeam":"2eme B Ou 2eme F","homeScore":0,"awayScore":0},

	{"matchNum":48,"dateTime":"2016-07-06T21:00", "group":"Demi-finales","stadium":"Lyon","homeTeam":"Vq1","awayTeam":"Vq2","homeScore":0,"awayScore":0},
	{"matchNum":49,"dateTime":"2016-07-07T21:00", "group":"Demi-finales","stadium":"Marseille","homeTeam":"Vq3","awayTeam":"Vq4","homeScore":0,"awayScore":0},
	{"matchNum":50,"dateTime":"2016-07-10T21:00", "group":"Finale","stadium":"Saint-Denis","homeTeam":"","awayTeam":"","homeScore":0,"awayScore":0}
	];
	
	$scope.tabs = [
		{ title:'8èmes de finale', content:'<div class="item active"><h3 class="group-name">8èmes de finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'8èmes de finale\'}:true" match="match"></pronostic-final></div>'},
		{ title:'Quarts de finale', content:'<div class="item"><h3 class="group-name">Quarts de finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Quarts de finale\'}:true" match="match"></pronostic-final></div>'},
		{ title:'Demi-finales', content:'<div class="item"><h3 class="group-name">Demi-finales</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Demi-finales\'}:true" match="match"></pronostic-final></div>'},
		{ title:'Finale', content:'<div class="item"><h3 class="group-name">Finale</h3><hr/><pronostic-final ng-repeat="match in games | filter:{group:\'Finale\'}:true" match="match"></pronostic-final></div>'}];
		
	$scope.init = function(){
		// TODO :
	}	
	
	
	$scope.submitPronostic = function(){
		var community = $location.host() == 'localhost' ? 'test' : $location.host();
		var predictions = [];
		
		$linq.Enumerable()
			.From($scope.games)
			.ForEach(function(element){
				predictions.push(createPrediction(community, element));
		});
		
		PredictionService.savePredictions(UserService.getToken(), {
		  match_predictions_attributes: predictions
		})
		.then(function(result){
			if (result.status == 'success')
				Notification.success( result.message );
			else
				Notification.error( result.message );
		});
	} 
	
	var createPrediction = function(host, game){
		return {
			community : host, 
			email: UserService.getCurrentLogin(), 
			match_id: game.matchNum, 
			away_score: game.predictionAway_Score, 
			away_team_id: game.awayTeam,
			home_score: game.predictionHome_Score, 
			home_team_id: game.homeTeam,
			home_winner: game.home_winner
		};
	}

			
	$scope.watchMatch = function(match){
		if(match.group === '8�mes de finale'){
			var matchQuart = $linq.Enumerable()
									.From($scope.games)
									.Where(function(m){
										return m.homeTeam === match.homeTeam + ' Ou ' + match.awayTeam || 
										m.awayTeam === match.homeTeam + ' Ou ' + match.awayTeam;
									}).First();
			
			var winnerTeam = $scope.winnerMatch(match);
			if(winnerTeam != false)
			{
				if(matchQuart.homeTeam === match.homeTeam + ' Ou ' + match.awayTeam)
					matchQuart.trueHomeTeam = winnerTeam;
				else
					matchQuart.trueAwayTeam = winnerTeam;
			}
			$scope.watchMatch(matchQuart);
		}
		else if(match.group == 'Quarts de finale'){
			var indexQuart, matchDemie;
			indexQuart = $linq.Enumerable()
							.From($scope.games)
							.Where(function(m){
								return m.group == 'Quarts de finale';
							})
							.OrderBy(function(m){
								return m.matchNum;
							})
							.Select(function(m){
								return m.matchNum;
							})
							.IndexOf(match.matchNum) + 1;				
			if(indexQuart != -1)
			{
				matchDemie = $linq.Enumerable()
							.From($scope.games)
							.Where(function(m){
								return m.homeTeam === 'Vq'+indexQuart || 
										m.awayTeam === 'Vq'+indexQuart;
							}).First();			
				var winnerTeam = $scope.winnerMatch(match);
				if(winnerTeam != false)
				{
					if(matchDemie.homeTeam === 'Vq'+indexQuart)
						matchDemie.trueHomeTeam = winnerTeam;
					else
						matchDemie.trueAwayTeam = winnerTeam;
				}
			}
			$scope.watchMatch(matchDemie);
		}
		else if(match.group == 'Demi-finales'){
				var  matchFinale = $linq.Enumerable()
							.From($scope.games)
							.Where(function(m){
								return m.group === 'Finale';
							}).First();			
				var winnerTeam = $scope.winnerMatch(match);
				if(winnerTeam != false && matchFinale != null)
				{
					if(match.homeTeam === 'Vq1')
						matchFinale.trueHomeTeam = winnerTeam;
					else
						matchFinale.trueAwayTeam = winnerTeam;
				}
		}
	}
	
	
	$scope.winnerMatch = function(match){
		if(match.predictionHome_Score != undefined && match.predictionAway_Score != undefined)
			return (match.predictionHome_Score > match.predictionAway_Score || (match.predictionHome_Score == match.predictionAway_Score && match.home_winner)) ? match.trueHomeTeam : match.trueAwayTeam;
		else
			return false;
	}
}]);