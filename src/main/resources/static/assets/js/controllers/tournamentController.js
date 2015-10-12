'use strict';

grandSlamSimApp.controller('TournamentController', ['$scope', '$modal', '$location', '$interval', '$http', '$routeParams', 'toaster', 'TournamentSocket', function($scope, $modal, $location, $interval, $http, $routeParams, toaster, tournamentSocket) {
		
	var tournamentName = $routeParams.tournamentName;
	// size of the spacing to display between each round matchup
	var MATCHUP_SPACING = 130;
	var ROUNDS_WIDTH = 225;
	var MIDPOINT_BUFFER = 30;
	var ROUNDS_SPACE_BUFFER = 60;
	var html = '';
	var finalsRoundId, finalsRoundPosition;
	
	$scope.players = [];
	$scope.matchStats = [];
	$scope.max;
	$scope.dynamic = 0;
	$scope.completed = false;

	// start button event handler
	$scope.startTournament = function() {
		$http.post('simulator/start/'+tournamentName).
		  then(function(response) {
			document.getElementById('startButton').disabled = true;
		  }, function(response) {
			console.log("An error has occured with status: " + response.status + " and error message: " + response.statusText);
		  });
	};

	// modal dialog
	$scope.openWinnerModal = function (size) {
		var modalInstance = $modal.open({
		  animation: false,
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

	$scope.openInvalidTournamentModal = function (size) {
		var modalInstance = $modal.open({
		  animation: false,
		  templateUrl: 'invalidTournamentModalContent.html',
		  controller: 'InvalidTournamentModalController',
		  size: size,
		  resolve: {
			invalidTournamentName: function () {
			  return $scope.invalidTournamentName;
			}
		  }
		});

		modalInstance.result.then(function (selectedItem) {
		}, function () {
		  $log.info('Modal dismissed at: ' + new Date());
		});
	};

	// hook into the window resize event
	addEvent(window, "resize", function(event) {
	  positionRounds(document.getElementById("tournament-div").offsetTop);
	});

	function addEvent(object, type, callback) {
	    if (object == null || typeof(object) == 'undefined') return;
	    if (object.addEventListener) {
	        object.addEventListener(type, callback, false);
	    } else if (object.attachEvent) {
	        object.attachEvent("on" + type, callback);
	    } else {
	        object["on"+type] = callback;
	    }
	};

	/* Tournament Table draw functions */
	function initTournament(rounds, maxRoundsSize, matches) {
		// reset the html buffer
		html = '';
		
	    var height = ((rounds[0]*MATCHUP_SPACING));
		var width = ((rounds.length+1)*ROUNDS_WIDTH);
	    document.getElementById("tournament-div").style.height=height+"px";
	    document.getElementById("tournament-div").style.width=width+"px";

		html+='<table id="tournament-draw-table" class="table">';
		html+='<thead>';
		html+='<tr>';
		for(var i=0;i<rounds.length;i++) {
	        html+='<th>'+getRoundName(rounds[i])+'</th>';
	    }
		// add Winner to the rounds
		html+='<th>Winner</th>';
		
		html+='</tr>';
		html+='</thead>';
		
		html+='<tbody>';
		html+='<tr>';
	    for(var i=0;i<rounds.length;i++) {
			html+='<td class="tournament-round">';
	        drawRounds(rounds[i],i, maxRoundsSize);
			html+='</td>';
	    }
			
	    // once we have finished the rounds, draw the winner
	    drawWinner(maxRoundsSize);
		
		html+='</tr>';
		
		html+='</tbody>';
		
		html+='</table>';

	    document.getElementById('tournament-div').innerHTML = html;
			
	    var tournamentRounds = document.getElementsByClassName("tournament-round");
	    for(var i=0; i<tournamentRounds.length; i++) {
	        tournamentRounds[i].style.height = height+"px";
	    }
		
	    positionRounds(document.getElementById("tournament-div").offsetTop);

	    // position the winner round
	    var topValue = document.getElementById(finalsRoundId).offsetTop;
	    var winnerTop = topValue+MIDPOINT_BUFFER;
	    document.getElementById("round-winner").style.top = winnerTop+"px";
	    document.getElementById("round-winner").setAttribute('gss-top', finalsRoundPosition+MIDPOINT_BUFFER);

		fillRounds(matches, 0);

	}

	function fillRounds(matches, pos) {
		for(var i=0; i<matches.length; i++) {
	        document.getElementById('round-'+pos+'-match-'+i+'-playerA-img').setAttribute('src', 'assets/img/players/'+matches[i].playerA.image);
			document.getElementById('round-'+pos+'-match-'+i+'-playerA-name').value = matches[i].playerA.formattedName;
	        document.getElementById('round-'+pos+'-match-'+i+'-playerB-img').setAttribute('src', 'assets/img/players/'+matches[i].playerB.image);
			document.getElementById('round-'+pos+'-match-'+i+'-playerB-name').value = matches[i].playerB.formattedName;
		}
	}

	function fillWinner(winner) {
	    document.getElementById('round-winner-img').setAttribute('src', 'assets/img/players/'+winner.winner.image);
	    document.getElementById('round-winner-name').value = winner.winner.formattedName;
	}

	function fillSingleRound(matchResult) {
		document.getElementById('round-'+matchResult.round+'-match-'+matchResult.matchPosition+'-score').innerHTML = matchResult.score;
	    if(!matchResult.finalMatch) {
	        if (matchResult.playerA == true) {
	            document.getElementById('round-' + matchResult.newRound + '-match-' + matchResult.newPosition + '-playerA-img').setAttribute('src', 'assets/img/players/' + matchResult.winner.image);
	            document.getElementById('round-' + matchResult.newRound + '-match-' + matchResult.newPosition + '-playerA-name').value = matchResult.winner.formattedName;
	        } else {
	            document.getElementById('round-' + matchResult.newRound + '-match-' + matchResult.newPosition + '-playerB-img').setAttribute('src', 'assets/img/players/' + matchResult.winner.image);
	            document.getElementById('round-' + matchResult.newRound + '-match-' + matchResult.newPosition + '-playerB-name').value = matchResult.winner.formattedName;
	        }
	    }
	}

	function drawWinner(max){
		// topOffset is changed after the table is rendered
	    html+= '<td class="tournament-round">';
		
	    html+= '<div id="round-winner" class="matchups" style="position:absolute;">';
	    html+= '<table class="playerTable">';
	    html+= '<tr>';
	    html+= '<td>';
	    html+= '<img id="round-winner-img" class="img-thumbnail">';
	    html+= '</td>';
	    html+= '<td>';
	    html+= '<input type="text" id="round-winner-name" readonly="readonly">';
	    html+= '</td>';
	    html+= '</tr>';
	    html+= '<tr>';
	    html+= '<td>';
	    html+= '</td>';
	    html+= '<td>';
		html+= '<span id="round-winner-score"></span>';
	    html+= '</td>';
	    html+= '</tr>';
	    html+= '</table>';
	    html+= '</div>';
		
		html+= '</td>';

	}

	function drawRounds(numMatches, pos, max) {
	    var topOffset, currTopOffset, currTopOffsetInc;
		var isFinalRound = false;
		
		if (numMatches == 1) { // finals
			isFinalRound = true;
	    } 
		
	    topOffset = document.getElementById("tournament-div").clientHeight / (numMatches*2);
	    
		// find the relative distance for which to position nested matchups
		currTopOffsetInc = document.getElementById("tournament-div").clientHeight / numMatches;

	    // set the intial top offset for the first matchup, this will subsequently be incremented in the loop
	    currTopOffset = topOffset;
	    
	    for(var i=0; i<numMatches; i++) {

			if(isFinalRound) {
	            finalsRoundId = 'round-'+pos+'match-'+i;
	            finalsRoundPosition = currTopOffset;
	        }

	        html+= '<div id="round-'+pos+'match-'+i+'" class="matchups" gss-top='+currTopOffset+' style="position:absolute; top:'+currTopOffset+'px;">';
	        html+= '<table class="playerTable">';
	        html+= '<tr>';
	        html+= '<td>';
	        html+= '<img id="round-'+pos+'-match-'+i+'-playerA-img" class="img-thumbnail">';
	        html+= '</td>';
	        html+= '<td>';
	        html+= '<input type="text" id="round-'+pos+'-match-'+i+'-playerA-name" readonly="readonly">';
	        html+= '</td>';
	        html+= '</tr>';
	        html+= '<tr>';
	        html+= '<td>';
	        html+= '<img id="round-'+pos+'-match-'+i+'-playerB-img" class="img-thumbnail">';
	        html+= '</td>';
	        html+= '<td>';
	        html+= '<input type="text" id="round-'+pos+'-match-'+i+'-playerB-name" readonly="readonly">';
	        html+= '</td>';
	        html+= '</tr>';
	        html+= '<tr>';
	        html+= '<td>';
	        html+= '</td>';
	        html+= '</tr>';
	        html+= '</table>';
			html+='<div class="match-score"><span id="round-'+pos+'-match-'+i+'-score"></span></div>';
	        html+= '</div>';
	        currTopOffset=currTopOffset+currTopOffsetInc;
	    }

	}

	function positionRounds(offSet) {
	    var rounds = document.querySelectorAll('[gss-top]');
	    for(var i=0; i<rounds.length; i++) {
	        var prevOffSet = parseInt(rounds[i].getAttribute('gss-top'));
	        rounds[i].style.top=(prevOffSet+offSet)+'px';
	    }
	}

	function getRoundName(position) {
	    switch(position) {
		case 64:
	        return "Round 1";
		case 32:
	        return "Round 2";
		case 16:
	        return "Round 3";
	    case 8:
	        return "Round of 16";
	    case 4:
	        return "Quarter Finals";
	    case 2:
	        return "Semi Finals";
	    case 1:
	        return "Finals";        
	    default:
	        return "Round "+position;
	    }
	}

	/* Startup actions */
	var initStompClient = function() {
		tournamentSocket.init('ws');
		
		tournamentSocket.connect(function(frame) {
			tournamentSocket.subscribe("/topic/tournament.started."+tournamentName, function(message) {
				document.getElementById('startButton').disabled = true;
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
				var modalResult = { 'winner': winner.winner, 'tournamentName': tournamentName };
				$scope.tournamentResult = modalResult;
				$scope.dynamic += 1;
				$scope.openWinnerModal();
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
		$scope.max = response.data.players.length-1;
		setTimeout(function(){ window.dispatchEvent(new Event('resize')); }, 100);
	  }, function(response) {
	  	var modalResult = { 'tournamentName': tournamentName };
		$scope.invalidTournamentName = modalResult;
		$scope.openInvalidTournamentModal();
		$location.path("/select");
	  });

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

grandSlamSimApp.controller('InvalidTournamentModalController', function ($scope, $modalInstance, invalidTournamentName) {

	$scope.invalidTournamentName = invalidTournamentName;

	$scope.ok = function () {
		$modalInstance.close('');
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

});
	