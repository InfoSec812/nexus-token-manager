(function(){
	var app = angular.module('userSelector', [ ]);
    
    app.directive('userSelector', function() {
        return {
            restrict: 'E',
            templateUrl: 'widgets/user-selector.html',
            controller: ['$log', '$http', '$rootScope', function($log, $http, $rootScope) {
            }],
            controllerAs: 'userSelector'
        };
    });
})();