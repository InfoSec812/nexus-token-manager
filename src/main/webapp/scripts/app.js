(function(){
	var app = angular.module('nexusmanager', ['login', 'userTokens', 'addToken', 'userSelector']);

	app.controller('PageController', ['$http', '$scope', '$rootScope', '$log', function($http, $scope, $rootScope, $log) {
		$rootScope.userinfo = {};
		$rootScope.isAuthenticated = false;
		$rootScope.isAdmin = false;	
		$rootScope.activity=0;
		$rootScope.usernames=[];
		$rootScope.username='';

		$http({method: 'OPTIONS', url: 'do/login', responseType: 'json'})
				.success(function(data) {
					$scope.credentials = {};
					$log.info("User is logged in: "+data);

					$rootScope.userinfo = data;
					$rootScope.isAuthenticated = true;
					$rootScope.username=data.data.clientPermissions.loggedInUsername;
					var permissions = data.data.clientPermissions.permissions;
					for (var i=0; i<permissions.length; i++) {
						if (	!$rootScope.isAdmin && permissions[i].id==="security:*") {
							$log.warn("ADMIN: "+permissions[i].id);
							$rootScope.isAdmin = true;
						}
					}
					$rootScope.activity=2;
				});

		this.showTokenList = function() {
			$log.debug("Calling token loading function.");
			$scope.$emit('userTokenList');
		}

		this.addToken = function() {
			$log.debug("Displaying add token form.");
			$rootScope.activity=1;
		}

		this.logout = function() {
			$log.debug("Sending HTTP request to perform server-side logout.");
			$http.get('do/logout')
				.success(function(data) {
					$log.info("Logging out for user '"+$rootScope.userinfo.data.clientPermissions.loggedInUsername+"'");
					$rootScope.userinfo = {};
				});
			$rootScope.isAuthenticated = false;
			$rootScope.isAdmin = false;
			$rootScope.activity = 0;
		};
	}]);
})();