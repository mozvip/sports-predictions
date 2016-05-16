/**
* Angular Controller -> LoginController  
* Login user in euro2016 Predictor
* login() * Try to log in user input
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
* save() * Try to create a new user
* loginChanged() * Use this method to control email availability
* setResponse(), setWidgetId() et cbExpiration() * Recaptcha implementation
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

/**
* Angular Controller -> PronosticController  
* Save and Get prediction data in euro2016 Predictor
* init() * Get data prediction with games data
* submitPronostic() * Save predictions
**/
var PronosticController = function($scope, $location, UserService, PredictionService, GamesService, Notification, $linq){

	$scope.games = [];
	
	$scope.tabs = [{ title:'Groupe A', content:'<div role="tabpanel" class="tab-pane active" id="group-a"><div class="group-container"><h3 class="groupName">Groupe A</h3><pronostic ng-repeat="match in games | filter:\'Groupe A\'" match="match"></pronostic></div>	</div>' },
		{ title:'Groupe B', content:'<div role="tabpanel" class="tab-pane active" id="group-b"><div class="group-container"><h3 class="groupName">Groupe B</h3><pronostic ng-repeat="match in games | filter:\'Groupe B\'" match="match"></pronostic></div>	</div>' },
		{ title:'Groupe C', content:'<div role="tabpanel" class="tab-pane active" id="group-c"><div class="group-container"><h3 class="groupName">Groupe C</h3><pronostic ng-repeat="match in games | filter:\'Groupe C\'" match="match"></pronostic></div>	</div>' },
		{ title:'Groupe D', content:'<div role="tabpanel" class="tab-pane active" id="group-d"><div class="group-container"><h3 class="groupName">Groupe D</h3><pronostic ng-repeat="match in games | filter:\'Groupe D\'" match="match"></pronostic></div>	</div>' },
		{ title:'Groupe E', content:'<div role="tabpanel" class="tab-pane active" id="group-e"><div class="group-container"><h3 class="groupName">Groupe E</h3><pronostic ng-repeat="match in games | filter:\'Groupe E\'" match="match"></pronostic></div>	</div>' },
		{ title:'Groupe F', content:'<div role="tabpanel" class="tab-pane active" id="group-f"><div class="group-container"><h3 class="groupName">Groupe F</h3><pronostic ng-repeat="match in games | filter:\'Groupe F\'" match="match"></pronostic></div>	</div>' }];


	$scope.init = function(){
		var error = false;
		$scope.games = [];
		var res = GamesService.getGroupGames();
		res.then(function (result) {
			if(result.Games  != null && result.Games != undefined)
				$scope.games = result.Games;
			else{
				Notification.error({message: 'Les scores des matches n\'ont pu être récupérés. Un problème technique est à l\'origine du problème.', title: 'Erreur'});
				error = true;
			}
		});
		
		if(error){
			$scope.games = [];
			return ;
		}
			
		PredictionService.getPredictions(UserService.getToken())
		.then(function(result){
			if(result.Predictions.status === 200 && result.Predictions.Predictions != undefined)
			{
				$linq.Enumerable()
					.From($scope.games)
					.ForEach(function(element){
						var gameID = element.matchNum;
						var prediction = $linq.Enumerable()
							.From(result.Predictions.Predictions)
							.FirstOrDefault(null, function(prediction){
								return prediction.match_id === gameID;
							});
							
						if(prediction != null)
						{
							element.predictionHome_Score = prediction.home_score;
							element.predictionAway_Score = prediction.away_score;
						}
						else
						{
							element.predictionHome_Score = 0;
							element.predictionAway_Score = 0;
						}
						element.home_winner = false;
					});
			}
			else{
				Notification.error({message: 'Les scores des matches n\'ont pu être récupérés. Un problème technique est à l\'origine du problème.', title: 'Erreur'});
				error = true;
			}
		});
		
		if(error){
			$scope.games = [];
			return ;
		}
	}
	
	$scope.submitPronostic = function(){
		var community = $location.host() == 'localhost' ? 'test' : $location.host();
		var predictions = [];
		
		$linq.Enumerable()
			.From($scope.games)
			.ForEach(function(element){
				predictions.push(createPrediction(community, element));
		});
		
		PredictionService.savePredictions(UserService.getToken(), {match_predictions_attributes: predictions})
		.then(function(result){
			if(result.status != 200)
				Notification.error('ERROR');
		});
	}  
	
	var createPrediction = function(host, game){
		return {
			community : host, 
			email: UserService.getCurrentLogin(), 
			match_id: game.matchNum, 
			away_score: game.predictionAway_Score, 
			away_team_id: game.awayTeam,
			home_score: game.predictionHome_Score, 
			home_tean_id: game.homeTeam,
			home_winner: game.home_winner ? game.homeTeam : game.awayTeam };
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
* Contains ranking data in application.
* init() * Get rankings for this community
**/
var RanksController = function($scope, $filter, $location, UserService, RankingService, Notification, $linq, NgTableParams){

	$scope.Ranks = [];
	$scope.currentUser = UserService.getCurrentLogin();
	
	$scope.init = function(){
		
		var res = RankingService.getRanks();
		res.then(function (result) {	
			if (result.Ranks.RanksData != undefined){
				$scope.Ranks = result.Ranks.RanksData;
				$scope.RanksParams = new NgTableParams({
						page: 1,            // show first page
						count: 20,           // count per page
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
	}	
	
	$scope.classCurentUser = function(rank){
		if(rank.email == UserService.getCurrentLogin())
			return "currentUser";
		else
			return "notCurrentUser";
	}
}
RanksController.$inject = ['$scope', '$filter', '$location', 'UserService', 'RankingService', 'Notification', '$linq', 'NgTableParams'];

/**
* Angular Controller -> ForgetController  
* Forget password in application.
* forget() * Call UserService.forgetPassword() to send email
* setResponse(), setWidgetId() et cbExpiration() * Recaptcha implementation
**/
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
		UserService.forgetPassword($scope.email, $scope.response).then(
			function( response ){
				if (response.status == 'success') {
					Notification.success( response.message );
					$location.path('/login');
				} else {
					Notification.error( response.message );
				}
			}
		)
	}
	
	$scope.classCurrentUser = function(email) {
	// TO FINISH
		if(email === UserService.getCurrentLogin())
			return 'currentUser';
		else 
			return 'notCurrentUser';
    };
}
ForgetController.$inject = ['$scope', '$location', 'UserService', 'Notification'];

/**
* Angular Controller -> ResetPasswordController  
* Forget password in application.
* changePassword() * Change password to user connected
**/
var ResetPasswordController = function($scope, $location, $routeParams, UserService, Notification){
	
	$scope.password1 = '';
	$scope.password2 = '';
	
	$scope.changePassword = function(){
		if ($scope.password1 === $scope.password2) {
			
			UserService.changePassword($routeParams.email, $routeParams.token, $scope.password1).then(
				function( response ){
					if (response.status == 'success') {
						Notification.success( response.message );
						$location.path('/login');
					} else {
						Notification.error( response.message );
					}
				}
			)

		} else {
			Notification.error("Les mots de passe ne concordent pas");
		}
	}

}
ResetPasswordController.$inject = ['$scope', '$location', '$routeParams', 'UserService', 'Notification'];

var UserProfileController = function($scope, $location, $routeParams, UserService, Notification){
	
	$scope.password1 = '';
	$scope.password2 = '';
	
	$scope.changePassword = function(){
		if ($scope.password1 === $scope.password2) {
			
			UserService.changePassword($routeParams.email, $routeParams.token, $scope.password1).then(
				function( response ){
					if (response.status == 'success') {
						Notification.success( response.message );
						$location.path('/login');
					} else {
						Notification.error( response.message );
					}
				}
			)

		} else {
			Notification.error("Les mots de passe ne concordent pas");
		}
	}

}
UserProfileController.$inject = ['$scope', '$location', '$routeParams', 'UserService', 'Notification'];

/**
* Angular Controller -> HomeController  
* First controller
* logOut() * Log out user connected
**/
var HomeController = function($scope, $location, UserService, Notification){

	$scope.logOut = function()	{
		UserService.logout();
		$location.path('/');
		Notification.info({message: 'Vous êtes maintenant déconnecté!', title: 'Déconnexion'});
	}
}
HomeController.$inject = ['$scope', '$location', 'UserService', 'Notification'];