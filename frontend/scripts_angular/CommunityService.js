angular.module('sports-predictions')
    .factory('CommunityService', ['$http', '$q', 'BackendService', '$window', function ($http, $q, BackendService, $window) {

        return {
            getCommunity: function () {

                let currentCommunity = $window.localStorage.getItem('community');

                var deferredObject = $q.defer();
                if (currentCommunity != undefined) {
                    let community = JSON.parse(currentCommunity);
                    community.openingDate = new Date(community.openingDate);
                    deferredObject.resolve(community);
                } else {
                    $http.get(BackendService.getBackEndURL() + "community").then(
                        function (response) {
                            let community = response.data;
                            community.openingDate = new Date(community.openingDate);
                            $window.localStorage.setItem('community', JSON.stringify(community));
                            deferredObject.resolve(community);
                        }, function (response) {
                            deferredObject.reject(response);
                        }
                    )
                }
                return deferredObject.promise;
            },

            save: function (community) {
                var config = BackendService.getRequestConfig();

                var data = JSON.stringify(community);

                $window.localStorage.setItem('community', data);

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