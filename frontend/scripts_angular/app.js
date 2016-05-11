// Application du site euro2016Predictions
var euro2016Predictions = angular.module('euro2016Predictions', ['chieffancypants.loadingBar', 'ngRoute', 'ui.bootstrap', 'ngCookies', 'vcRecaptcha'])
                                    .config(function (cfpLoadingBarProvider) {
                                        cfpLoadingBarProvider.includeSpinner = false;
                                    });

/* Définition des controllers à l'application */
euro2016Predictions.controller('LoginController', LoginController);

euro2016Predictions.controller('SignupController', SignupController);
euro2016Predictions.controller('TestController', TestController);
euro2016Predictions.controller('RanksController', RanksController);
euro2016Predictions.controller('PronosticController', PronosticController);

/*  Interceptor des réponses HTTP  pour l'auth  */
euro2016Predictions.factory('AuthInterceptor', AuthInterceptor);

/* Définition des factory */ 
euro2016Predictions.factory('UserService', UserService);
euro2016Predictions.factory('PredictionService', PredictionService);
euro2016Predictions.factory('RankingService', RankingService);
euro2016Predictions.factory('GamesService', GamesService);
									

var config = function($routeProvider, $locationProvider, $httpProvider) {
  $routeProvider





	.when('/ranks', {
		templateUrl:'/views/ranks.html',
		authorized: true,
		controller: 'RanksController'
    })
	.when('/pronostic', {
		controller: 'PronosticController', 
		authorized: true, 
		templateUrl: '/views/pronostic.html'
	})
    .when('/login', {
		controller:'LoginController',
		templateUrl:'/views/login.html',
		authorized: false
    })



	.when('/signup', {
		controller: 'SignupController',
		templateUrl:'/views/signup.html',
		authorized: false
	})
	.when('profil', {
		templateUrl:'/views/profil.html',
		authorized: false
	})
	.when('forgetPassword', {
		templateUrl:'/views/forgetPassword.html',
		authorized: false
	})
	.when('/yourStats', {
		templateUrl:'/views/yourStats.html',
		authorized: true	
	})
    .otherwise({
      redirectTo:'/ranks'
    });
	
    $httpProvider.interceptors.push('AuthInterceptor');
    
    $locationProvider.html5Mode(false);
};
config.$inject = ['$routeProvider', '$locationProvider', '$httpProvider'];
euro2016Predictions.config(config);

euro2016Predictions.run(['$rootScope', '$location', 'UserService', function($rootScope, $location, UserService) {
    $rootScope.$on("$routeChangeStart", function (event, next, current) {
			if (next.authorized  && !UserService.isConnected())
				$location.url('login');
    });
}]);

/* Définition des directives */ 
euro2016Predictions.directive('showWhenConnected', showWhenConnected);
euro2016Predictions.directive('hideWhenConnected', hideWhenConnected);
euro2016Predictions.directive('userRank', userRank);

