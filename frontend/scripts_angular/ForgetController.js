/**
 * Angular Controller -> LoginController  
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions')
.controller('ForgetController', ['$scope', '$route', '$routeParams', '$location', 'UserService', 'count', 'Notification',
function($scope, $location, UserService, Notification){

	$scope.email = '';
	
	// Implementation recaptcha
	$scope.response = null;
	
    $scope.model = {
        key: '6LdiSh8TAAAAADLasplj5tGB390M6qBzH24vmXED'
    };
	
	$scope.forget = function(){
		UserService.forgetPassword($scope.email, $scope.response).then(
			function( response ){
				if (response.status == 'success') {
					Notification.success( response.message );
					$location.path('/login');
				} else {
					Notification.error( response.message );
				}
			}
		)
	}
	
}]);