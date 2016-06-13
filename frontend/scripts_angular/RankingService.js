/**
* Angular Service -> RankingService
* Expose method to get ranks of user
* getRanks() -> Return all ranks of community user
**/
angular.module('sports-predictions')
	.factory('RankingService', ['$rootScope', '$http', '$q','$linq', 'BackendService', function ($rootScope, $http, $q, $linq, BackendService) {

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
								result.message = 'Erreur dans la récupération des resultats !';
							deferredObject.resolve({ Ranks: result });
						}, function(data){
							result.message = 'Erreur dans la récupération des resultats !';
							deferredObject.resolve({ Ranks: result });
						});
						return deferredObject.promise;
					}
                }
        }]);