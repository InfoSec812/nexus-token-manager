(function(){
	var app = angular.module('login', [ ]);

	app.directive('login', function() {
		return {
			restrict: 'E',
			templateUrl: 'widgets/login.html',
			controller:['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log) {
				$scope.credentials = {};

				$scope.doLogin = function() {
					$log.info("Sending login request.");
					$http.get('do/login?username='+$scope.credentials.username+'&password='+$scope.credentials.password)
						.success(function(data) {
							$scope.credentials = {};
							$log.info("Login successful");
							
							$rootScope.userinfo = data;
							$rootScope.isAuthenticated = true;
							var permissions = data.data.clientPermissions.permissions;
							for (var i=0; i<permissions.length; i++) {
								if (	!$rootScope.isAdmin && permissions[i].id==="security:*") {
									$log.warn("ADMIN: "+permissions[i].id);
									$rootScope.isAdmin = true;
								}
							}
							$rootScope.activity=2;
						});
				}
			}],
			controllerAs: 'login'
		}
	});
})();