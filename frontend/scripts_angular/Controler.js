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
* Angular Controller -> LoginController  
* Login user in euro2016 Predictor
* login()
**/
var SignupController = function($scope, $route, $routeParams, $location, UserService, $uibModal) {
    
	$scope.newuser = {
        Login: '',
		Name: '',
        Password: ''
    };
	
	var openModal = function(){
		var modalInstance = $uibModal.open({
			  animation: true,
			  templateUrl: '/views/signupOK.html',
			  //controller: 'ModalInstanceCtrl',
			  size: 'lg',
			  resolve: {
				email: function () {
				  return $scope.Login;
				}
			  }
		});
	}	
	
	$scope.init = function(){
		openModal();
	}
	
    $scope.save = function() {
		var res = UserService.signup($scope.newuser.Login, $scope.newuser.Name, $scope.newuser.Password);
		
		 res.then(function (result) {
            if (result.User != null  && result.User.status === 500) 
				$scope.returnRequest = result.User.message;
			else if(result.User != null  && result.User.status === 204)
				openModal();
        });
    }
}
SignupController.$inject = ['$scope', '$route', '$routeParams', '$location', 'UserService', '$uibModal'];

/**
* Angular Controller -> HomeController  
* Contains global data in application.
* logOut()
**/
var HomeController = function($scope, $location, UserService){
	$scope.logOut = function()	{
		UserService.logout();
		$location.path('/');
	}
}
HomeController.$inject = ['$scope','$location', 'UserService'];