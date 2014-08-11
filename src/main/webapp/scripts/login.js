(function(){
	var app = angular.module('login', [ ]);

	app.directive('login', function() {
		return {
			restrict: 'E',
			templateUrl: 'widgets/login.html',
            replace: true,
			controller:['$scope', '$rootScope', '$http', '$log', function($scope, $rootScope, $http, $log) {
				$scope.credentials = {};

				$http({method: 'OPTIONS', url: 'do/login', responseType: 'json'})
						.success(function(data) {
							$scope.credentials = {};
							$log.info("User is logged in: "+data.data.clientPermissions.loggedInUsername);

							$rootScope.userinfo = data;
							$rootScope.isAuthenticated = true;
							$rootScope.username=data.data.clientPermissions.loggedInUsername;
							var permissions = data.data.clientPermissions.permissions;
							for (var i=0; i<permissions.length; i++) {
								if (	!$rootScope.isAdmin && permissions[i].id==="security:users") {
									if (permissions[i].value==15) {
										$log.warn("ADMIN: "+permissions[i].id);
										$rootScope.isAdmin = true;
									}
								}
							}
						});

				$scope.handleAuthData = function(data) {
					$scope.credentials = {};
					$log.info("User is logged in: "+data.data.clientPermissions.loggedInUsername);

					$rootScope.userinfo = data;
					$rootScope.isAuthenticated = true;
					$rootScope.username=data.data.clientPermissions.loggedInUsername;
					var permissions = data.data.clientPermissions.permissions;
					for (var i=0; i<permissions.length; i++) {
						if (	!$rootScope.isAdmin && permissions[i].id==="security:users") {
							if (permissions[i].value==15) {
								$log.warn("ADMIN: "+permissions[i].id);
								$rootScope.isAdmin = true;
							}
						}
					}
					$scope.$emit('reloadTokenUserList');
					$scope.$emit('reloadUserList');
					$scope.$emit('userTokenList');
				};

				$scope.$on('checkAuth', function() {
					$http({method: 'OPTIONS', url: 'do/login', responseType: 'json'})
						.success($scope.handleAuthData);
				});

				$scope.doLogin = function() {
					$log.info("Sending login request.");
					$http.get('do/login?username='+$scope.credentials.username+'&password='+$scope.credentials.password)
						.success($scope.handleAuthData);
				}
			}],
			controllerAs: 'login'
		}
	});
})();