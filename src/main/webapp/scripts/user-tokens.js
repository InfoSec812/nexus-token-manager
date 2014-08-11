(function(){
	var app = angular.module('userTokens', [ ]);
    
    app.directive('userTokens', function() {
        return {
			restrict: 'E',
			templateUrl: 'widgets/user-tokens-list.html',
            replace: true,
            controller: ['$http', '$rootScope', '$scope', '$log', function($http, $rootScope, $scope, $log) {
				$scope.tokenList = [];
				$scope.tokenUsernames=[];
                $scope.selected = [];
				$scope.tokenSelectedUser={};

				this.delete = function() {
					$log.debug("Attempting to delete token: "+$scope.selected.token);
					var url = 'do/token?token='+$scope.selected.token;
					if ($rootScope.isAdmin) {
						if (!($scope.tokenSelectedUser==={})) {
							url += '&username='+$scope.tokenSelectedUser;
						}
					}
					$http.delete(url)
							.success(function(data) {
								$scope.$emit('userTokenList');
							})
							.error(function(data){
								alert("Failed to delete token.");
							});
				};

				$scope.$on('setSelectedUser', function(event, data) {
					for (var i=0; i<$scope.tokenUsernames; i++) {
						$log.debug("Comparing: "+$scope.tokenUsernames[i]+" to "+data);
						if ($scope.tokenUsernames[i]===data) {
							$scope.tokenSelectedUser = $scope.tokenUsernames[i];
						}
					}
					$scope.$emit('userTokenList');
				});

				$scope.$on('userTokenList', function(event) {
					var url = 'do/token';
					if (typeof($scope.tokenSelectedUser)==='string') {
						if ($rootScope.isAdmin) {
							url += '?username='+$scope.tokenSelectedUser;
						}
					}
					$http.get(url)
						.success(function(data) {
							$scope.$emit('setTokenList', data.tokens);
						});
				});

				$scope.$on('setTokenList', function(event, tokenlist) {
					$log.debug('Refreshing token list.');
					$scope.tokenList = tokenlist;
					$scope.selected = $scope.tokenList[0];
					$rootScope.activity=2;
				});

                $scope.$on('reloadTokenUserList', function(event) {
					if ($rootScope.isAdmin) {
                    	$log.debug('Attempting to load user list.');
						$http.get('do/userlist')
								.success(function(data) {
									$scope.$emit('setTokenUserList', data);
								});
					}
                });

                $scope.$on('setTokenUserList', function(event, data) {
                    $scope.usernames = data.users;
					var currentUserListed = false;
					for (var i=0; i<$rootScope.username.length; i++) {
						if ($scope.tokenUsernames[i]===$rootScope.username) {
							currentUserListed = true;
							$scope.tokenSelectedUser = $scope.tokenUsernames[i];
						}
					}
					if (!currentUserListed) {
						$scope.tokenUsernames.push($rootScope.username);
						$scope.tokenSelectedUser = $rootScope.username;
					}
                    $scope.userListPopulated=true;
                });

				this.refresh = function() {
					$scope.$emit('userTokenList');
				};

				$scope.$emit('tokenListControllerLoaded');
            }],
            controllerAs: 'tokens'
        }
	});
})();