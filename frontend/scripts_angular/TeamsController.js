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
                text: "Confirmez-vous la suppression de ces " + listOfEmailsToDelete.length + " équipes ?",
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
        if ($scope.form.file.$valid && $scope.file) {
            TeamService.createTeam($scope.teamName, $scope.teamDescription, $scope.file);

        }
    }

}]);