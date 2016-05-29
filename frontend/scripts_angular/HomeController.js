/**
* Angular Controller -> HomeController  
* First controller
* logOut() * Log out user connected
**/
angular.module('sports-predictions').controller('HomeController', ['$scope', '$location', 'UserService', 'Notification', function ($scope, $location, UserService, Notification) {

	$scope.logOut = function()	{
		UserService.logout();
		$location.path('/');
		Notification.info({message: 'Vous êtes maintenant déconnecté!', title: 'Déconnexion'});
	}
    
    $scope.isConnected = function() {
        return UserService.isConnected();
    }

    $scope.isAdmin = false;    
    UserService.getCurrentUser().then( function(userProfile) {
        $scope.isAdmin = userProfile.admin;
    });
	
}]);