/**
* Angular Controller -> HomeController  
* First controller
* logOut() * Log out user connected
**/
angular.module('sports-predictions')
.controller('HomeController', ['$scope', '$location', 'UserService', 'BackendService', 'Notification', 'RankingService', '$linq',
function ($scope, $location, UserService, BackendService, Notification, RankingService, $linq) {
    
	$scope.rank = 0;
	
    $scope.logOut = function()	{
		UserService.logout();
		$location.path('/');
		Notification.info({message: 'Vous êtes maintenant déconnecté!', title: 'Déconnexion'});
	}
  
    $scope.connected = function() {
        return $location.path() != '/login';
    }

    $scope.admin = function() {
        return UserService.isAdmin();
    }
	
	$scope.init = function(){
		RankingService.getRanks().then(
			function( result ){
				if (result.Ranks.RanksData != undefined){
					$scope.rank = $linq.Enumerable()
                            .From(result.Ranks.RanksData)
                            .FirstOrDefault({currentRanking: 0}, function (rank) {
                                return rank.email === UserService.getCurrentLogin();
                            }).currentRanking;
				}
				else
					Notification.error({message: result.Ranks.message, title: 'Erreur lors de la récupération du classement'});
				}, function( reponse ) {
					Notification.error( 'Position introuvable' );
				}
			)
	}
	
}]);