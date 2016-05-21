angular.module('sports-predictions')
    .factory('AdminService', ['$q', '$http', 'BackendService', 'Notification', function ($q, $http, BackendService, Notification) {
        return {
            getUsers: function () {
                var deferredObject = $q.defer();
                var result;
                var config = {
                    headers: { 'Accept': 'application/json' }
                };

                $http.get(BackendService.getBackEndURL() + 'admin/users', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error('error');
                    });

                return deferredObject.promise;
            }
        }
    }]);