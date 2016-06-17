/**
 * Angular Controller -> LoginController  
 * Login user in euro2016 Predictor
 * login() * Try to log in user input
 **/
angular.module('sports-predictions')
	.controller('MatchStatController', ['$scope', '$location', '$routeParams', 'GamesService', '$linq', 
		function ($scope, $location, $routeParams, GamesService, $linq) {

			$scope.gameStats = { perfect: 0, good: 0, bad: 0};

			$scope.drawStats = function( gameStats ) {
					Highcharts.chart('containerStat', {
					chart: {
						plotBackgroundColor: null,
						plotBorderWidth: null,
						plotShadow: false,
						type: 'pie'
					},
					title: {
						text: '',
					},
					tooltip: {
						pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
					},
					plotOptions: {
						pie: {
							allowPointSelect: true,
							cursor: 'pointer',
							dataLabels: {
								enabled: true,
								format: '<b>{point.name}</b>: {point.percentage:.1f} %',
								style: {
									color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
								}
							}
						}
					},
					exporting:{
						enabled: false
					},	
					credits: {
						enabled: false
					},
					series: [{
						name: 'Pronostic',
						colorByPoint: true,
						data: [{
							color: '#4caf50',
							name: '% de joueurs ayant trouvé le bon score',
							y: gameStats.perfect / gameStats.total
						}, {
							color:  '#F2A200',
							name: '% de joueurs ayant trouvé la bonne tendance',
							y: gameStats.good / gameStats.total,
							sliced: true,
							selected: true
						}, {
							color: '#E31937',
							name: '% de joueurs ayant un mauvais pronostic',
							y: gameStats.bad / gameStats.total
						}]
					}]
				});
			}
				
			$scope.init = function(){
				GamesService.getGameStats($routeParams.matchNum).then(
					function( response ){
						$scope.gameStats = response;
						$scope.drawStats( response );
					}, function( reponse ) {
						Notification.error( 'Match introuvable' );
					}
				)
			}
			
			$scope.back = function(){	
			}
			
			$scope.classFlagTeam = function(nameTeam, type) {
				var linking = {
					"France" : "FRA",
					"Allemagne" : "GER",
					"Albanie" : "ALB",
					"Autriche" : "AUT",
					"Belgique" : "BEL", 
					"Roumanie" : "ROU",
					"Suisse" : "SUI", 
					"Angleterre" : "ENG", 
					"Russie" : "RUS", 
					"Slovaquie" : "SVK",
					"Galles" : "WAL",
					"Irlande Du Nord" : "NIR", 
					"Pologne" : "POL", 
					"Ukraine" : "UKR",
					"Croatie" :  "CRO",
					"Rep. Tcheque"  : "CZE",
					"Espagne" : "ESP",
					"Turquie" : "TUR",
					"Italie" : "ITA", 
					"Irlande" : "IRL", 
					"Suede" : "SWE", 
					"Hongrie" : "HUN", 
					"Islande" : "ISL",
					"Portugal" : "POR"
				}
				return type+"-"+linking[nameTeam];
			};
}]);