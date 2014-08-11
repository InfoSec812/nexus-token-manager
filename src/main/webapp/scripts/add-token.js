(function(){
	var app = angular.module('addToken', [ ]);
    
    app.directive('addToken', function() {
        return {
            restrict: 'E',
			templateUrl: 'widgets/add-token.html',
            replace: true,
            controller: ['$log', '$http', '$scope', '$rootScope', function($log, $http, $scope, $rootScope){
                $log.info("Initializing add-token");
                $scope.tokenString = '';
				$scope.selectedUser={};
				$scope.usernames=[];

                this.generate = function() {
					function s4() {
                        return Math.floor((1 + Math.random()) * 0x10000)
                               .toString(16)
                               .substring(1);
                    };
                    $scope.tokenString = s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                            s4() + '-' + s4() + s4() + s4();
                };

				$scope.$on('clearAddTokenForm', function() {
					$scope.tokenString = '';
				});

				this.persist = function() {
					$log.debug("Persisting token string: "+$scope.tokenString);
					var url = 'do/token?token='+$scope.tokenString;
					if ($rootScope.isAdmin) {
						url += "&username="+$scope.selectedUser;
					}
					$http.post(url)
							.success(function(data, status) {
								if ($rootScope.isAdmin) {
									$scope.$emit('setSelectedUser', $scope.selectedUser);
								} else {
									$scope.$emit('userTokenList');
								}
								$scope.$emit('clearAddTokenForm');
								$scope.$emit('reloadUserList');
							})
							.error(function(data, status) {
								if (typeof(data.message)==='string') {
									alert("Failed to add token: "+data.message);
									$scope.$emit('clearAddTokenForm');
								} else {
									alert("Unknown error.");
								}
							});
				}

                $scope.$on('reloadUserList', function(event) {
					if ($rootScope.isAdmin) {
                    	$log.debug('Attempting to load user list.');
						$http.get('do/userlist')
								.success(function(data) {
									$scope.$emit('setUserList', data);
								});
					}
                });

                $scope.$on('setUserList', function(event, data) {
                    $scope.usernames = data.users;
					var currentUserListed = false;
					for (var i=0; i<$rootScope.username.length; i++) {
						if ($scope.usernames[i]===$rootScope.username) {
							currentUserListed = true;
							$scope.selectedUser = $scope.usernames[i];
						}
					}
					if (!currentUserListed) {
						$scope.usernames.push($rootScope.username);
						$scope.selectedUser = $rootScope.username;
					}
                    $scope.userListPopulated=true;
                });

                $scope.$emit('reloadUserList');
            }],
            controllerAs: 'add'
        }
    });
})();