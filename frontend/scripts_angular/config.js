
angular.module('sports-predictions')
    .config(['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {
        
        $routeProvider
            .when('/ranks', {
                templateUrl: '/views/ranks.html',
                authorized: true,
                controller: 'RanksController'
            })
            .when('/pronostic', {
                controller: 'PredictionsController',
                authorized: true,
                resolve: { currentUser: ['UserService', function(UserService) {
                    return UserService.getCurrentUser();
                }]},                
                templateUrl: '/views/pronostic.html'
            })
            .when('/pronostic-final', {
                controller: 'PronosticFinalController',
                authorized: true,
                templateUrl: '/views/pronosticFinal.html'
            })
            .when('/login', {
                controller: 'LoginController',
                templateUrl: '/views/login.html',
                authorized: false
            })
            .when('/sign-up', {
                controller: 'SignupController',
                templateUrl: '/views/signup.html',
                authorized: false
            })
            .when('/admin', {
                templateUrl: '/views/admin.html',
                resolve: { currentUser: ['UserService', function(UserService) {
                    return UserService.getCurrentUser();
                }]},
                authorized: true,
                controller: 'AdminController'
            })
            .when('/forget-password', {
                templateUrl: '/views/forgetPassword.html',
                authorized: false,
                controller: 'ForgetController'
            })
            .when('/forget-password/:email/:token', {
                templateUrl: '/views/changePassword.html',
                controller: 'ResetPasswordController',
                authorized: false
            })
            .when('/your-stats', {
                templateUrl: '/views/yourStats.html',
                authorized: true
            })
            .when('/user-profile', {
                templateUrl: '/views/user-profile.html',
                resolve: { currentUser: ['UserService', function(UserService) {
                    return UserService.getCurrentUser();
                }]},
                controller: 'UserProfileController',
                authorized: true
            })
            .otherwise({
                redirectTo: '/pronostic',
                resolve: { currentUser: ['UserService', function(UserService) {
                    return UserService.getCurrentUser();
                }]}                
            });

        $locationProvider.html5Mode(false);
    }]);

angular.module('sports-predictions')
    .run(['$rootScope', '$location', 'UserService', function ($rootScope, $location, UserService) {
        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            if (next.authorized && !UserService.isConnected())
                $location.url('login');
        });
    }]);