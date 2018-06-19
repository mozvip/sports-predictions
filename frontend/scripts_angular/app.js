// Application du site sportsPredictions
var sportsPredictions = angular.module('sports-predictions',
    [
        'ui.grid', 'ui.grid.selection',
        'ui.grid.exporter',
        'ngAnimate',
        'ui.bootstrap',
        'chieffancypants.loadingBar',
        'ngRoute',
        'ngCookies',
        'vcRecaptcha',
        'angular-linq',
        'ui-notification',
        'ngTable',
        'oitozero.ngSweetAlert',
        'ngFileUpload'
    ])
    .config(function (cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeSpinner = false;
    });

/* Définition des directives */
sportsPredictions.directive('pronostic', pronostic);
sportsPredictions.directive('compileHtml', compileHtml);
sportsPredictions.directive('pronosticFinal', pronosticFinal);
sportsPredictions.directive('backButton', backButton);
sportsPredictions.directive('team', team);