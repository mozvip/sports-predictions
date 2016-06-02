/**
* Angular Directive -> logoCEDisplay
* Attribute Directive for display logo CE if host is grand-est
**/
var logoCeDisplay = function ($location) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
		
		
            var showIfGE = function() {
                if($location.host() == 'grand-est'){
                    $(element).show();
                } else {
                    $(element).hide();
                }
            };
            showIfGE();
        }
    };
}
logoCeDisplay.$inject = ['$location'];

/**
* Angular Directive -> showWhenConnected
* Attribute Directive for display element when user is connected
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
* Angular Directive -> hideWhenConnected
* Attribute Directive for display element when user is disconnected
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
* Angular Directive -> pronostic
* Element Directive for display a match
**/
var pronostic = function(){
	return {
		restrict: 'E',
		templateUrl: '/partials-views/pronostic.html',
		scope: {
			match: "="
		},
		controller: function($scope, $location) {
									
			$scope.isPronosticable = function(match){
				return (match.dateTime <= new Date() && !match.done);
			}
			
			$scope.realScore = function(predictionScore, realScore){
				return predictionScore == realScore ? "realScoreGood" : "realScoreBad";
			}
			
			$scope.$watch('match.predictionHome_Score', function( newValue, oldValue ){
				
			   if(newValue == "" || newValue == undefined)
				   $scope.match.predictionHome_Score = 0;
			   else
				   $scope.match.predictionHome_Score = parseInt(newValue);
			  }, true);
			  
			$scope.$watch('match.predictionAway_Score', function( newValue, oldValue ){
			   if(newValue == "" || newValue == undefined)
				   $scope.match.predictionAway_Score = 0;
			   else
				   $scope.match.predictionAway_Score = parseInt(newValue);
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
					"Hongrie" : "HUN", 
					"Islande" : "ISL",
					"Portugal" : "POR"
				}
				return "flag-"+linking[nameTeam];
			};
        }
    };
}

/**
* Angular Directive -> pronostic
* Attribute Directive for compile tab content in pronostic view
**/
var compileHtml = function($sce, $parse, $compile) {
    return {
      restrict: 'A',
      compile: function ngBindHtmlCompile(tElement, tAttrs) {
        var ngBindHtmlGetter = $parse(tAttrs.compileHtml);
        var ngBindHtmlWatch = $parse(tAttrs.compileHtml, function getStringValue(value) {
          return (value || '').toString();
        });
        $compile.$$addBindingClass(tElement);

        return function ngBindHtmlLink(scope, element, attr) {
          $compile.$$addBindingInfo(element, attr.compileHtml);

          scope.$watch(ngBindHtmlWatch, function ngBindHtmlWatchAction() {

            element.html($sce.trustAsHtml(ngBindHtmlGetter(scope)) || '');
            $compile(element.contents())(scope);
          });
        };
      }
    };
 }
compileHtml.$inject = ['$sce', '$parse', '$compile'];


/**
* Angular Directive -> pronosticFinal
* Element Directive for display a final match
**/
var pronosticFinal = function(){
	return {
		restrict: 'E',
		templateUrl: '/partials-views/pronosticFinal.html',
		scope: {
			match: "="
		},
		controller: function($scope, $location) {
									
			$scope.matchNul = function(match){
				return match.predictionHome_Score != undefined && match.predictionAway_Score != undefined &&
				match.predictionHome_Score == match.predictionAway_Score;
			}
			
			$scope.isPronosticable = function(match){
				return (match.dateTime <= new Date() && !match.done);
			}
			
			$scope.realScore = function(predictionScore, realScore){
				return predictionScore == realScore ? "realScoreGood" : "realScoreBad";
			}
			
			$scope.$watch('match.home_winner', function( newValue, oldValue ){
				if(newValue != undefined){
					$scope.$parent.watchMatch($scope.match);
				}
			  }, true);
			
			$scope.$watch('match.predictionHome_Score', function( newValue, oldValue ){
				if(newValue != undefined)
				{
				   if(newValue == "")
						$scope.match.predictionHome_Score = 0;
				   else
						$scope.match.predictionHome_Score = parseInt(newValue);
					$scope.$parent.watchMatch($scope.match);
				}
			  }, true);
			  
			$scope.$watch('match.predictionAway_Score', function( newValue, oldValue ){
				if(newValue != undefined)
				{
				   if(newValue == "")
					   $scope.match.predictionAway_Score = 0;
				   else
					   $scope.match.predictionAway_Score = parseInt(newValue);
					$scope.$parent.watchMatch($scope.match);
				}
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
					"Hongrie" : "HUN", 
					"Islande" : "ISL",
					"Portugal" : "POR"
				}
				return nameTeam != undefined ? "flag-"+linking[nameTeam] : "flag-";
			};
        }
    };
}