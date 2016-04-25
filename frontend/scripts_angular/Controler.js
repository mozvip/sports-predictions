/**
* Angular Controller -> LoginController  
* Login user in euro2016 Predictor
* login()
**/
var LoginController = function($scope, $route, $routeParams, $location, UserService) {
    
	$scope.User = {
        Login: '',
        Password: ''
    };
	
    $scope.login = function() {
		var res = UserService.login($scope.User.Login, $scope.User.Password);
		
		 res.then(function (result) {
            if (result.User != null  && result.User.status != 200) {
				$scope.returnRequest = result.User.message;
            }
			else
				$location.path('pronostic');
        });
    }   
}
LoginController.$inject = ['$scope', '$route', '$routeParams', '$location', 'UserService'];


/**
* Angular Controller -> HomeController  
* Contains global data in application. Access token per exemple.
**/
var HomeController = function($scope, $route, $routeParams){
	
}
HomeController.$inject = ['$scope', '$route', '$routeParams'];