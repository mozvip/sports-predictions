angular.module('sports-predictions').controller('AdminController', ['$scope', '$http', 'AdminService', 'UserService', function ($scope, $http, AdminService, UserService) {

        $scope.currentUser = UserService.currentUser();

        $scope.community = $scope.currentUser.community;

        $scope.userGridOptions = {
                enableSorting: true,
                enableRowSelection: true,
                columnDefs: [
                        { field: 'name', displayName: 'Nom' },
                        { field: 'email', displayName: 'Email' },
                        { field: 'currentScore', displayName: 'Score' },
                        { field: 'admin'},
                        { field: 'lastLoginDate', displayName:'Dernière Activité'},
                        { field: 'active'}
                ],
                onRegisterApi: function (gridApi) {
                        $scope.userGridOptionsApi = gridApi;
                }
        }

        AdminService.getUsers().then(function (response) {
                $scope.userGridOptions.data = response.users;
        });

}]);