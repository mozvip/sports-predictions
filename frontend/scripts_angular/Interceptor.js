var AuthInterceptor = function ($q, $location) {
	return { 'responseError' : function(rejection){
		// TODO : Implémenter parametre ?returnUrl
		if(rejection.status === 401)
			$location.url('login');
		}
	};
}
AuthInterceptor.$inject = ['$q', '$location'];