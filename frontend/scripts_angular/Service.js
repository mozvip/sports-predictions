/**
* Angular Service -> UserService
* Expose method to login, logout user,to know if user is connected and to sign up a new user.
* Controler which use this service : HeaderController (TO IMPLEMENT), LOGIN CONTROLLER, SIGNUPCONTROLLER(TO IMPLEMENT)
* isConnected()
* login(login, password)
* logout()
**/
var UserService = function($rootScope, $http, $q)
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
			
			return deferredObject.promise;
		},
		logout: function(){
			
			$rootScope.$broadcast("connectionStateChanged");
		}
	};
}
UserService.$inject = ['$rootScope', '$http', '$q'];