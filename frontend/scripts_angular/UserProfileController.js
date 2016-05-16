angular.module('sports-predictions').controller('UserProfileController', ['$scope', '$http', 'UserService', function ($scope, $http, UserService) {

        $scope.currentUser = UserService.currentUser();

        $scope.save = function () {
                UserService.save($scope.currentUser);
        }

}]);