angular.module('sports-predictions').controller('TeamsController', ['$scope', '$http', 'AdminService', 'TeamService', 'community', 'SweetAlert', 'BackendService', 'currentUser', 'teams', 'Notification', function ($scope, $http, AdminService, TeamService, community, SweetAlert, BackendService, currentUser, teams, Notification) {

    $scope.community = community;

    $scope.teams = teams;

    $scope.currentUser = currentUser;

    $scope.imageURL = function(team) {
        return BackendService.getDataURL() + team.imageURL;
    }

    $scope.joinTeam = function(team) {
        TeamService.joinTeam(team);
    }

    $scope.canJoin = function(team) {
        return true;
    }

    $scope.canLeave = function(team) {
        return !$scope.canJoin(team);
    }

    $scope.canDelete = function(team) {
        return currentUser.email == team.owner;
    }

    $scope.deleteTeam = function(team) {
        TeamService.deleteTeam(team.name);
    }

    $scope.createTeam = function () {
        if ($scope.createTeamForm.file.$valid && $scope.file) {
            TeamService.createTeam($scope.teamName, $scope.teamDescription, $scope.file).then( function() {
                TeamService.getTeams().then(function(teams) {
                    $scope.teams = teams;
                    $scope.teamName = '';
                    $scope.teamDescription = '';
                    $scope.file = undefined;
                })
            });
        }
    }

}]);