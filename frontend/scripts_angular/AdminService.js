angular.module('sports-predictions')
    .factory('AdminService', ['$q', '$http', 'BackendService', 'UserService', 'Notification', function ($q, $http, BackendService, UserService, Notification) {
        return {
            getUsers: function () {
                var deferredObject = $q.defer();
                var result;
                var config = {
                    headers: { 'Accept': 'application/json', 'Authorization' : 'Basic ' + UserService.getToken() }
                };

                $http.get(BackendService.getBackEndURL() + 'admin/users', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error( {'title':response.statusText, 'message':response.data} );
                    });

                return deferredObject.promise;
            }
        }
    }]);