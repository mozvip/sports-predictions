/**
 * Angular Controller -> LoginController  
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions').controller('LoginController', ['$scope', '$route', '$routeParams', '$location', 'UserService', 'count', 'community', 'Notification', function ($scope, $route, $routeParams, $location, UserService, count, community, Notification) {

    $scope.User = {
        Login: '',
        Password: ''
    };

    $scope.count = count;

    $scope.community = community;

    $scope.login = function () {
        var loginResult = UserService.login($scope.User.Login, $scope.User.Password);
        loginResult.then(function (response) {
            if (response.status != 200) {
                Notification.error("Votre email ou votre mot de passe est incorrect");
            } else {
                $location.path('pronostic');
            }
        });
    }
}]);