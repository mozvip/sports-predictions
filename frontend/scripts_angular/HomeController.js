/**
* Angular Controller -> HomeController  
* First controller
* logOut() * Log out user connected
**/
angular.module('sports-predictions')
	.controller('HomeController', ['$scope', '$location', 'UserService', 'BackendService', 'Notification',
		function ($scope, $location, UserService, BackendService, Notification) {

			$scope.logOut = function () {
				UserService.logout();
				$location.path('/');
				Notification.info({ message: 'Vous êtes maintenant déconnecté!', title: 'Déconnexion' });
			}

			$scope.connected = function () {
				return $location.path() != '/login';
			}

			$scope.admin = function () {
				return UserService.isAdmin();
			}

			$scope.currentRanking = 0;
			$scope.init = function () {
				UserService.getCurrentUser().then(function (userProfile) {
					$scope.currentRanking = userProfile.currentRanking;
				})
			}


		}]);