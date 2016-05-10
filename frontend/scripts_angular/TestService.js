var GamesService = function($rootScope, $http, $q, $location){
	return {
		getGroupGames: function(){
			// RAF : Install bower install angular-linq + Faire un filtre sur les phases de poules
			// Faire une directive pour les matchs simples au début
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            .get($location.protocol() + '://' + $location.host() + ':8080/games.json', config)
			.then(function(data){
				if(data.status === 200)
					result = data.data;
				deferredObject.resolve({ Games:  result });
			}, function(data){
				deferredObject.resolve({ Games: null });
			});
			
			return deferredObject.promise;
		},
		getFinalGames: function(){
			// TODO : TO BE IMPLEMENT
		}
	};
}