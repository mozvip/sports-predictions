angular.module('sports-predictions')
    .factory('TeamService', ['$q', '$http', 'BackendService', 'UserService', 'Notification', 'Upload', function ($q, $http, BackendService, UserService, Notification, Upload) {
        return {
            getTeams: function () {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.get(BackendService.getBackEndURL() + 'teams', config)
                    .then(function (response) {
                        deferredObject.resolve({ teams: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            createTeam: function (name, description, image) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');

                Upload.upload({
                    url: BackendService.getBackEndURL() + 'teams',
                    data: {file: file, 'name': $scope.teamName, 'description': $scope.teamDescription}
                }).then(function (resp) {
                    console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                }, function (resp) {
                    console.log('Error status: ' + resp.status);
                    Notification.error({ 'title': resp.status, 'message': resp.data });
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
                });

                return deferredObject.promise;
            }
            
        }
    }]);