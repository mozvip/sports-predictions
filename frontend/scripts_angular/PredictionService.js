/**
* Angular Service -> PredictionService
* Expose method to save and get predictions for one user
* Controler which use this service : PronosticController
* savePredictions() -> Save predictions to user connected
**/
angular.module('sports-predictions')
	.factory('PredictionService', ['$rootScope', '$http', '$q', 'BackendService', function ($rootScope, $http, $q, BackendService) {

                return {
					savePredictions: function(predictions){
						var deferredObject = $q.defer();
						var result = {};
						var config = BackendService.getRequestConfig('application/json');
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
                }
        }]);