'use strict';

/* Controllers */
grandSlamSimApp.controller('SelectController', ['$scope', '$modal', '$location', '$interval', '$http', function($scope, $modal, $location, $interval, $http) {
		
		$scope.hasTournaments = true;
		$scope.tournaments = [];
		
			$http.get('select/list').
			  then(function(response) {
				$scope.tournaments = response.data;
				if($scope.tournaments.length == 0) {
					$scope.hasTournaments = false;
				}
			  }, function(response) {
				alert("An error has occured with status: " + response.status + " and error message: " + response.statusText);
			  });
		  
	}]);
	