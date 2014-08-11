(function(){
	var app = angular.module('userTokens', [ ]);
    
    app.directive('userTokens', function() {
        return {
			restrict: 'E',
			templateUrl: 'widgets/user-tokens-list.html',
            controller: ['$http', '$rootScope', '$scope', '$log', function($http, $rootScope, $scope, $log) {
				$scope.tokens = [];
                $scope.selected = [];

				$scope.$on('userTokenList', function(event) {
					$log.info("Loading the controller for user-tokens.js");
					$http.get('do/token')
						.success(function(data) {
							$rootScope.activity=2;
							$scope.tokens = data.tokens;
						});
				});
            }],
            controllerAs: 'tokens'
        }
	});
})();