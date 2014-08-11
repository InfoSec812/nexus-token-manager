(function(){
	var app = angular.module('nexusmanager', ['login', 'userTokens', 'addToken']);

	app.controller('PageController', ['$http', '$scope', '$rootScope', '$log', function($http, $scope, $rootScope, $log) {
		$rootScope.userinfo = {};
		$rootScope.isAuthenticated = false;
		$rootScope.isAdmin = false;	
		$rootScope.activity=0;
		$rootScope.username='';

		$scope.$on('tokenListControllerLoaded', function(event){ 
				$scope.$emit('checkAuth');
		});

		this.showHelp = function() {
			$rootScope.activity = 7;
		}

		this.showMain = function() {
			$log.debug("Calling token loading function.");
			$scope.$emit('reloadUserList');
			$scope.$emit('userTokenList');
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