/**
* Angular Controller -> SignupController  
* Sign up new user in euro2016 Predictor
* save() * Try to create a new user
* loginChanged() * Use this method to control email availability
**/
angular.module('sports-predictions')
.controller('SignupController', ['$scope', '$route', '$routeParams', '$location', 'UserService', 'Notification',
function($scope, $route, $routeParams, $location, UserService, Notification) {
	// Implementation recaptcha
	$scope.response = null;

    $scope.model = {
        key: '6LchHVYUAAAAAEtUEWoMaLPkZBLFS71UYKXhU7eQ'
    };

	$scope.signUpOpened = false;
	
	$scope.newuser = {
        Login: '',
		Name: '',
        Password: ''
    };
				
    $scope.save = function() {
		var res = UserService.signup($scope.newuser.Login, $scope.newuser.Name, $scope.newuser.Password, $scope.response);
		
			res.then(function (result) {
			if (result.User != null  && result.User.status === 500) 
				Notification.error({message: result.User.message, title: 'Erreur lors de l\'enregistrement'});
			else if(result.User != null  && result.User.status === 204)
			{
				Notification.success({message: 'Merci de vous connecter à l\'application afin d\'accèder au concours de pronostics.', title: 'Enregistrement effectué'});
				$location.path('/');
			}
		});
    }
}]);