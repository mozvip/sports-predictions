angular.module('sports-predictions')
    .factory('CommunityService', ['$http', '$q', 'BackendService', function ($http, $q, BackendService) {

        var currentCommunity;

        return {
            getCommunity: function () {
                var deferredObject = $q.defer();
                if (currentCommunity != undefined) {
                    deferredObject.resolve(currentCommunity);
                } else {
                    $http.get(BackendService.getBackEndURL() + "community").then(
                        function (response) {
                            let data = response.data;
                            data.openingDate = new Date(data.openingDate);
                            deferredObject.resolve(data);
                        }, function (response) {
                            deferredObject.reject(response);
                        }
                    )
                }
                return deferredObject.promise;
            },

            save: function (community) {
                var config = BackendService.getRequestConfig();

                community.openingDate = new Date(community.openingDate);

                var data = JSON.stringify(community);

                currentCommunity = undefined;   // force refresh on next call

                var deferredObject = $q.defer();
                $http.post(BackendService.getBackEndURL() + "community", data, config).then(
                    function (response) {
                        deferredObject.resolve(response);
                    }, function (response) {
                        deferredObject.reject(response);
                    }
                )
                return deferredObject.promise;
            }
        }
    }]);