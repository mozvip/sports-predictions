
var team = function() {
    return {
        restrict: 'E',
        scope: {
        	name: "=",
			position: "@"
        },
        templateUrl: '/partials-views/team.html',
        controller: function($scope) {

            $scope.classFlagTeam = function() {
                var linking = {
                    "France" : "fr",
                    "Allemagne" : "de",
                    "Albanie" : "al",
                    "Arabie Saoudite" : "sa",
                    "Autriche" : "AUT",
                    "Australie" : "au",
                    "Belgique" : "be",
                    "Brésil" : "br",
                    "Costa Rica" : "cr",
                    "Croatie" : "hr",
                    "Danemark" : "dk",
                    "Pérou" : "pe",
                    "Maroc" : "ma",
                    "Russie" : "ru",
                    "Uruguay" : "uy",
                    "Panama": "pa",
                    "Tunisie": "tn",
                    "Japon": "jp",
                    "Pologne": "pl",
                    "Colombie": "co",
                    "Argentine" : "ar",
                    "Egypte" : "eg",
                    "Iran" : "ir",
                    "Roumanie" : "ROU",
                    "Angleterre" : "gb-eng",
                    "Slovaquie" : "SVK",
                    "Galles" : "WAL",
                    "Irlande Du Nord" : "NIR",
                    "Ukraine" : "UKR",
                    "Rep. Tcheque"  : "CZE",
                    "Corée du Sud"  : "kr",
                    "Espagne" : "es",
                    "Nigéria"  : "ng",
                    "Turquie" : "TUR",
                    "Italie" : "ITA",
                    "Irlande" : "IRL",
                    "Mexique" : "mx",
                    "Serbie" : "rs",
                    "Sénégal" : "sn",
                    "Suède" : "se",
                    "Suisse" : "ch",
                    "Hongrie" : "hu",
                    "Islande" : "is",
                    "Portugal" : "pt"
                }
                return "flag-icon flag-icon-"+linking[$scope.name];
            };
        }
    };
}


/**
* Angular Directive -> pronostic
* Element Directive for display a match
**/
var pronostic = function(){
	return {
		restrict: 'E',
		templateUrl: '/partials-views/pronostic.html',
		scope: {
			match: "=",
			access: "="
		},
		controller: function($scope, $location) {
									
			$scope.isPronosticable = function(match){
				return new Date(match.dateTime) >= new Date() && $scope.access;
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
					"France" : "fr",
					"Allemagne" : "de",
					"Albanie" : "al",
                    "Arabie Saoudite" : "sa",
					"Autriche" : "AUT",
                    "Australie" : "au",
					"Belgique" : "be",
                    "Brésil" : "br",
                    "Costa Rica" : "cr",
                    "Croatie" : "hr",
                    "Danemark" : "dk",
                    "Pérou" : "pe",
                    "Maroc" : "ma",
                    "Russie" : "ru",
                    "Uruguay" : "uy",
					"Panama": "pa",
					"Tunisie": "tn",
                    "Japon": "jp",
                    "Pologne": "pl",
                    "Colombie": "co",
                    "Argentine" : "ar",
                    "Egypte" : "eg",
                    "Iran" : "ir",
					"Roumanie" : "ROU",
					"Angleterre" : "gb-eng",
					"Slovaquie" : "SVK",
					"Galles" : "WAL",
					"Irlande Du Nord" : "NIR", 
					"Ukraine" : "UKR",
					"Rep. Tcheque"  : "CZE",
                    "Corée du Sud"  : "kr",
					"Espagne" : "es",
                    "Nigéria"  : "ng",
					"Turquie" : "TUR",
					"Italie" : "ITA", 
					"Irlande" : "IRL",
                    "Mexique" : "mx",
                    "Serbie" : "rs",
                    "Sénégal" : "sn",
					"Suède" : "se",
                    "Suisse" : "ch",
					"Hongrie" : "hu",
					"Islande" : "is",
					"Portugal" : "pt"
				}
				return "flag-icon flag-icon-"+linking[nameTeam];
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
			match: "=match",
			access: "=access"
		},
		controller: function($scope, $location) {
			
			$scope.matchNul = function(){
				return $scope.match.predictionHome_Score != undefined && $scope.match.predictionAway_Score != undefined &&
					$scope.match.predictionHome_Score == $scope.match.predictionAway_Score;
			}
			
			$scope.isPronosticable = function(){
				return $location.path() == '/pronostic-final-delegate' || ($scope.access=='W' && new Date(match.dateTime) >= new Date() && !match.done);
			}

			$scope.displayFinalScore = function() {
				return $location.path() != '/pronostic-final-delegate' && $scope.match.done;
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
				   else{
						$scope.match.predictionHome_Score = parseInt(newValue);
						if($scope.match.predictionHome_Score > $scope.match.predictionAway_Score)
							$scope.match.home_winner  = true;
				   }
					$scope.$parent.watchMatch($scope.match);
				}
			  }, true);
			  
			$scope.$watch('match.predictionAway_Score', function( newValue, oldValue ){
				if(newValue != undefined)
				{
				   if(newValue == "")
					   $scope.match.predictionAway_Score = 0;
				   else{
					   $scope.match.predictionAway_Score = parseInt(newValue);
					   if($scope.match.predictionHome_Score < $scope.match.predictionAway_Score)
							$scope.match.home_winner  = false;
				   }
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


/**
* Angular Directive -> pronosticFinal
* Element Directive for display a final match
**/
var backButton = function(){
    return {
      restrict: 'A',

      link: function(scope, element, attrs) {
        element.bind('click', goBack);

        function goBack() {
			window.history.back();
			//history.back();
			scope.$apply();
        }
      }
    }
};	