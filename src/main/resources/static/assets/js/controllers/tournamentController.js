'use strict';

/* Controllers */
grandSlamSimApp.controller('TournamentController', ['$scope', '$modal', '$location', '$interval', '$http', '$routeParams', 'toaster', 'TournamentSocket', function($scope, $modal, $location, $interval, $http, $routeParams, toaster, tournamentSocket) {
		
		var tournamentName = $routeParams.tournamentName;
		
		$scope.players = [];
		$scope.matchStats = [];
		$scope.max;
		$scope.dynamic = 0;
		$scope.completed = false;
		
		$scope.startTournament = function() {
			$http.post('simulator/start/'+tournamentName).
			  then(function(response) {
				$('#startButton').prop('disabled', true);
			  }, function(response) {
				alert("An error has occured with status: " + response.status + " and error message: " + response.statusText);
			  });
		};

		var initStompClient = function() {
			tournamentSocket.init('ws');
			
			tournamentSocket.connect(function(frame) {
				tournamentSocket.subscribe("/topic/tournament.started."+tournamentName, function(message) {
					$('#startButton').prop('disabled', true);
				});
				tournamentSocket.subscribe("/topic/match.stats."+tournamentName, function(message) {
					var matchStat = JSON.parse(message.body);
					$scope.matchStats.push(matchStat.headline);
				});
				tournamentSocket.subscribe("/topic/match.results."+tournamentName, function(message) {
					var matchResult = JSON.parse(message.body);
					fillSingleRound(matchResult);
					$scope.dynamic += 1;
				});
				tournamentSocket.subscribe("/topic/match.winner."+tournamentName, function(message) {
					var winner = JSON.parse(message.body);
					fillWinner(winner);
					var modalResult = { 'winner': winner.winner, 'tournamentName': $scope.tournament.name };
					$scope.tournamentResult = modalResult;
					$scope.dynamic += 1;
					$scope.open();
					$scope.completed = true;
				});
			}, function(error) {
				toaster.pop('error', 'Error', 'Connection error ' + error);
				
		    });
		};
		  
		initStompClient();
		
		// get tournament details
		$http.get('simulator/details/'+tournamentName).
		  then(function(response) {
			$scope.tournament = response.data;
			initTournament(response.data.rounds, response.data.maxMatches, response.data.matches);
			$scope.max = response.data.players.length;
		  }, function(response) {
			alert("Opps!, this tournment is either in progress or finished and can not be joined.");
			$location.path("/select");
		  });
		  
		  
		  $scope.animationsEnabled = true;
		  $scope.open = function (size) {
			var modalInstance = $modal.open({
			  animation: $scope.animationsEnabled,
			  templateUrl: 'winnerModalContent.html',
			  controller: 'WinnerModalController',
			  size: size,
			  resolve: {
				tournamentResult: function () {
				  return $scope.tournamentResult;
				}
			  }
			});

			modalInstance.result.then(function (selectedItem) {
			}, function () {
			  $log.info('Modal dismissed at: ' + new Date());
			});
		  };

		  $scope.toggleAnimation = function () {
			$scope.animationsEnabled = !$scope.animationsEnabled;
		  };
  
	}]);
	
grandSlamSimApp.controller('WinnerModalController', function ($scope, $modalInstance, tournamentResult) {

	  $scope.tournamentResult = tournamentResult;

	  $scope.ok = function () {
		$modalInstance.close('');
	  };

	  $scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	  };
	});
	