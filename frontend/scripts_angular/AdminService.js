angular.module('sports-predictions')
    .factory('AdminService', ['$q', '$http', 'BackendService', 'UserService', 'Notification', function ($q, $http, BackendService, UserService, Notification) {
        return {
            getUsers: function () {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.get(BackendService.getBackEndURL() + 'admin/users', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                    });

                return deferredObject.promise;
            },
            getUsersNoPrediction: function () {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.get(BackendService.getBackEndURL() + 'admin/users-no-prediction', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                    });

                return deferredObject.promise;
            },
            toggleActive: function (email) {
                var deferredObject = $q.defer();
                var config = {
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8', 'Authorization': 'Basic ' + UserService.getToken() }
                };
                
                var data = 'email=' + email;

                $http.post(BackendService.getBackEndURL() + 'admin/toggle-active', data, config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                    });

                return deferredObject.promise;
            }
        }
    }]);