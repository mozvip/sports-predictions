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
var SignupController = function($scope, $route, $routeParams, $location, UserService, vcRecaptchaService, Notification) {
    
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
			var res = UserService.signup($scope.newuser.Login, $scope.newuser.Name, $scope.newuser.Password, $scope.response);
			
			 res.then(function (result) {
				if (result.User != null  && result.User.status === 500) 
					Notification.error({message: result.User.message, title: 'Erreur lors de l\'enregistrement'});
				else if(result.User != null  && result.User.status === 204)
				{
					Notification.success({message: 'Merci de vous connecter à l\'application afin d\'accèder au concours de pronostique.', title: 'Enregistrement effectué'});
					$location.path('/');
					openModal();
				}
			});
		}
		else
			alert('Cette adresse mail est incorrecte !');
    }
}
SignupController.$inject = ['$scope', '$route', '$routeParams', '$location', 'UserService', 'vcRecaptchaService', 'Notification'];

var PronosticController = function($scope, $location, UserService, PredictionService, GamesService, Notification, $linq){

	$scope.games = [];

	$scope.init = function(){
		var game, error = false;
		
		var res = GamesService.getGroupGames();
		res.then(function (result) {
			if(result.Games  != null && result.Games != undefined)
				$scope.games = result.Games;
			else{
				Notification.error({message: 'Les scores des matches n\'ont pu être récupérés. Un problème technique est à l\'origine du problème.', title: 'Erreur'});
				error = true;
			}
		});
		
		/*var r = PredictionService.getPredictions(UserService.getToken());
		r.then(function(result){
			alert(result.Predictions);
		});*/
		
		// TODO : Faire un merge avec les pronostics déjà saisis.
		// + Ajouter un boolean home_winner qui va servir pour départager les deux équipes.
		//Vos pronostics n\'ont pu être récupéré. Un problème technique est à l\'origine du problème
	}
}
PronosticController.$inject = ['$scope','$location', 'UserService', 'PredictionService', 'GamesService', 'Notification', '$linq'];

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
* Angular Controller -> RanksController  
* Contains global data in application.
* logOut()
**/
var RanksController = function($scope, $filter, $location, UserService, RankingService, Notification, $linq, NgTableParams){

	$scope.Ranks = [];
	$scope.currentUser = UserService.getCurrentLogin();
	$scope.yourScore = 0;

	$scope.logOut = function()	{
		UserService.logout();
		$location.path('/');
	}
			
	var getYourScore = function(){
		$scope.yourScore = $linq.Enumerable()
						.From($scope.Ranks)
						.Select(function(rank){
							return {'SCORE' : rank.currentScore, 'LOGIN': rank.email}
						})
						.FirstOrDefault(0, function(rank){
							return rank.LOGIN === $scope.currentUser;
						});
	}
	
	$scope.init = function(){
		
		var res = RankingService.getRanks();
		res.then(function (result) {	
			if (result.Ranks.RanksData != undefined){
				$scope.Ranks = result.Ranks.RanksData;
				$scope.RanksParams = new NgTableParams({
						page: 1,            // show first page
						count: 10,           // count per page
					}, {
						total: $scope.Ranks.length, // length of data
						getData: function($defer, params) {
							
						// use build-in angular filter
						var filteredData = params.filter() ?
							$filter('filter')($scope.Ranks, params.filter()) :
								$scope.Ranks;
						var orderedData = params.sorting() ?
							$filter('orderBy')(filteredData, params.orderBy()) :
								$scope.Ranks;
						params.total(orderedData.length); // set total for recalc pagination
						$defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						}
				});
			}
			else
				Notification.error({message: result.Ranks.message, title: 'Erreur lors de la récupération du classement'});
        });
		
		getYourScore();
	}	
}
RanksController.$inject = ['$scope', '$filter', '$location', 'UserService', 'RankingService', 'Notification', '$linq', 'NgTableParams'];


var DetailController = function($scope, $routeParams){
}
DetailController.$inject = ['$scope', '$routeParams'];


var ForgetController =   function($scope, $location, UserService, Notification){
	
	$scope.email = '';
	
	// Implementation recaptcha
	$scope.response = null;
    $scope.widgetId = null;

    $scope.model = {
        key: '6LdiSh8TAAAAADLasplj5tGB390M6qBzH24vmXED'
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
	
	$scope.forget = function(){
		UserService.forgetPassword($scope.email, $scope.response);
	}

}
ForgetController.$inject = ['$scope', '$location', 'UserService', 'Notification'];

var ChangePasswordController =   function($scope, $location, $routeParams, UserService, Notification){
	
	$scope.password1 = '';
	$scope.password2 = '';
	
	$scope.changePassword = function(){
		if ($scope.password1 === $scope.password2) {
			UserService.changePassword($routeParams.email, $routeParams.token, $scope.password1);
			$location('#/login');
			Notification.error("Votre mot de passe a été changé");
		} else {
			Notification.error("Les mots de passes ne concordent pas");
		}
	}

}
ChangePasswordController.$inject = ['$scope', '$location', '$routeParams', 'UserService', 'Notification'];