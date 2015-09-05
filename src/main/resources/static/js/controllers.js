'use strict';

/* Controllers */

angular.module('grandSlamSim.controllers', ['toaster'])
	.controller('TournamentController', ['$scope', '$location', '$interval', '$http', 'toaster', 'TournamentSocket', function($scope, $location, $interval, $http, toaster, tournamentSocket) {
		
		$scope.players = [];
		$scope.matchStats = [];
	
		$scope.startTournament = function() {
			$http.get('/simulator/start').
			  then(function(response) {
				// this callback will be called asynchronously
				// when the response is available
			  }, function(response) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
			  });
		};

		var initStompClient = function() {
			tournamentSocket.init('/ws');
			
			tournamentSocket.connect(function(frame) {
				tournamentSocket.subscribe("/topic/match.stats", function(message) {
					var matchStat = JSON.parse(message.body);
					$scope.matchStats.push(matchStat.headline);
				});
				tournamentSocket.subscribe("/topic/match.results", function(message) {
					var matchResult = JSON.parse(message.body);
					fillSingleRound(matchResult);
				});
				tournamentSocket.subscribe("/topic/match.winner", function(message) {
					var winner = JSON.parse(message.body);
					fillWinner(winner);
				});
			}, function(error) {
				toaster.pop('error', 'Error', 'Connection error ' + error);
				
		    });
		};
		  
		initStompClient();
		
		// get tournament details
		$http.get('/simulator/details').
		  then(function(response) {
			$scope.tournament = response.data;
				console.log(response.data);
			initTournament(response.data.rounds, response.data.maxMatches, response.data.matches);
		  }, function(response) {
			console.log("Error == " + response.data);
		  });
	}]);
	