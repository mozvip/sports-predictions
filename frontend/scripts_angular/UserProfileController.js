angular.module('sports-predictions').controller('UserProfileController', ['$scope', '$http', 'UserService', 'currentUser', 'Notification', function ($scope, $http, UserService, currentUser, Notification) {

        $scope.currentUser = currentUser;
        $scope.oldPassword = '';
        $scope.password1 = '';
        $scope.password2 = '';

        $scope.saveProfile = function () {
                UserService.saveProfile($scope.currentUser).then(
                        function (response) {
                                if (response.status < 400) {
                                        Notification.success("Modification sauvegardées");
                                } else {
                                        Notification.error(response.message);
                                }
                        }
                );
        }

        $scope.changePassword = function () {
                if ($scope.password1 === $scope.password2) {

                        UserService.changeOwnPassword($scope.oldPassword, $scope.password1).then(
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