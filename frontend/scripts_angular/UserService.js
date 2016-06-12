/**
* Angular Service -> UserService
* Expose method to login, logout user,to know if user is connected and to sign up a new user.
* Controler which use this service : HomeController, LoginController, SignupController, PronosticController, RanksController, ForgetController, UserProfileController
* isConnected() -> Can get is user is connected
* getCurrentLogin() -> Return current user login
* login(login, password) -> Log user with login/password
* logout() -> Log out current user
* signup() -> Sign up new user
* loginAvailable() -> Can know if login is available for sign up
* forgetPassword() ->  User forgot password
* changePassword() -> Can change password for current user
**/
angular.module('sports-predictions')
    .factory('UserService', ['$rootScope', '$http', '$q', '$cookies', 'BackendService', function ($rootScope, $http, $q, $cookies, BackendService) {

		var connectedUser;
		
		return {
			
			saveProfile : function( userProfile ) {
				var deferredObject = $q.defer();
				var data = 'name=' + userProfile.name;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
				$http.post(BackendService.getBackEndURL() + "user/saveProfile", data, config).then(
					function (response) {
						deferredObject.resolve(response);
					}, function (response) {
						deferredObject.reject();
					}
				)
				return deferredObject.promise;
			},
			
			isConnected: function() {
				return connectedUser != undefined || BackendService.getToken() != undefined;	
			},
			
			isAdmin: function() {
				return connectedUser != undefined && connectedUser.admin;
			},

			refreshProfile: function() {
				connectedUser = undefined;
				return this.getCurrentUser();
			},

			getCurrentUser: function () {
				var deferredObject = $q.defer();
				if (connectedUser) {
					deferredObject.resolve(connectedUser);
				} else {
					if (BackendService.getToken()) {
						$http.get(BackendService.getBackEndURL() + 'user/predictions', BackendService.getRequestConfig()).then(
							function( response ) {
								deferredObject.resolve( response );
								connectedUser = response.data;
							}, function( response ) {
								deferredObject.reject();
							}
						);
					} else {
						deferredObject.reject();
					}
				}
				return deferredObject.promise;
			},

			getCount: function () {
				var deferredObject = $q.defer();
				$http.get(BackendService.getBackEndURL() + "user/count").then(
					function (response) {
						deferredObject.resolve(response.data);
					}, function (response) {
						deferredObject.reject();
					}
				)
				return deferredObject.promise;
			},
			getCurrentLogin: function () {
				return $cookies.get('SESSION_CURRENT_LOGIN');
			},
			login: function (login, password) {

				connectedUser = undefined;

				var deferredObject = $q.defer();
				var data = 'email=' + login + '&password=' + password;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');

				$http
					.post(BackendService.getBackEndURL() + 'user/signin', data, config)
					.then(function (response) {
						if (response.status === 200) {
							connectedUser = response.data;
							BackendService.connect( response.data );
							$cookies.put('SESSION_CURRENT_LOGIN', login);
							deferredObject.resolve( response );
							$rootScope.$broadcast("connectionStateChanged");
						} else {
							deferredObject.resolve( response );
						}
					}, function (response) {
						deferredObject.resolve( response );
					});

				return deferredObject.promise;
			},
			logout: function () {
				$cookies.remove('SESSION_ID');
				$rootScope.$broadcast("connectionStateChanged");
				connectedUser = undefined;
			},
			signup: function (login, name, password, captcha) {
				var deferredObject = $q.defer();
				var userResult = {};
				var data = 'email=' + login + '&name=' + name + '&password=' + password + '&g-recaptcha-response=' + captcha;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');

				$http
					.post(BackendService.getBackEndURL() + 'user/create', data, config)
					.then(function (data) {
						userResult.status = data.status;
						if (data.status === 500)
							userResult.message = 'Erreur utilisateur déjà existant !';
						deferredObject.resolve({ User: userResult });
					}, function (data) {
						userResult.message = "Erreur lors de l'enregistrement de cet utilisateur  !";
						deferredObject.resolve({ User: userResult });
					});

				return deferredObject.promise;
			},
			forgetPassword: function (email, recaptcha) {
				var deferredObject = $q.defer();
				var data = 'email=' + email + '&g-recaptcha-response=' + recaptcha;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');

				$http
					.post(BackendService.getBackEndURL() + 'user/forget-password', data, config)
					.then(function (result) {
						deferredObject.resolve({ status: 'success', message: 'Un email vous permettant de réinitialiser votre mot de passe vient de vous être envoyé' });
					}, function (result) {
						if (result.status === 404)
							deferredObject.resolve({ status: 'error', message: 'Cette adresse mail est inconnue !' });
						else
							deferredObject.resolve({ status: 'error', message: 'Une erreur est survenue' });
					});
				return deferredObject.promise;
			},
			changePassword: function (email, token, newPassword) {
				var deferredObject = $q.defer();
				var data = 'email=' + email + '&changePasswordToken=' + token + '&password=' + newPassword;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');

				$http
					.post(BackendService.getBackEndURL() + 'change-password/reset', data, config)
					.then(function (response) {
						deferredObject.resolve({ status: 'success', message: "Votre mot de passe vient d'être modifié avec succès" });
					}, function (response) {
						deferredObject.resolve({ status: 'error', message: 'Une erreur est survenue' });
					});
				return deferredObject.promise;
			},
			changeOwnPassword: function (oldPassword, newPassword) {
				var deferredObject = $q.defer();
				var data = 'oldPassword=' + oldPassword + '&newPassword=' + newPassword;
				var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
				$http
					.post(BackendService.getBackEndURL() + 'change-password/self', data, config)
					.then(function (response) {
						deferredObject.resolve({ status: 'success', message: "Votre mot de passe vient d'être modifié avec succès" });
					}, function (response) {
						var message = (response.status == 401 ? 'Le mot de passe actuel que vous avez saisi est incorrect !' : 'Une erreur est survenue' );
						deferredObject.resolve({ status: 'error', message: message });
					});
				return deferredObject.promise;
			}			
		};
	}]);