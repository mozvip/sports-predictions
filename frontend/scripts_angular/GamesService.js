angular.module('sports-predictions')
        .factory('GamesService', ['$rootScope', '$http', '$q', '$linq', 'BackendService', function ($rootScope, $http, $q, $linq, BackendService) {

                return {
                        getGames: function () {
                                var deferredObject = $q.defer();

                                $http
                                        .get(BackendService.getBackEndURL() + 'score/games')
                                        .then(function (response) {
                                                deferredObject.resolve(response.data);
                                        }, function (response) {
                                                deferredObject.reject();
                                        });

                                return deferredObject.promise;
                        },

                        getGameStats: function( gameId ) {
                                var deferredObject = $q.defer();
                                var config = BackendService.getRequestConfig();

                                $http
                                        .get(BackendService.getBackEndURL() + 'game?gameId=' + gameId, config)
                                        .then(function (response) {
                                                deferredObject.resolve(response.data);
                                        }, function (response) {
                                                deferredObject.reject();
                                        });

                                return deferredObject.promise;
                        },

                        getGroupGames: function () {
                                var deferredObject = $q.defer();
                                var result;

                                $http
                                        .get(BackendService.getBackEndURL() + 'score/games')
                                        .then(function (data) {
                                                if (data.status === 200)
                                                        result = $linq.Enumerable()
                                                                .From(data.data)
                                                                .Where(function (match) {
                                                                        return match.group;
                                                                })
                                                                .OrderBy(function (match) {
                                                                        return match.matchNum;
                                                                })
                                                                .ToArray();
                                                deferredObject.resolve({ Games: result });
                                        }, function (data) {
                                                deferredObject.resolve({ Games: null });
                                        });

                                return deferredObject.promise;
                        },
                        getFinalGames: function () {
                                var deferredObject = $q.defer();
                                var result;

                                $http
                                        .get(BackendService.getBackEndURL() + 'score/games')
                                        .then(function (data) {
                                                if (data.status === 200)
                                                        result = $linq.Enumerable()
                                                                .From(data.data)
                                                                .Where(function (match) {
                                                                        return !match.group;
                                                                })
                                                                .OrderBy(function (match) {
                                                                        return match.matchNum;
                                                                })
                                                                .ToArray();
                                                deferredObject.resolve({ Games: result });
                                        }, function (data) {
                                                deferredObject.resolve({ Games: null });
                                        });

                                return deferredObject.promise;
                        }
                }

        }]);