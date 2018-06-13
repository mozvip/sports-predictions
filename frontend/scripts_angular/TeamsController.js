angular.module('sports-predictions').controller('TeamsController', ['$scope', '$http', 'AdminService', 'TeamService', 'CommunityService', 'SweetAlert', 'BackendService', 'currentUser', 'community', 'Notification', function ($scope, $http, AdminService, TeamService, CommunityService, SweetAlert, BackendService, currentUser, community, Notification) {

    $scope.community = community;

    $scope.teamGridOptions = {
        enableSorting: true,
        enableRowSelection: true,
        enableFiltering: true,
        enableGridMenu: true,
        columnDefs: [
            {field: 'name', displayName: 'Nom'},
            {field: 'description', displayName: 'Description'},
            {field: 'image', displayName: 'Image'}
        ],
        onRegisterApi: function (gridApi) {
            $scope.teamGridOptionsApi = gridApi;
        }
    }

    TeamService.getTeams().then(function (response) {
        $scope.teamGridOptions.data = response.teams;
    });

    $scope.deleteTeams = function () {

        var listOfEmailsToDelete = [];

        for (var i = 0; i < $scope.teamGridOptionsApi.selection.getSelectedCount(); i++) {
            var userProfile = $scope.teamGridOptionsApi.selection.getSelectedRows()[i];
            if (userProfile) {
                listOfEmailsToDelete.push(userProfile.email);
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
                for (var email of listOfEmailsToDelete) {
                    AdminService.deleteUser(email).then(function () {
                            for (var i = $scope.userNoPredictionGridOptions.data.length - 1; i >= 0; i--) {
                                var row = $scope.userNoPredictionGridOptions.data[i];
                                if (row.email && row.email == email) {
                                    $scope.userNoPredictionGridOptions.data.splice(i, 1);
                                }
                            }
                            Notification.success("L'utilisateur " + email + " a été supprimé")
                        }, function () {
                        }
                    );
                }
            });
    }

    $scope.createTeam = function () {
        SweetAlert.swal({
                title: "Etes vous sûr?",
                text: "Confirmez-vous la création de cette équipe ?",
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

}]);angular.module('sports-predictions').controller('TeamsController', ['$scope', '$http', 'AdminService', 'TeamService', 'CommunityService', 'SweetAlert', 'BackendService', 'currentUser', 'community', 'Notification', function ($scope, $http, AdminService, TeamService, CommunityService, SweetAlert, BackendService, currentUser, community, Notification) {

    $scope.community = community;

    $scope.teamGridOptions = {
        enableSorting: true,
        enableRowSelection: true,
        enableFiltering: true,
        enableGridMenu: true,
        columnDefs: [
            {field: 'name', displayName: 'Nom'},
            {field: 'description', displayName: 'Description'},
            {field: 'image', displayName: 'Image'}
        ],
        onRegisterApi: function (gridApi) {
            $scope.teamGridOptionsApi = gridApi;
        }
    }

    TeamService.getTeams().then(function (response) {
        $scope.teamGridOptions.data = response.teams;
    });

    $scope.deleteTeams = function () {

        var listOfEmailsToDelete = [];

        for (var i = 0; i < $scope.teamGridOptionsApi.selection.getSelectedCount(); i++) {
            var userProfile = $scope.teamGridOptionsApi.selection.getSelectedRows()[i];
            if (userProfile) {
                listOfEmailsToDelete.push(userProfile.email);
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
                for (var email of listOfEmailsToDelete) {
                    AdminService.deleteUser(email).then(function () {
                            for (var i = $scope.userNoPredictionGridOptions.data.length - 1; i >= 0; i--) {
                                var row = $scope.userNoPredictionGridOptions.data[i];
                                if (row.email && row.email == email) {
                                    $scope.userNoPredictionGridOptions.data.splice(i, 1);
                                }
                            }
                            Notification.success("L'utilisateur " + email + " a été supprimé")
                        }, function () {
                        }
                    );
                }
            });
    }

    $scope.createTeam = function () {
        SweetAlert.swal({
                title: "Etes vous sûr?",
                text: "Confirmez-vous la création de cette équipe ?",
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
                var data = 'name=' + $scope.teamName + '&description=' + $scope.teamDescription + '&image=' + $scope.teamImage;
                var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
                $http.post(BackendService.getBackEndURL() + "teams", data, config).then(function (response) {
                    Notification.success("L'équipe a été créée avec succès");
                }, function (response) {
                    Notification.error("Une erreur est survenue lors de la création de cette équipe !");
                });
            });
    }

}]);