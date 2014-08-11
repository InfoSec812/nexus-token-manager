(function(){
	var app = angular.module('addToken', [ ]);
    
    app.directive('addToken', function() {
        return {
            restrict: 'E',
			templateUrl: 'widgets/add-token.html',
            controller: ['$log', '$http', '$rootScope', function($log, $http, $rootScope){
                this.tokenString = '';
				this.status = '';

                this.generate = function() {
					function s4() {
                        return Math.floor((1 + Math.random()) * 0x10000)
                               .toString(16)
                               .substring(1);
                    };
                    this.tokenString = s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                            s4() + '-' + s4() + s4() + s4();
                };

				this.persist = function() {
					$http.put('do/token?token='+this.tokenString)
							.success(function(data, status) {
								$log.debug("Calling token loading function.");
								$scope.$emit('userTokenList');
							})
							.error(function(data, status) {
								alert("Failed to add token!");
							});
				}
            }],
            controllerAs: 'add'
        }
    });
})();