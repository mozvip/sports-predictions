/**
* Angular Service -> UserService
* Expose method to login, logout user,to know if user is connected and to sign up a new user.
* Controler which use this service : HeaderController (TO IMPLEMENT), LoginController, SignupController(TO IMPLEMENT)
* isConnected()
* getToken()
* login(login, password)
* logout()
* signup()
* loginAvailable()
* forgetPassword()
* changePassword()
**/
var UserService = function($rootScope, $http, $q, $cookies){
	return {
		isConnected: function(){
			return this.getToken() != null;
		},
		getToken: function(){
			return $cookies.get('SESSION_ID');
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
            .post('https://www.pronostics2016.com/api/user/signin', data, config)
			.then(function(data){
				userResult.status = data.status;
				if(data.status === 200)
					$cookies.put('SESSION_ID', data.data.authToken);
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
		signup: function(login, name, password){
			var deferredObject = $q.defer();
			var userResult = {};
			var data = 'email='+login+'&name='+name+'&password='+password;
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
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
			// TODO : TO BE IMPLEMENT
		},
		forgetPassword: function(login){
			// TODO : TO BE IMPLEMENT
		},
		changePassword: function(){
			// TODO : TO BE IMPLEMENT
		}
	};
}
UserService.$inject = ['$rootScope', '$http', '$q', '$cookies'];

var PredictionService = function($rootScope, $http, $q){
	return {
		save: function(){
			// TODO : TO BE IMPLEMENT
		},
		get: function(){
			// TODO : TO BE IMPLEMENT
		}
	};
}
PredictionService.$inject = ['$rootScope', '$http', '$q'];

var RankingService = function($rootScope, $http, $q){
	return {
		get: function(){
			// TODO : TO BE IMPLEMENT
		}
	};
}
RankingService.$inject = ['$rootScope', '$http', '$q'];