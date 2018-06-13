
angular.module('sports-predictions')
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {

        $routeProvider
            .when('/ranks', {
                templateUrl: '/views/ranks.html',
                authorized: true,
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }]
                },
                controller: 'RanksController'
            })
            .when('/pronostic', {
                controller: 'PredictionsController',
                authorized: true,
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }],
					check:['CommunityService', '$location', function(CommunityService, $location){
						CommunityService.getCommunity().then(function(result){
							if(result.groupsAccess == 'N')
								$location.path('ranks');
						});
					}]
                },
                templateUrl: '/views/predictions.html'
            })
            .when('/pronostic-final', {
                controller: 'PronosticFinalController',
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }],                    
					check:['CommunityService', '$location', function(CommunityService, $location){
						CommunityService.getCommunity().then(function(result){
							if(result.finalsAccess == 'N')
								$location.path('ranks');
						});
					}]
                },
                authorized: true,
                templateUrl: '/views/pronosticFinal.html'
            })
            .when('/pronostic-final-delegate', {
                controller: 'PronosticFinalController',
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }],                    
					check:['CommunityService', '$location', function(CommunityService, $location){
						CommunityService.getCommunity().then(function(result){
							if(result.finalsAccess == 'N')
								$location.path('ranks');
						});
					}]
                },
                authorized: true,
                templateUrl: '/views/pronosticFinalDelegate.html'
            })
            .when('/login', {
                controller: 'LoginController',
                templateUrl: '/views/login.html',
                resolve: {
                    count: ['UserService', function (UserService) {
                        return UserService.getCount();
                    }]
                },
                authorized: false
            })
            .when('/coming-soon', {
                controller: 'ComingSoonController',
                templateUrl: '/views/coming-soon.html',
                resolve: {
                    count: ['UserService', function (UserService) {
                        return UserService.getCount();
                    }]
                },
                authorized: false
            })
            .when('/sign-up', {
                controller: 'SignupController',
                templateUrl: '/views/signup.html',
                authorized: false
            })
            .when('/teams', {
                controller: 'TeamsController',
                templateUrl: '/views/teams.html',
                authorized: true,
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function (CommunityService) {
                        return CommunityService.getCommunity();
                    }],
                }
            })
            .when('/admin', {
                templateUrl: '/views/admin.html',
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }]

                },
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
				resolve: {
                    community: ['CommunityService', function(CommunityService) {
                        return CommunityService.getCommunity();
                    }]
                },
                controller: 'ResetPasswordController',
                authorized: false
            })
            .when('/stats', {
                templateUrl: '/views/stats.html',
				resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }]
                },
				controller: 'StatsController',
                authorized: true
            })
            .when('/user-profile', {
                templateUrl: '/views/user-profile.html',
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }]
                },
                controller: 'UserProfileController',
                authorized: true
            })
			.when('/stat-match/:matchNum', {
				templateUrl: '/views/match-stat.html',
				resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }]
                },
				controller: 'MatchStatController',
				authorized: true
			})
            .otherwise({
                redirectTo: '/pronostic',
                resolve: {
                    currentUser: ['UserService', function (UserService) {
                        return UserService.getCurrentUser();
                    }],
                    check:['CommunityService', '$location', function(CommunityService, $location){
                        CommunityService.getCommunity().then(function(community){
                            if (new Date(community.openingDate) > new Date()) {
                                $location.path('coming-soon');
                            }
                        });
                    }]
                }
            });

        $locationProvider.html5Mode(false);
    }]);

angular.module('sports-predictions')
    .run(['$rootScope', '$location', 'UserService', function ($rootScope, $location, UserService) {
        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            if (next.authorized && !UserService.isConnected()) {
                $location.url('login');
            }
        });
    }]);