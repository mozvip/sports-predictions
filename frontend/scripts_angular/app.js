angular.module('euro2016-predictions', ['ngRoute'])
 
.controller('LoginController', function($scope, $route, $routeParams, $location) {
    
    $scope.login = function() {
        alert( $scope.username );
    }
    
})
 
.controller('WelcomeController', function($scope, $route, $routeParams, $location) {
    
    $scope.message = "Hello World !";
    
})

.controller('SignupController', function($scope, $route, $routeParams, $location) {
})

.controller('InputScoresController', function($scope, $route, $routeParams, $location) {
    
    
    
})

.config(function($routeProvider, $locationProvider) {
  $routeProvider
    .when('/welcome', {
      controller:'WelcomeController',
      templateUrl:'/views/welcome.html'
    })
	.when('/pronostic', {
      controller:'PronosticController',
      templateUrl:'/views/pronostic.html'
    })
	.when('/ranks', {
      controller:'RankController',
      templateUrl:'/views/ranks.html'
    })
    .when('/login', {
      controller:'LoginController',
      templateUrl:'/views/login.html'
    })
    .when('/signup', {
      controller:'SignupController',
      templateUrl:'signup.html'
    })
    .when('/input-scores', {
      controller:'InputScoresController',
      templateUrl:'input-scores.html'
    })
	.when('/signup', {
		controller:'SignupController',
		templateUrl:'/views/signup.html'
	})
    .otherwise({
      redirectTo:'/welcome'
    });
})
 
;

