angular.module('sports-predictions')
    .factory('TeamService', ['$q', '$http', 'BackendService', 'UserService', 'Notification', 'Upload', function ($q, $http, BackendService, UserService, Notification, Upload) {
        return {
            getTeams: function () {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.get(BackendService.getBackEndURL() + 'teams', config)
                    .then(function (response) {
                        deferredObject.resolve(response.data);
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
                    headers : config.headers,
                    data: {file: image, 'name': name, 'description': description}
                }).then(function (resp) {
                    Notification.success({ 'title': 'Succès', 'message': 'L\'équipe a été créée' });
                    deferredObject.resolve(resp);
                }, function (resp) {
                    Notification.error({ 'title': resp.statusText, 'message': resp.data });
                    deferredObject.reject(resp.statusText);
                });

                return deferredObject.promise;
            }
            
        }
    }]);