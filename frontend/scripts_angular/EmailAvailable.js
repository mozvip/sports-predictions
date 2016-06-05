angular.module('sports-predictions').directive('emailAvailable', function ($q, $http, BackendService) {

    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {

            ctrl.$asyncValidators.emailAvailable = function (modelValue, viewValue) {

                if (ctrl.$isEmpty(modelValue)) {
                    // consider empty model valid
                    return $q.when();
                }

                var def = $q.defer();

                var config = {
                    headers : { 'Accept' : 'application/json'}
                };
			
                $http
                    .get(BackendService.getBackEndURL() + 'user/emailAvailable?email='+modelValue,config)
                    .then(function(response){
                        if (response.data) {
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