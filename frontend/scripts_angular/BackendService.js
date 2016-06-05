angular.module('sports-predictions')
	.factory('BackendService', ['$location', '$cookies', function ($location, $cookies) {
		return {
			getBackEndURL: function () {

				var port = $location.protocol() == 'https' ? '443' : '80';
				if ($location.host() == 'localhost') {
					// hack for local development without backend
					//return 'https://test.pronostics2016.com/api/';
					// hack for development with local backend
					return 'http://localhost:8080/api/';
				} else {
					return $location.protocol() + '://' + $location.host() + ':' + port + '/api/';
				}
			},
			getToken: function () {
				return $cookies.get('SESSION_ID');
			},
			connect : function( authToken ) {
				$cookies.put('SESSION_ID', authToken);	
			},
			getRequestConfig: function ( contentType ) {
				return {
					headers: {
						'Accept': 'application/json', 'Content-Type': contentType, 'Authorization': 'Basic ' + this.getToken()
					}
				}
			}
		}
	}]);