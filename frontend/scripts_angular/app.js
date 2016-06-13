// Application du site euro2016Predictions
var euro2016Predictions = angular.module('sports-predictions', ['ui.grid', 'ui.grid.selection', 'ui.grid.exporter', 'ngAnimate', 'vTabs', 'ui.bootstrap', 'chieffancypants.loadingBar', 'ngRoute', 'ngCookies', 'vcRecaptcha', 'angular-linq', 'ui-notification', 'ngTable', 'oitozero.ngSweetAlert'])
	.config(function (cfpLoadingBarProvider) {
		cfpLoadingBarProvider.includeSpinner = false;
	});

/* Définition des directives */
euro2016Predictions.directive('logoCeDisplay', logoCeDisplay);
euro2016Predictions.directive('pronostic', pronostic);
euro2016Predictions.directive('compileHtml', compileHtml);
euro2016Predictions.directive('pronosticFinal', pronosticFinal);