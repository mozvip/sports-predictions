angular.module('euro2016-predictions', [/*'chieffancypants.loadingBar',*/ 'ngRoute'])
		/*.config(function (cfpLoadingBarProvider) {
			cfpLoadingBarProvider.includeSpinner = true;
        })*/
 
.controller('LoginController', ['$scope', '$route', '$routeParams', '$location', 'UserService',function($scope, $route, $routeParams, $location, UserService) {
    
	$scope.User = {
        Login: '',
        Password: ''
    };
	
    $scope.login = function() {
		//alert( $scope.User.Login + " " +  $scope.User.Password);
		var res = UserService.login($scope.User.Login, $scope.User.Password);
		
		 res.then(function (result) {
            if (result.authToken != null) {
                $scope.auth = result.authToken;
            }
        });
    }
    
}])
.service('UserService', ['$rootScope', '$http', '$q', function($rootScope, $http, $q)
{
	return {
		isConnected: function(){
			return false;
		},
		login: function(login, password){
			var deferredObject = $q.defer();
			
			var userResult ={
				User: null,
				message: ''
			}
			
			var data = {
				email:login,
				password:password
			};

			var config = {
				headers : {
					'Accept' : 'application/json',
					'Content-Type' : 'application/x-www-form-urlencoded'
					//'Access-Control-Allow-Origin': '*'
					},
				dataType: 'script'
			};

			$http
            .post('https://www.pronostics2016.com/api/user/signin', data, config)
			.then(function(data){
				userResult.User = data;
				userResult.message = 'Authentification OK';
				deferredObject.resolve({ User:  userResult });
				$rootScope.$broadcast("connectionStateChanged");
			}, function(data){
				userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
				deferredObject.resolve({ User: userResult });
			});
			/*.success(function (data) {
				userResult.User = data;
				deferredObject.resolve({ User:  userResult });
				$rootScope.$broadcast("connectionStateChanged");
			})
			.error(function(data){
				userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
				deferredObject.resolve({ User: userResult });
			});*/
			
			return deferredObject.promise;
		},
		logout: function(){
			
			$rootScope.$broadcast("connectionStateChanged");
		}
	};
}])
.config(function($routeProvider, $locationProvider, $httpProvider) {
  $routeProvider
    .when('/welcome', {
      templateUrl:'/views/welcome.html',
	  authorized: false
    })
	.when('/pronostic', {
      templateUrl:'/views/pronostic.html',
	  authorized: true
    })
	.when('/ranks', {
      templateUrl:'/views/ranks.html',
	  authorized: false
    })
    .when('/login', {
      controller:'LoginController',
      templateUrl:'/views/login.html',
	  authorized: false
    })
    .when('/input-scores', {
      templateUrl:'input-scores.html'
    })
	.when('/signup', {
		templateUrl:'/views/signup.html',
	  authorized: false
	})
    .otherwise({
      redirectTo:'/welcome'
    });
	
	$httpProvider.interceptors.push(function($q, $location){
		return { 'responseError' : function(rejection){
			if(rejection.status === 401)
				$location.url('login');
			}
		};
	});
})
.run(['$rootScope', '$location', 'UserService', function($rootScope, $location, UserService) {
    $rootScope.$on("$routeChangeStart", function (event, next, current) {
			if (next.authorized  && !UserService.isConnected()) {
				$location.url("login");
        }
    });
}]);

