var AuthInterceptor = function ($q, $location) {
	return { 'responseError' : function(rejection){
		if(rejection.status === 401)
			$location.url('login');
		else
			$q.defer().resolve(rejection);
		}
	};
}
AuthInterceptor.$inject = ['$q', '$location'];