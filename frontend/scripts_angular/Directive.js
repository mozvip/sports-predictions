/**
* TO COMMENT
**/
var showWhenConnected = function (UserService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var showIfConnected = function() {
                if(UserService.isConnected()) {
                    $(element).show();
                } else {
                    $(element).hide();
                }
            };
            showIfConnected();
            scope.$on('connectionStateChanged', showIfConnected);
        }
    };
}
showWhenConnected.$inject = ['UserService'];

/**
* TO COMMENT
**/
var hideWhenConnected = function (UserService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var hideIfConnected = function() {
                if(UserService.isConnected()) {
                    $(element).hide();
                } else {
                    $(element).show();
                }
            };
            hideIfConnected();
            scope.$on('connectionStateChanged', hideIfConnected);
        }
    };
}
hideWhenConnected.$inject = ['UserService'];

/**
* TO COMMENT
**/
var userRank = function(UserService){
    return {
        restrict: 'E',
        templateUrl: '/partials-views/user-rank.html',
		scope: {
            user: "="
        },
		controller: function($scope, UserService) {
            $scope.classCurrentUser = function(email) {
				if(email === UserService.getCurrentLogin())
					return 'currentUser';
				else 
					return 'notCurrentUser';
            };
        }
    };
}
userRank.$inject = ['UserService'];

/**
* TO COMMENT
**/
var pronostic = function(){
	return {
		restrict: 'E',
		templateUrl: '/partials-views/pronostic.html',
		scope: {
			match: "="
		},
		controller: function($scope, $location) {
			
			$scope.linkMatch = function(match){
				$location.path('detail/'+match.matchNum);
			}
						
			$scope.isPronosticable = function(match){
				return (match.dateTime <= new Date() && !match.done);
			}
			
			$scope.$watch('match.homeScore', function( newValue, oldValue ){
			   if(newValue == "")
				   $scope.match.homeScore = 0;
			   else
				   $scope.match.homeScore = parseInt(newValue);
			  }, true);
			  
			$scope.$watch('match.awayScore', function( newValue, oldValue ){
			   if(newValue == "")
				   $scope.match.awayScore = 0;
			   else
				   $scope.match.awayScore = parseInt(newValue);
			  }, true);
			
			$scope.classFlagTeam = function(nameTeam) {
				var linking = {
					"France" : "FRA",
					"Allemagne" : "GER",
					"Albanie" : "ALB",
					"Autriche" : "AUT",
					"Belgique" : "BEL", 
					"Roumanie" : "ROU",
					"Suisse" : "SUI", 
					"Angleterre" : "ENG", 
					"Russie" : "RUS", 
					"Slovaquie" : "SVK",
					"Galles" : "WAL",
					"Irlande Du Nord" : "NIR", 
					"Pologne" : "POL", 
					"Ukraine" : "UKR",
					"Croatie" :  "CRO",
					"Rep. Tcheque"  : "CZE",
					"Espagne" : "ESP",
					"Turquie" : "TUR",
					"Italie" : "ITA", 
					"Irlande" : "IRL", 
					"Suede" : "SWE", 
					"Autriche" : "AUT",
					"Hongrie" : "HUN", 
					"Islande" : "ISL",
					"Portugal" : "POR"
				}
				return "flag-"+linking[nameTeam];
			};
        }
    };
}