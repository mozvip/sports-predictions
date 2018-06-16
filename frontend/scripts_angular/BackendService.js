angular.module('sports-predictions')
	.factory('BackendService', ['$location', '$window', '$http', function ($location, $window, $http) {
		return {
            getBackEndURL: function () {

                if ($location.host() == 'localhost') {
                    // hack for local development without backend
                    //return 'https://test.pronostics2016.com/api/';
                    // hack for development with local backend
                    return 'http://localhost:9000/api/';
                } else {
                    var port = $location.protocol() == 'https' ? '443' : '80';
                    return $location.protocol() + '://' + $location.host() + ':' + port + '/api/';
                }
            },
            getDataURL: function () {

                if ($location.host() == 'localhost') {
                    // hack for local development without backend
                    //return 'https://test.pronostics2016.com/api/';
                    // hack for development with local backend
                    return 'http://localhost:9000';
                } else {
                    var port = $location.protocol() == 'https' ? '443' : '80';
                    return $location.protocol() + '://' + $location.host() + ':' + port;
                }
            },
			get : function( url ) {
				var config = this.getRequestConfig();
				return $http.get( this.getBackEndURL() + url, config );
			},
			getToken: function () {
				return $window.localStorage.getItem('SESSION_ID');
			},
			connect : function( profile ) {
                $window.localStorage.setItem('SESSION_ID', profile.authToken);
			},
			getRequestConfig: function ( contentType ) {
				
				if (contentType == undefined) {
					contentType = 'application/json';
				}
				
				return {
					headers: {
						'Accept': 'application/json', 'Content-Type': contentType, 'Authorization': 'Basic ' + this.getToken()
					}
				}
			}
		}
	}]); 