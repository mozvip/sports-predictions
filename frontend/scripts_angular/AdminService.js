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
                        Notification.error({ 'title': response.statusText, 'message': response.data.message });
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
                        Notification.error({ 'title': response.statusText, 'message': response.data.message });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            toggleActive: function (email) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.post(BackendService.getBackEndURL() + 'admin/toggle-active/' + email, '', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            toggleAdmin: function (email) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.post(BackendService.getBackEndURL() + 'admin/toggle-admin/' + email, '', config)
                    .then(function (response) {
                        deferredObject.resolve({ users: response.data });
                    }, function (response) {
                        Notification.error({ 'title': response.statusText, 'message': response.data });
                        deferredObject.reject();
                    });

                return deferredObject.promise;
            },
            toggleLate: function (email) {
                var deferredObject = $q.defer();
                var config = BackendService.getRequestConfig();

                $http.post(BackendService.getBackEndURL() + 'admin/toggle-late/' + email, '', config)
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
                var config = BackendService.getRequestConfig();

                $http.delete(BackendService.getBackEndURL() + 'user/' + email, config)
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