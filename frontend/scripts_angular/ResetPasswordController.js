/**
* Angular Controller -> ResetPasswordController  
* Forget password in application.
* changePassword() * Change password to user connected
**/
angular.module('sports-predictions')
.controller('ResetPasswordController', ['$scope', '$location', '$routeParams', 'UserService', 'Notification', 
function($scope, $location, $routeParams, UserService, Notification){

	$scope.password1 = '';
	$scope.password2 = '';
	
	$scope.changePassword = function(){
		if ($scope.password1 === $scope.password2) {
			
			UserService.changePassword($routeParams.email, $routeParams.token, $scope.password1).then(
				function( response ){
					if (response.status == 'success') {
						Notification.success( response.message );
						$location.path('/login');
					} else {
						Notification.error( response.message );
					}
				}
			)

		} else {
			Notification.error("Les mots de passe ne concordent pas");
		}
	}
}]);