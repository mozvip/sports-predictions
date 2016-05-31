/**
* Angular Service -> PredictionService
* Expose method to save and get predictions for one user
* Controler which use this service : PronosticController
* savePredictions() -> Save predictions to user connected
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
		}
	};
}
PredictionService.$inject = ['$rootScope', '$http', '$q', 'BackendService'];

/**
* Angular Service -> RankingService
* Expose method to get ranks of user
* getRanks() -> Return all ranks of community user
**/
var RankingService = function($rootScope, $http, $q, $linq, BackendService){
	
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
				if(data.status === 200){
					var dataOrder = $linq.Enumerable()
									.From(data.data.data)
									.OrderByDescending(function(rank){
										return rank.currentScore;
									})
									.ToArray();
					result.RanksData = dataOrder;
				}
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
RankingService.$inject = ['$rootScope', '$http', '$q', '$linq', 'BackendService'];