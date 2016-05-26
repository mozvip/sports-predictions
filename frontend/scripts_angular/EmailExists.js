angular.module('sports-predictions').directive('emailExists', function ($q, $http, BackendService) {

    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {

            ctrl.$asyncValidators.emailExists = function (modelValue, viewValue) {

                if (ctrl.$isEmpty(modelValue)) {
                    // consider empty model valid
                    return $q.when();
                }

                var def = $q.defer();

                var config = {
                    headers : { 'Accept' : 'application/json'}
                };
			
                $http
                    .get(BackendService.getBackEndURL() + 'user/availability?email='+modelValue,config)
                    .then(function(response){
                        if (!response.data) {
                            def.resolve();
                        } else {
                            def.reject();
                        }
                    }, function(response){
                            def.reject();
                    });

                return def.promise;
            };
        }
    };

});