/**
* Angular Controller -> RanksController  
* Contains ranking data in application.
* init() * Get rankings for this community
**/
angular.module('sports-predictions')
.controller('RanksController', ['$scope', '$filter', '$location', 'UserService', 'RankingService', 'Notification', '$linq', 'NgTableParams',
function($scope, $filter, $location, UserService, RankingService, Notification, $linq, NgTableParams){

	$scope.Ranks = [];
	$scope.currentUser = UserService.getCurrentLogin();
	
	$scope.init = function(){
		
		var res = RankingService.getRanks();
		res.then(function (result) {	
			if (result.Ranks.RanksData != undefined){
				$scope.Ranks = result.Ranks.RanksData;
				$scope.RanksParams = new NgTableParams({
						page: 1,            // show first page
						count: 20,           // count per page
					}, {
						total: $scope.Ranks.length, // length of data
						getData: function($defer, params) {
							
						// use build-in angular filter
						var filteredData = params.filter() ?
							$filter('filter')($scope.Ranks, params.filter()) :
								$scope.Ranks;
						var orderedData = params.sorting() ?
							$filter('orderBy')(filteredData, params.orderBy()) :
								$scope.Ranks;
						params.total(orderedData.length); // set total for recalc pagination
						$defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						}
				});
			}
			else
				Notification.error({message: result.Ranks.message, title: 'Erreur lors de la récupération du classement'});
        });
	}	
	
	$scope.classCurentUser = function(rank){
		if(rank.email == UserService.getCurrentLogin())
			return "currentUser";
		else
			return "notCurrentUser";
	}
	
	$scope.whatArrow = function(rank){
		if(rank.currentRanking > rank.previousRanking)
			return "arrow-down";
		else if(rank.currentRanking < rank.previousRanking)
			return "arrow-up";
		else
			return "arrow-right";
	}
}]);