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
* Angular Controller -> SignupController  
* Sign up new user in euro2016 Predictor
* save()  
**/
var SignupController = function($scope, $route, $routeParams, $location, UserService, $uibModal, vcRecaptchaService) {
    
	// Implementation recaptcha
	$scope.response = null;
    $scope.widgetId = null;

    $scope.model = {
        key: '6LdiSh8TAAAAADLasplj5tGB390M6qBzH24vmXED'
    };
	
	$scope.newuser = {
        Login: '',
		Name: '',
        Password: '',
		login_available: null
    };

    $scope.setResponse = function (response) {
        $scope.response = response;
    };

    $scope.setWidgetId = function (widgetId) {
        $scope.widgetId = widgetId;
    };
	
	$scope.cbExpiration = function() {
		vcRecaptchaService.reload($scope.widgetId);
		$scope.response = null;
    };
				 			 	
	var openModal = function(){
		var modalInstance = $uibModal.open({
			  animation: true,
			  templateUrl: '/views/signupOK.html',
			  size: 'lg'
		});
	}	
	
	$scope.loginChanged = function(){
		   
		if($scope.newuser.Login != undefined && $scope.newuser.Login != ''){
			var regEmail = new RegExp('^[0-9a-z._-]+@{1}[0-9a-z.-]{2,}[.]{1}[a-z]{2,5}$','i');
			if(regEmail.test($scope.newuser.Login))
			{
				var res = UserService.loginAvailable($scope.newuser.Login);
				 res.then(function (result) {
					$scope.newuser.login_available = result.Result;
				});
			}
			else{
				$scope.newuser.login_available = false;
				$scope.returnRequest = 'Le login doit être une adresse mail valide !';
				return ;
			}
		}
		else
			$scope.newuser.login_available = true;
		
		if($scope.newuser.login_available)
			$scope.returnRequest = '';
		else
			$scope.returnRequest = 'Cette adresse mail est déjà utilisée !';
	}
				
    $scope.save = function() {
		if($scope.newuser.login_available)
		{
			var res = UserService.signup($scope.newuser.Login, $scope.newuser.Name, $scope.newuser.Password);
			
			 res.then(function (result) {
				if (result.User != null  && result.User.status === 500) 
					$scope.returnRequest = result.User.message;
				else if(result.User != null  && result.User.status === 204)
				{
					$location.path('/');
					openModal();
				}
			});
		}
		else
			alert('Cette adresse mail est incorrecte !');
    }
}
SignupController.$inject = ['$scope', '$route', '$routeParams', '$location', 'UserService', '$uibModal', 'vcRecaptchaService'];

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


var TestController = function($scope){
	Highcharts.chart('containerRank', {
		chart: {
			type: 'column',
			inverted: false
		},
		title: {
            text: 'Avancement de votre score',
            x: -20
        },
		xAxis: {
			categories: ['Match 1', 'Match 2', 'Match 3', 'Match 4', 'Match 5', 'Match 6', 
				'Match 7', 'Match 8', 'Match 9', 'Match 10', 'Match 11', 'Match 12']
		},
		exporting:{
			enabled: false
		},	
		credits: {
			enabled: false
		},
		series: [{
			name: 'Score',
			type:'line',
			data: [29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]
		},{
			name: 'Classement',
		type: 'column',
		data: [5,5,2,81,88,1,8,1,8,1,8,9],
		color: '#FF0000'
		}]
	});
	
	Highcharts.chart('containerStat', {
		chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
		},
		title: {
            text: 'Statistiques sur vos pronostiques',
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        },
		exporting:{
			enabled: false
		},	
		credits: {
			enabled: false
		},
        series: [{
            name: 'Brands',
            colorByPoint: true,
            data: [{
                name: 'Microsoft Internet Explorer',
                y: 56.33
            }, {
                name: 'Chrome',
                y: 24.03,
                sliced: true,
                selected: true
            }, {
                name: 'Firefox',
                y: 10.38
            }, {
                name: 'Safari',
                y: 4.77
            }, {
                name: 'Opera',
                y: 0.91
            }, {
                name: 'Proprietary or Undetectable',
                y: 0.2
            }]
        }]
	});
}
TestController.$inject = ['$scope'];



/**
* Angular Controller -> HomeController  
* Contains global data in application.
* logOut()
**/
var PronosticController = function($scope, $location, UserService, RankingService){

	$scope.Ranks = [];
	$scope.currentUser = UserService.getCurrentLogin();

	$scope.init = function(){
		var res = RankingService.get();
		res.then(function (result) {	
			if (result.Ranks.RanksData != undefined)
				$scope.Ranks = result.Ranks.RanksData;
			//else
				// Fenetre modale d'erreur dans laquelle on affiche le message result.Ranks.message
        });
	}
	
	 $scope.classCurrentUser= function(email){
		if(email === $scope.currentUser)
			return "currentUser";
		else 
			return "notCurrentUser";
    }
}
PronosticController.$inject = ['$scope','$location', 'UserService', 'RankingService'];