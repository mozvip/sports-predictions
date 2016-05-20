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
	
	$scope.images = [
    	'images/background-1.jpg',
    	'images/background-2.jpg',
    	'images/background-3.jpg',
		'images/background-4.jpg',
		'images/background-5.jpg',
		'images/background-6.jpg',
		'images/background-7.jpg'
  	];
}]);