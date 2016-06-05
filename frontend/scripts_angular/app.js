// Application du site euro2016Predictions
var euro2016Predictions = angular.module('sports-predictions', ['ui.grid', 'ui.grid.selection', 'ngAnimate', 'vTabs', 'ui.bootstrap', 'chieffancypants.loadingBar', 'ngRoute', 'ngCookies', 'vcRecaptcha', 'angular-linq', 'ui-notification', 'ngTable'])
	.config(function (cfpLoadingBarProvider) {
		cfpLoadingBarProvider.includeSpinner = false;
	})
	.factory('BackendService', function ($location) {
		return {
			getBackEndURL: function () {

				var port = $location.protocol() == 'https' ? '443' : '80';
				if ($location.host() == 'localhost') {
					// hack for local development without backend
					//return 'https://test.pronostics2016.com/api/';
					// hack for development with local backend
					return 'http://localhost:8080/api/';
				} else {
					return $location.protocol() + '://' + $location.host() + ':' + port + '/api/';
				}
			},
			getToken: function () {
				return $cookies.get('SESSION_ID');
			},			
			getRequestConfig: function () {
				return {
					headers: {
						'Accept': 'application/json', 'Content-Type': 'application/json', 'Authorization': 'Basic ' + this.getToken()
					}
				}
			}
		}
	});

/* Définition des controllers à l'application */
euro2016Predictions.controller('SignupController', SignupController);
euro2016Predictions.controller('TestController', TestController);
euro2016Predictions.controller('RanksController', RanksController);
euro2016Predictions.controller('ForgetController', ForgetController);
euro2016Predictions.controller('ResetPasswordController', ResetPasswordController);
euro2016Predictions.controller('PronosticFinalController', PronosticFinalController);

/* Définition des factory */
euro2016Predictions.factory('PredictionService', PredictionService);
euro2016Predictions.factory('RankingService', RankingService);

/* Définition des directives */
euro2016Predictions.directive('logoCeDisplay', logoCeDisplay);
euro2016Predictions.directive('pronostic', pronostic);
euro2016Predictions.directive('compileHtml', compileHtml);
euro2016Predictions.directive('pronosticFinal', pronosticFinal);