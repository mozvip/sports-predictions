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
angular.module('sports-predictions')
    .factory('UserService', ['$rootScope', '$http', '$q', '$cookies', 'BackendService', function ($rootScope, $http, $q, $cookies, BackendService) {

		var connectedUser;

		return {
			
			refreshProfile : function() {
				connectedUser = undefined;
			},

			getCurrentUser: function () {
				var deferredObject = $q.defer();
				if (this.isConnected()) {
					if (connectedUser == undefined) {
						$http.get(BackendService.getBackEndURL() + "user/predictions", { headers: { 'Authorization': 'Basic ' + this.getToken() } }).then(
							function (response) {
								deferredObject.resolve(response.data);
								connectedUser = response.data;
							}, function (response) {
								deferredObject.reject();
							}
						)
					} else {
						deferredObject.resolve( connectedUser );
					}
				} else {
					deferredObject.reject();
				}
				return deferredObject.promise;
			},

			isConnected: function () {
				return this.getToken() != null;
			},
			getToken: function () {
				return $cookies.get('SESSION_ID');
			},
			getCurrentLogin: function () {
				return $cookies.get('SESSION_CURRENT_LOGIN');
			},
			login: function (login, password) {

				connectedUser = undefined;
				
				var deferredObject = $q.defer();
				var userResult = {
					message: ''
				};
				var data = 'email=' + login + '&password=' + password;
				var config = {
					headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
				};

				$http
					.post(BackendService.getBackEndURL() + 'user/signin', data, config)
					.then(function (response) {
						userResult.status = response.status;
						if (response.status === 200) {
							connectedUser = response.data;
							$cookies.put('SESSION_ID', response.data.authToken);
							$cookies.put('SESSION_CURRENT_LOGIN', login);
							deferredObject.resolve({ User: userResult });
							$rootScope.$broadcast("connectionStateChanged");
						} else {
							userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
							deferredObject.resolve({ User: userResult });
						}
					}, function (response) {
						userResult.message = 'Erreur identifiant ou mot de passe incorrect !';
						deferredObject.resolve({ User: userResult });
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
				var config = {
					headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
				};

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
				var config = {
					headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
				};

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
				var config = {
					headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
				};

				$http
					.post(BackendService.getBackEndURL() + 'change-password/reset', data, config)
					.then(function (response) {
						deferredObject.resolve({ status: 'success', message: "Votre mot de passe vient d'être modifié avec succès" });
					}, function (response) {
						deferredObject.resolve({ status: 'error', message: 'Une erreur est survenue' });
					});
				return deferredObject.promise;
			}
		};
	}]);