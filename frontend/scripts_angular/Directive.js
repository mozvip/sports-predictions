var showWhenConnected = function (UserService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var showIfConnected = function() {
                if(UserService.isConnected()) {
                    $(element).show();
                } else {
                    $(element).hide();
                }
            };
            showIfConnected();
            scope.$on('connectionStateChanged', showIfConnected);
        }
    };
}
showWhenConnected.$inject = ['UserService'];



var hideWhenConnected = function (UserService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var hideIfConnected = function() {
                if(UserService.isConnected()) {
                    $(element).hide();
                } else {
                    $(element).show();
                }
            };
            hideIfConnected();
            scope.$on('connectionStateChanged', hideIfConnected);
        }
    };
}
hideWhenConnected.$inject = ['UserService'];


var userRank = function(UserService){
// TODO : Faire un dossier partials-view afin de mettre les directives dedans : EX : templateUrl: "partials-view/user-rank.html",
    return {
        restrict: 'E',
        template: '<p ng-class="classCurrentUser(user.email)">{{user.name}} : {{user.email}}</p><h4>Score : {{user.currentScore}}</h4><br/>',
        scope: {
            user: "="
        },
		controller: function($scope, UserService) {
            $scope.classCurrentUser = function(email) {
				if(email === UserService.getCurrentLogin())
					return 'currentUser';
				else 
					return 'notCurrentUser';
            };
        }
    };
}
userRank.$inject = ['UserService'];

var pronostic = function(){
	// TODO : Faire une partials-view
	    return {
        restrict: 'E',
        template: '	<div>{{match.dateTime}} - {{match.group}} - {{match.stadium}}</div><div>{{match.homeTeam}}</div>	  <div>VS</div>	  <div>{{match.awayTeam}}</div>	  <br/>',
        scope: {
            match: "="
        }
    };
}
