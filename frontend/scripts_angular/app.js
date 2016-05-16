// Application du site euro2016Predictions
var euro2016Predictions = angular.module('euro2016Predictions', ['ngAnimate', 'ui.bootstrap', 'chieffancypants.loadingBar', 'ngRoute', 'ngCookies', 'vcRecaptcha', 'angular-linq', 'ui-notification', 'ngTable'])
	.config(function (cfpLoadingBarProvider) {
		cfpLoadingBarProvider.includeSpinner = false;
	})
	.factory('BackendService', function ($location) {
		return {
			getBackEndURL: function () {
				
				var port = $location.protocol() == 'https' ? '443' : '80';
				if ($location.host() == 'localhost') {
					// hack for local development without backend
					return 'https://test.pronostics2016.com/api/';
					// hack for development with local backend
					//return 'http://localhost:8080/api/';
				} else {
					return $location.protocol() + '://' + $location.host() + ':' + port + '/api/';
				}
			}
		}
	});

/* Définition des controllers à l'application */
euro2016Predictions.controller('LoginController', LoginController);

euro2016Predictions.controller('SignupController', SignupController);
euro2016Predictions.controller('TestController', TestController);
euro2016Predictions.controller('RanksController', RanksController);
euro2016Predictions.controller('PronosticController', PronosticController);
euro2016Predictions.controller('ForgetController', ForgetController);
euro2016Predictions.controller('ResetPasswordController', ResetPasswordController);
euro2016Predictions.controller('HomeController', HomeController);

/* Définition des factory */
euro2016Predictions.factory('UserService', UserService);
euro2016Predictions.factory('PredictionService', PredictionService);
euro2016Predictions.factory('RankingService', RankingService);
euro2016Predictions.factory('GamesService', GamesService);


var config = function ($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider


		.when('/ranks', {
			templateUrl: '/views/ranks.html',
			authorized: true,
			controller: 'RanksController'
		})
		.when('/pronostic', {
			controller: 'PronosticController',
			authorized: true,
			templateUrl: '/views/pronostic.html'
		})
		.when('/login', {
			controller: 'LoginController',
			templateUrl: '/views/login.html',
			authorized: false
		})
		.when('/sign-up', {
			controller: 'SignupController',
			templateUrl: '/views/signup.html',
			authorized: false
		})
		.when('/user-profile', {
			templateUrl: '/views/user-profile.html',
			authorized: true,
			controller: UserProfileController
		})
		.when('/forget-password', {
			templateUrl: '/views/forgetPassword.html',
			authorized: false,
			controller: 'ForgetController'
		})
		.when('/forget-password/:email/:token', {
			templateUrl: '/views/changePassword.html',
			controller: 'ResetPasswordController',
			authorized: false
		})
		.when('/your-stats', {
			templateUrl: '/views/yourStats.html',
			authorized: true
		})
		.otherwise({
			redirectTo: '/ranks'
		});

    $locationProvider.html5Mode(false);
};
config.$inject = ['$routeProvider', '$locationProvider', '$httpProvider'];
euro2016Predictions.config(config);

euro2016Predictions.run(['$rootScope', '$location', 'UserService', function ($rootScope, $location, UserService) {
    $rootScope.$on("$routeChangeStart", function (event, next, current) {
		if (next.authorized && !UserService.isConnected())
			$location.url('login');
    });
}]);

/* Définition des directives */
euro2016Predictions.directive('showWhenConnected', showWhenConnected);
euro2016Predictions.directive('hideWhenConnected', hideWhenConnected);
euro2016Predictions.directive('userRank', userRank);
euro2016Predictions.directive('pronostic', pronostic);
euro2016Predictions.directive('compileHtml', compileHtml);
