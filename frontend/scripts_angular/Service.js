/**
* Angular Service -> UserService
* Expose method to login, logout user,to know if user is connected and to sign up a new user.
* Controler which use this service : HeaderController (TO IMPLEMENT), LoginController, SignupController(TO IMPLEMENT)
* isConnected() -> Can get is user is connected
* getToken() -> Return user access token
* getCurrentLogin() -> Return current user login
* login(login, password) -> Log user with login/password
* logout() -> Log out current user
* signup() -> Sign up new user
* loginAvailable() -> Can know if login is available for sign up
* forgetPassword() ->  User forgot password : TO TEST
* changePassword() -> Can change password for current user : TODO
**/
var UserService = function($rootScope, $http, $q, $cookies, $location){
	return {
		isConnected: function(){
			return this.getToken() != null;
		},
		getToken: function(){
			return $cookies.get('SESSION_ID');
		},
		getCurrentLogin: function(){
			return $cookies.get('SESSION_CURRENT_LOGIN');
		},
		login: function(login, password){
			var deferredObject = $q.defer();
			var userResult ={
				message: ''
			};
			var data = 'email='+login+'&password='+password;
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
            //.post($location.protocol() + '://' + $location.host() + '/api/user/signin', data, config)
			.post('https://www.pronostics2016.com/api/user/signin', data, config)
			.then(function(data){
				userResult.status = data.status;
				if(data.status === 200){
					$cookies.put('SESSION_ID', data.data.authToken);
					$cookies.put('SESSION_CURRENT_LOGIN', login);
				}
				else
					userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
				
				deferredObject.resolve({ User:  userResult });
				$rootScope.$broadcast("connectionStateChanged");
			}, function(data){
				userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
				deferredObject.resolve({ User: userResult });
			});
			
			return deferredObject.promise;
		},
		logout: function(){
			$cookies.remove('SESSION_ID');
			$rootScope.$broadcast("connectionStateChanged");
		},
		signup: function(login, name, password, captcha){
			var deferredObject = $q.defer();
			var userResult = {};
			var data = 'email='+login+'&name='+name+'&password='+password+'&g-recaptcha-response='+captcha;
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
            //.post($location.protocol() + '://' + $location.host() + '/api/user/create', data, config)
			.post('https://www.pronostics2016.com/api/user/create', data, config)
			.then(function(data){
				userResult.status = data.status;
				if(data.status === 500)
					userResult.message = 'Erreur utilisateur déjà existant !';	
				deferredObject.resolve({ User:  userResult });
			}, function(data){
				userResult.message = "Erreur lors de l'enregistrement de cet utilisateur  !";
				deferredObject.resolve({ User: userResult });
			});
			
			return deferredObject.promise;
		},
		loginAvailable: function(login){	
			var deferredObject = $q.defer();
			var result = true;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            //.get($location.protocol() + '://' + $location.host() + '/api/user/availability?email='+login,config)
			.get('https://www.pronostics2016.com/api/user/availability?email='+login,config)
			.then(function(data){
				deferredObject.resolve({ Result:  data.data });
			}, function(data){
				deferredObject.resolve({ Result:  false });
			});
			
			return deferredObject.promise;
		},
		forgetPassword: function(login){
			// A TESTER LORSQUE TOUT EST OK
			var deferredObject = $q.defer();
			var data = 'email='+login;
			var message = '';
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
            //.post($location.protocol() + '://' + $location.host() + '/api/user/forget-password', data, config)
			.post('https://www.pronostics2016.com/api/user/forget-password', data, config)
			.then(function(data){
				userResult.status = data.status;
				if(data.status === 200)
					message = 'Un email vous a été envoyé permettant de réinitialiser votre mot de passe !';
				else
					message = 'Cette adresse mail est inconnue !';
				deferredObject.resolve({ Return: message });
			}, function(data){
				message = 'Erreur : identifiant incorrect !';
				deferredObject.resolve({ Return: message });
			});
			return deferredObject.promise;
		},
		changePassword: function(login, newPassword, token){
			// TODO : TO BE IMPLEMENT
		}
	};
}
UserService.$inject = ['$rootScope', '$http', '$q', '$cookies', '$location'];

var PredictionService = function($rootScope, $http, $q, $location){
	return {
		savePredictions: function(){
			// TODO : TO BE IMPLEMENT
		},
		getPredictions: function(token){
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json',
				'Authorization': 'Basic ' + token}
			};
			$http
            //.get($location.protocol() + '://' + $location.host() + '/api/user/predictions', config)
			.get('https://www.pronostics2016.com/api/user/predictions', config)
			.then(function(data){
				result.status = data.status;
				if(data.status === 200)
					result = data.match_predictions_attributes;
				deferredObject.resolve({ Predictions:  result });
			}, function(data){
				deferredObject.resolve({ Predictions: null });
			});
			
			return deferredObject.promise;
		}
	};
}
PredictionService.$inject = ['$rootScope', '$http', '$q', '$location'];

/**
* Angular Service -> RankingService
* Expose method to get ranks of user
* get() -> Return all ranks of community user
* getYourRanking(login) -> Return ranks of current user
**/
var RankingService = function($rootScope, $http, $q, $location){
	return {
		getRanks: function(){
			var deferredObject = $q.defer();
			var result= {};
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            //.get($location.protocol() + '://' + $location.host() + '/api/user/rankings', config)
			.get('https://www.pronostics2016.com/api/user/rankings', config)
			.then(function(data){
				result.status = data.status;
				if(data.status === 200)
					result.RanksData = data.data.data;
				else
					result.message = 'Erreur dans la récupération des résultats !';
				deferredObject.resolve({ Ranks: result });
			}, function(data){
				result.message = 'Erreur dans la récupération des résultats !';
				deferredObject.resolve({ Ranks: result });
			});
			return deferredObject.promise;
		}
	};
}
RankingService.$inject = ['$rootScope', '$http', '$q', '$location'];


var GamesService = function($rootScope, $http, $q, $location, $linq){
	return {
		getGroupGames: function(){
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            .get($location.protocol() + '://' + $location.host() + ':8080/games.json', config)
			.then(function(data){
				if(data.status === 200)
					result = $linq.Enumerable()
								.From(data.data)
								.Where(function(match){
									return match.group.startsWith("Groupe")
								})
								.OrderBy(function(match){
									return match.matchNum;
								})
								.ToArray();
				deferredObject.resolve({ Games:  result });
			}, function(data){
				deferredObject.resolve({ Games: null });
			});
			
			return deferredObject.promise;
		},
		getFinalGames: function(){
			// TODO : TO BE IMPLEMENT
		}
	};
}
GamesService.$inject = ['$rootScope', '$http', '$q', '$location', '$linq'];