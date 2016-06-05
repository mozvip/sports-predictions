angular.module('sports-predictions').controller('AdminController', ['$scope', '$http', 'AdminService', 'UserService', 'GamesService', 'currentUser', function ($scope, $http, AdminService, UserService, GamesService, currentUser) {

        $scope.userGridOptions = {
                enableSorting: true,
                enableRowSelection: true,
                enableFiltering: true,
                columnDefs: [
                        { field: 'name', displayName: 'Nom' },
                        { field: 'email', displayName: 'Email' },
                        { field: 'currentScore', displayName: 'Score' },
                        { field: 'admin' },
                        { field: 'lastLoginDate', displayName: 'Dernière Activité' },
                        { field: 'active' }
                ],
                onRegisterApi: function (gridApi) {
                        $scope.userGridOptionsApi = gridApi;
                }
        }

        $scope.userGridOptions.isRowSelectable = function (row) {
                // ensure user does not disable its own account
                return (row.entity.active == false || row.entity.email != currentUser.email);
        };

        AdminService.getUsers().then(function (response) {
                $scope.userGridOptions.data = response.users;
        });

        $scope.disableEnable = function () {
                for(let i=0; i<$scope.userGridOptionsApi.selection.getSelectedCount(); i++) {
                        var userProfile = $scope.userGridOptionsApi.selection.getSelectedRows()[i];
                        AdminService.toggleActive( userProfile.email );
                        userProfile.active = !userProfile.active;
                }
        }

        $scope.games = [];
        GamesService.getGames().then(function (response) {
                $scope.games = response;
        })

        $scope.currentUser = currentUser;
        $scope.community = currentUser.community;

        $scope.gameLabel = function (game) {
                return game.group + ' ' + game.homeTeam + ' - ' + game.awayTeam;
        }
        
        $scope.homeScore = 0;
        $scope.awayScore = 0;
        $scope.gameId = 0;

}]);