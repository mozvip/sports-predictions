angular.module('sports-predictions').controller('AdminController', ['$scope', '$http', 'AdminService', 'GamesService', 'CommunityService', 'SweetAlert', 'BackendService', 'currentUser', 'community', 'Notification', function ($scope, $http, AdminService, GamesService, CommunityService, SweetAlert, BackendService, currentUser, community,  Notification) {

        $scope.community = community;

        $scope.userGridOptions = {
                enableSorting: true,
                enableRowSelection: true,
                enableFiltering: true,
                enableGridMenu: true,
                columnDefs: [
                        { field: 'name', displayName: 'Nom' },
                        { field: 'email', displayName: 'Email' },
                        { field: 'currentScore', displayName: 'Score' },
                        { field: 'admin', type: 'boolean' },
                        { field: 'lastLoginDate', displayName: 'Dernière Activité' },
                        { field: 'active', type: 'boolean' }
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

        $scope.userNoPredictionGridOptions = {
                enableSorting: true,
                enableRowSelection: true,
                enableFiltering: true,
                enableGridMenu: true,
                columnDefs: [
                        { field: 'name', displayName: 'Nom' },
                        { field: 'email', displayName: 'Email' },
                        { field: 'admin', type: 'boolean' },
                        { field: 'lastLoginDate', displayName: 'Dernière Activité' },
                        { field: 'active', type: 'boolean' }
                ],
                onRegisterApi: function (gridApi) {
                        $scope.userGridNoPredictionOptionsApi = gridApi;
                }
        }

        AdminService.getUsersNoPrediction().then(function (response) {
                $scope.userNoPredictionGridOptions.data = response.users;
        });

        $scope.disableEnable = function () {
                for (let i = 0; i < $scope.userGridOptionsApi.selection.getSelectedCount(); i++) {
                        var userProfile = $scope.userGridOptionsApi.selection.getSelectedRows()[i];
                        AdminService.toggleActive(userProfile.email);
                        userProfile.active = !userProfile.active;
                }
        }

        $scope.games = [];
        GamesService.getGames().then(function (response) {
                $scope.games = response;
        })

        $scope.currentUser = currentUser;

        $scope.gameLabel = function (game) {
                return game.group + ' - ' + game.homeTeam + ' - ' + game.awayTeam;
        }

        $scope.homeScore = 0;
        $scope.awayScore = 0;
        $scope.winningTeamName = '';

        $scope.selectedGame = undefined;

        $scope.saveParameters = function() {
                CommunityService.save( community );
        }

        $scope.deleteUsers = function() {

                var listOfEmailsToDelete = [];

                for (let i = 0; i < $scope.userGridNoPredictionOptionsApi.selection.getSelectedCount(); i++) {
                        var userProfile = $scope.userGridNoPredictionOptionsApi.selection.getSelectedRows()[i];
                        if (userProfile) {
                                listOfEmailsToDelete.push( userProfile.email );
                        }
                }

                SweetAlert.swal({
                        title: "Etes vous sûr?",
                        text: "Confirmez-vous la suppression de ces " + listOfEmailsToDelete.length + " utilisateurs ?",
                        type: "warning",
                        showCancelButton: true,
                        cancelButtonText: "Annuler",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Je confirme !",
                        closeOnConfirm: true
                },
                        function (isConfirm) {
                                if (!isConfirm) {
                                        return;
                                }
                                for (let email of listOfEmailsToDelete) {
                                        AdminService.deleteUser(email).then( function() {
                                                for (let i=$scope.userNoPredictionGridOptions.data.length-1; i>=0; i--) {
                                                        var row = $scope.userNoPredictionGridOptions.data[i];
                                                        if (row.email && row.email == email) {
                                                                $scope.userNoPredictionGridOptions.data.splice(i, 1);
                                                        }
                                                }
                                                Notification.success("L'utilisateur " + email + " a été supprimé")}, function() {}
                                        );
                                }
                        });
        }

        $scope.submitScore = function () {
                SweetAlert.swal({
                        title: "Etes vous sûr?",
                        text: "Confirmez-vous la soumission de ce score ?",
                        type: "warning",
                        showCancelButton: true,
                        cancelButtonText: "Annuler",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Je confirme !",
                        closeOnConfirm: true
                },
                        function (isConfirm) {
                                if (!isConfirm) {
                                        return;
                                }
                                var data = 'gameNum=' + $scope.selectedGame.matchNum + '&homeScore=' + $scope.homeScore + '&awayScore=' + $scope.awayScore + '&winningTeamName=' + $scope.winningTeamName;
                                var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
                                $http.post(BackendService.getBackEndURL() + "score/submit", data, config).then(function (response) {
                                        Notification.success("Le score a été soumis avec succès");
                                }, function (response) {

                                        if (response.status == 406) {
                                                Notification.error("Ce match n'a pas encore eu lieu !");
                                        } else {
                                                Notification.error("Une erreur est survenue lors de la soumission du score !");
                                        }
                                });
                        });
        }

}]);