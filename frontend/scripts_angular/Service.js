/**
* Angular Service -> PredictionService
* Expose method to save and get predictions for one user
* Controler which use this service : PronosticController
* savePredictions() -> Save predictions to user connected
* getPredictions() -> Get predictions to user connected
**/
var PredictionService = function($rootScope, $http, $q, BackendService){
	return {
		savePredictions: function(token, predictions){
			var deferredObject = $q.defer();
			var result = {};
			var config = {
				headers : { 'Accept' : 'application/json',
				'Content-Type': 'application/json',
				'Authorization': 'Basic ' + token}
			};
			$http
			.post(BackendService.getBackEndURL() + 'user/save', predictions, config)
			.then(function(data){
				if (data.status === 204)
					deferredObject.resolve({status:'success', message:'Sauvegarde réalisée avec succès'});
				else	
					deferredObject.resolve({status:'error', message:'Une erreur est survenue lors de la sauvegarde des pronostiques'});
			}, function(response){
				deferredObject.resolve({status:'error', message:'Une erreur est survenue lors de la sauvegarde des pronostiques'});
			});
			
			return deferredObject.promise;
		},
		getPredictions: function(token){
			var deferredObject = $q.defer();
			var result = {};
			var config = {
				headers : { 'Accept' : 'application/json',
				'Authorization': 'Basic ' + token}
			};
			$http
			.get(BackendService.getBackEndURL() + 'user/predictions', config)
			.then(function(data){
				result.status = data.status;
				if(data.status === 200)
					result.Predictions = data.data.match_predictions_attributes;
				deferredObject.resolve({ Predictions:  result });
			}, function(response){
				deferredObject.resolve({ Predictions: null });
			});
			
			return deferredObject.promise;
		}
	};
}
PredictionService.$inject = ['$rootScope', '$http', '$q', 'BackendService'];

/**
* Angular Service -> RankingService
* Expose method to get ranks of user
* getRanks() -> Return all ranks of community user
**/
var RankingService = function($rootScope, $http, $q, BackendService){
	
	return {
		getRanks : function(){
			var deferredObject = $q.defer();
			var result= {};
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
			.get(BackendService.getBackEndURL() + 'user/rankings', config)
			.then(function(data){
				result.status = data.status;
				if(data.status === 200)
					result.RanksData = data.data.data;
				else
					result.message = 'Erreur dans la récupération des résultats !';
				deferredObject.resolve({ Ranks: result });
			}, function(data){
				result.message = 'Erreur dans la récupération des résultats !';
				deferredObject.resolve({ Ranks: result });
			});
			return deferredObject.promise;
		}
	};
}
RankingService.$inject = ['$rootScope', '$http', '$q', 'BackendService'];

/**
* Angular Service -> GamesService
* Expose method to get all games with real result, information stadium, date and teams
* getGroupGames() -> Return all games of group phase
* getFinalGames() -> Return all games of final phase
**/
var GamesService = function($rootScope, $http, $q, $linq, BackendService){
	return {
		getGroupGames: function(){
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
			.get(BackendService.getBackEndURL() + 'score/games', config)
			.then(function(data){
				if(data.status === 200)
					result = $linq.Enumerable()
								.From(data.data)
								.Where(function(match){
									return match.group.startsWith("Groupe")
								})
								.OrderBy(function(match){
									return match.matchNum;
								})
								.ToArray();
				deferredObject.resolve({ Games:  result });
			}, function(data){
				deferredObject.resolve({ Games: null });
			});
			
			return deferredObject.promise;
		},
		getFinalGames: function(){
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            .get(BackendService.getBackEndURL() + 'score/games', config)
			.then(function(data){
				if(data.status === 200)
					result = $linq.Enumerable()
								.From(data.data)
								.Where(function(match){
									return (match.group.indexOf("Groupe") == -1);
								})
								.OrderBy(function(match){
									return match.matchNum;
								})
								.ToArray();
				deferredObject.resolve({ Games:  result });
			}, function(data){
				deferredObject.resolve({ Games: null });
			});
			
			return deferredObject.promise;
		}
	};
}
GamesService.$inject = ['$rootScope', '$http', '$q', '$linq', 'BackendService'];