/**
* Angular Controller -> HomeController  
* First controller
* logOut() * Log out user connected
**/
angular.module('sports-predictions')
	.controller('HomeController', ['$scope', '$location', 'UserService', 'BackendService', 'Notification', 'CommunityService',
		function ($scope, $location, UserService, BackendService, Notification, CommunityService) {

			$scope.community = undefined;
			CommunityService.getCommunity().then( function( community ) {
				$scope.community = community;
			});

			$scope.finalsAccessEnabled = function() {
				return $scope.community != undefined && $scope.community.finalsAccess != 'N';
			}

			$scope.groupsAccessEnabled = function() {
				return $scope.community != undefined && $scope.community.groupsAccess != 'N';
			}

			$scope.logOut = function () {
				UserService.logout();
				$location.path('/');
				Notification.info({ message: 'Vous êtes maintenant déconnecté!', title: 'Déconnexion' });
			}

			$scope.connected = function () {
				return UserService.isConnected();
			}

			$scope.admin = function () {
				return UserService.isAdmin();
			}

			$scope.currentRanking = function() {
				return UserService.getCurrentRanking();
			}

		}]);