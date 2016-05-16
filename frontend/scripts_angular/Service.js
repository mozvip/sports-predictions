/**
* Angular Service -> UserService
* Expose method to login, logout user,to know if user is connected and to sign up a new user.
* Controler which use this service : HomeController, LoginController, SignupController, PronosticController, RanksController, ForgetController, UserProfileController
* isConnected() -> Can get is user is connected
* getToken() -> Return user access token
* getCurrentLogin() -> Return current user login
* login(login, password) -> Log user with login/password
* logout() -> Log out current user
* signup() -> Sign up new user
* loginAvailable() -> Can know if login is available for sign up
* forgetPassword() ->  User forgot password
* changePassword() -> Can change password for current user
**/
var UserService = function($rootScope, $http, $q, $cookies, BackendService){
	
	var currentUser;
	
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
		currentUser: function() {
			return currentUser;
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
			.post(BackendService.getBackEndURL() + 'user/signin', data, config)
			.then(function(response){
				userResult.status = response.status;
				currentUser = response.data;
				$cookies.put('SESSION_ID', response.data.authToken);
				$cookies.put('SESSION_CURRENT_LOGIN', login);
				deferredObject.resolve({ User:  userResult });
				$rootScope.$broadcast("connectionStateChanged");
			}, function(response){
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
			.post(BackendService.getBackEndURL() + 'user/create', data, config)
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
			.get(BackendService.getBackEndURL() + 'user/availability?email='+login,config)
			.then(function(data){
				deferredObject.resolve({ Result:  data.data });
			}, function(data){
				deferredObject.resolve({ Result:  false });
			});
			
			return deferredObject.promise;
		},
		forgetPassword: function(email, recaptcha){
			var deferredObject = $q.defer();
			var data = 'email='+email + '&g-recaptcha-response=' + recaptcha;
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
			.post(BackendService.getBackEndURL() + 'user/forget-password', data, config)
			.then(function(result){
				deferredObject.resolve({status:'success', message:'Un email vous permettant de réinitialiser votre mot de passe vient de vous être envoyé'});
			}, function(result){
				if (result.status === 404)
					deferredObject.resolve({status:'error', message:'Cette adresse mail est inconnue !'});
				else	
					deferredObject.resolve({status:'error', message:'Une erreur est survenue'});
			});
			return deferredObject.promise;
		},
		changePassword: function(email, token, newPassword){
			var deferredObject = $q.defer();
			var data = 'email='+email + '&changePasswordToken=' + token + '&password='+newPassword;
			var config = {
				headers : { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'}
			};
			
			$http
			.post(BackendService.getBackEndURL() + 'change-password/reset', data, config)
			.then(function(response){
				deferredObject.resolve({status:'success', message:"Votre mot de passe vient d'être modifié avec succès"});
			}, function(response){
				deferredObject.resolve({status:'error', message:'Une erreur est survenue'});
			});
			return deferredObject.promise;
		}
	};
}
UserService.$inject = ['$rootScope', '$http', '$q', '$cookies', 'BackendService'];

/**
* Angular Service -> PredictionService
* Expose method to save and get predictions for one user
* Controler which use this service : PronosticController
* savePredictions() -> Save predictions to user connected
* getPredictions() -> Get predictions to user connected
**/
var PredictionService = function($rootScope, $http, $q, BackendService){
	return {
		savePredictions: function(token, predictions){
			var deferredObject = $q.defer();
			var result = {};
			var config = {
				headers : { 'Accept' : 'application/json',
				'Content-Type': 'application/json',
				'Authorization': 'Basic ' + token}
			};
			$http
			.post(BackendService.getBackEndURL() + 'user/save', predictions, config)
			.then(function(data){
				result.status = data.status;
				if(data.status != 200)
					result.message = 'Une erreur est survenue lors de la sauvegarde des pronostiques.';
				deferredObject.resolve({ Result:  result });
			}, function(response){
				result.status = response.status
				result.message = 'Une erreur est survenue lors de la sauvegarde des pronostiques.';
				deferredObject.resolve({ Result: result });
			});
			
			return deferredObject.promise;
		},
		getPredictions: function(token){
			var deferredObject = $q.defer();
			var result = {};
			var config = {
				headers : { 'Accept' : 'application/json',
				'Authorization': 'Basic ' + token}
			};
			$http
			.get(BackendService.getBackEndURL() + 'user/predictions', config)
			.then(function(data){
				result.status = data.status;
				if(data.status === 200)
					result.Predictions = data.data.match_predictions_attributes;
				deferredObject.resolve({ Predictions:  result });
			}, function(response){
				deferredObject.resolve({ Predictions: null });
			});
			
			return deferredObject.promise;
		}
	};
}
PredictionService.$inject = ['$rootScope', '$http', '$q', 'BackendService'];

/**
* Angular Service -> RankingService
* Expose method to get ranks of user
* getRanks() -> Return all ranks of community user
**/
var RankingService = function($rootScope, $http, $q, BackendService){
	
	return {
		getRanks : function(){
			var deferredObject = $q.defer();
			var result= {};
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
			.get(BackendService.getBackEndURL() + 'user/rankings', config)
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
RankingService.$inject = ['$rootScope', '$http', '$q', 'BackendService'];

/**
* Angular Service -> GamesService
* Expose method to get all games with real result, information stadium, date and teams
* getGroupGames() -> Return all games of group phase
* getFinalGames() -> Return all games of final phase
**/
var GamesService = function($rootScope, $http, $q, $linq, BackendService){
	return {
		getGroupGames: function(){
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
			.get(BackendService.getBackEndURL() + 'score/games', config)
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
			var deferredObject = $q.defer();
			var result;
			var config = {
				headers : { 'Accept' : 'application/json'}
			};
			
			$http
            .get(BackendService.getBackEndURL() + 'score/games', config)
			.then(function(data){
				if(data.status === 200)
					result = $linq.Enumerable()
								.From(data.data)
								.Where(function(match){
									return (match.group.indexOf("Groupe") == -1);
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
		}
	};
}
GamesService.$inject = ['$rootScope', '$http', '$q', '$linq', 'BackendService'];