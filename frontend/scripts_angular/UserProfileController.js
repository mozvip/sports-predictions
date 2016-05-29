angular.module('sports-predictions').controller('UserProfileController', ['$scope', '$http', 'UserService', 'currentUser', function ($scope, $http, UserService, currentUser) {

        $scope.currentUser = currentUser;
        $scope.oldPassword = '';
        $scope.password1 = '';
        $scope.password2 = '';

        $scope.save = function () {
                UserService.save($scope.currentUser);
        }

        $scope.changePassword = function () {
                if ($scope.password1 === $scope.password2) {

                        UserService.changePassword($routeParams.email, $scope.oldPassword, $scope.password1).then(
                                function (response) {
                                        if (response.status == 'success') {
                                                Notification.success(response.message);
                                        } else {
                                                Notification.error(response.message);
                                        }
                                }
                        )

                } else {
                        Notification.error("Les mots de passe que vous avez saisi ne concordent pas");
                }
        }

}]);