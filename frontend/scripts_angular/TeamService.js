angular.module('sports-predictions')
    .factory('TeamService', ['$q', '$http', 'BackendService', 'UserService', 'Notification', function ($q, $http, BackendService, UserService, Notification) {
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
            recalculateScores: function() {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();
                $http.get(BackendService.getBackEndURL() + 'score/recalculate', config)
                    .then(function (response) {
                        deferredObject.resolve(response);
                    }, function (response) {
                        deferredObject.reject(response);
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
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            toggleActive: function (email) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
                
                var data = 'email=' + email;

                $http.post(BackendService.getBackEndURL() + 'admin/toggle-active', data, config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            deleteUser: function (email) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig('application/x-www-form-urlencoded; charset=UTF-8');
                
                var data = 'email=' + email;

                $http.post(BackendService.getBackEndURL() + 'admin/delete-user', data, config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            }
            
        }
    }]);