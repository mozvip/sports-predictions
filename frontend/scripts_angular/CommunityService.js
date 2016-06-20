angular.module('sports-predictions')
        .factory('CommunityService', ['$http', '$q', 'BackendService', function ($http, $q, BackendService) {		
                return {
                        getCommunity: function () {
								currentCommnity = undefined;
                                var deferredObject = $q.defer();
                                $http.get(BackendService.getBackEndURL() + "community").then(
                                        function (response) {
												currentCommnity = response.data;
                                                deferredObject.resolve(response.data);
                                        }, function (response) {
                                                deferredObject.reject();
                                        }
                                )
                                return deferredObject.promise;
                        },

                        save: function( community ) {
                                var config = BackendService.getRequestConfig();
                                var data = JSON.stringify( community );

                                var deferredObject = $q.defer();
                                $http.post(BackendService.getBackEndURL() + "community", data, config).then(
                                        function (response) {
                                                deferredObject.resolve(response.data);
                                        }, function (response) {
                                                deferredObject.reject();
                                        }
                                )
                                return deferredObject.promise;
                        }
                }
        }]);