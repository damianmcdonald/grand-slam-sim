'use strict';

grandSlamSimApp.controller('AdminController', ['$scope', '$modal', '$location', '$interval', '$http', function($scope, $modal, $location, $interval, $http) {
		
	$scope.showStep2 = false;
	$scope.showAddButton = false;
	$scope.showMoveItems = false;
	
	$scope.validUser = false;
	$scope.tournaments = [];
	$scope.players = [];
	$scope.models = {
		selected: null,
		lists: {"players": []}
	};
	$scope.newTournament = {
		name: ''
	  };
	
	$scope.removeTournament = function(tournamentName) {
		$http.delete('admin/delete/'+tournamentName).
		  then(function(response) {
			getTournamentList();
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
		  }); 
	};
	
	$scope.verifyUniqueName = function(tournamentName) {
		$http.get('admin/checkUnique/'+tournamentName).
		  then(function(response) {
			if(response.data) {
				validationPassed();
				showStep2();
			} else {
				validationFailed();
				resetStep2();
			}
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
			resetStep2();
		  }); 
	};
	
	$scope.loadPlayers = function() {
		$http.get('admin/loadPlayers').
		  then(function(response) {
			$scope.players = response.data;
			$scope.models.lists.players = response.data;
			showPlayersLoaded();
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
			resetPlayersLoaded();
		  }); 
	};
	
	$scope.addTournament = function(tournamentName) {	
		var arr = $scope.models.lists.players;
		for(var i=0; i<arr.length; i++) {
			arr[i].seed = i+1;
		}
	
		$http.post('admin/add/'+tournamentName, arr).
		  then(function(response) {
			showStep3();
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
		  }); 
		  
	};
	
	 $scope.uploadFile = function() {
	  function doMultiPartPost(file, fileName) {
		  var fd = new FormData();
		  fd.append('file', file);
		  fd.append('filename', file.name);
		  $http.post("admin/uploadPlayers", fd, {
			  transformRequest: angular.identity,
			  headers: {'Content-Type': undefined}
		  }).success(function (data, status) {
			  $scope.players = data;
			  $scope.models.lists.players = data;
			  $scope.showAddButton = true;
		  }).error(function(data, status, headers, config) {
			  $scope.showAddButton = false;
			  console.log("An error has occured with status: " + status + " and error message: " + data);
		  });
	  }
	  // check if browser supports HTML5 FromData
	  if (typeof(window.FormData) === 'undefined') {
		console.log("This browser version is not supported by GrandSlamSim. Please upgrade your browser.");
		return;
	  }
	  var f = document.getElementById("upload-file").files[0];
	  doMultiPartPost(f, f.name);
	}
	
	  var getTournamentList = function() {
		$http.get('admin/list').
		  then(function(response) {
			$scope.tournaments = response.data;
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
		  });
	  };
	  
	  var showStep2 = function() {
		$scope.showStep2 = true;
	  };
	  
	  var showPlayersLoaded = function() {
		$scope.showMoveItems = true;
		$scope.showAddButton = true;
	  };
	  
	  var resetPlayersLoaded = function() {
		$scope.showMoveItems = false;
		$scope.showAddButton = false;
	  };
	  
	  var showStep3 = function() {
		getTournamentList();
		$scope.models.selected = null;
		$scope.models.lists.players = [];
		$scope.players = [];
		$scope.newTournament.name = '';
		$scope.showStep2 = false;
		$scope.showAddButton = false;
		$scope.showMoveItems = false;
		document.getElementById('add-tournament-name-div').classList.remove('has-error');
		document.getElementById('add-tournament-name-div').classList.remove('has-success');
	  };
	  
	  var resetStep2 = function() {
		$scope.showStep2 = false;
	  };
	  
	  var validationPassed = function() {
		document.getElementById('add-tournament-name-div').classList.add('has-success');
		document.getElementById('add-tournament-name-error').classList.add('hide');
		document.getElementById('add-tournament-name-div').classList.remove('has-error');
	  };
	  
	  var validationFailed = function() {
		document.getElementById('add-tournament-name-div').classList.add('has-error');
		document.getElementById('add-tournament-name-div').classList.remove('has-success');
		document.getElementById('add-tournament-name-error').classList.remove('hide');
	  };
	  
	  $http.get('admin/checkAdmin').
		  then(function(response) {
			$scope.validUser = true;
			getTournamentList();
		  }, function(response) {
			$scope.validUser = false;
		  }); 

}]);
	