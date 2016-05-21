angular.module('sports-predictions').controller('AdminController', ['$scope', '$http', 'AdminService', 'UserService', function ($scope, $http, AdminService, UserService) {

        $scope.currentUser = UserService.currentUser();
        
        $scope.users = [];
        
        $scope.community = $scope.currentUser.community;
        
        AdminService.getUsers().then( function(response) {
                $scope.users = response.users;
        });

}]);