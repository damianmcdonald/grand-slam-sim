// size of the spacing to display between each round matchup
var MATCHUP_SPACING = 160;
// size of the width of each round
var ROUND_WIDTH = 350;

var COL_SIZE;

/*
$( document ).ready(function() {
    init();
});
*/

function initTournament(rounds, maxRoundsSize, matches) {
    //var rounds = [16,8,4,2];
    COL_SIZE = Math.round(12/(rounds.length+1));
    var width = ((rounds.length+1)*ROUND_WIDTH)+100;
    var height = ((rounds[0]*MATCHUP_SPACING)+100);
    $("#drawtable").css("width", width);
    $("#drawtable").css("height", height);

    for(var i=0;i<rounds.length;i++) {
        drawRounds(rounds[i],i, maxRoundsSize);
    }
    // once we have finished the rounds, draw the winner
    drawWinner(maxRoundsSize);
	
	fillRounds(matches, 0);
}

function fillRounds(matches, pos) {
	for(var i=0; i<matches.length; i++) {
		$('#round-'+pos+'-match-'+i+'-playerA-img').attr('src', 'img/players/'+matches[i].playerA.image);
		$('#round-'+pos+'-match-'+i+'-playerA-name').val(matches[i].playerA.formattedName);
		$('#round-'+pos+'-match-'+i+'-playerB-img').attr('src', 'img/players/'+matches[i].playerB.image);
		$('#round-'+pos+'-match-'+i+'-playerB-name').val(matches[i].playerB.formattedName);
	}
}

function fillWinner(winner) {
	$('#round-winner-img').attr('src', 'img/players/'+winner.winner.image);
	$('#round-winner-name').val(winner.winner.formattedName);
/*
	$('#round-'+matchResult.round+'-match-'+matchResult.matchPosition+'-score').html(matchResult.score);
	$('#round-winner-img').attr('src', 'img/players/'+matchResult.winner.image);
	$('#round-winner-name').val(matchResult.winner.name);
	if(matchResult.playerA == true) {
		$('#round-'+matchResult.round+'-match-'+matchResult.matchPosition+'-playerB-name').css('text-decoration', 'line-through');
	} else {
		$('#round-'+matchResult.round+'-match-'+matchResult.matchPosition+'-playerB-name').css('text-decoration', 'line-through');
	}
	*/
}

function fillSingleRound(matchResult) {
	$('#round-'+matchResult.round+'-match-'+matchResult.matchPosition+'-score').html(matchResult.score);
	if(matchResult.playerA == true) {
		$('#round-'+matchResult.newRound+'-match-'+matchResult.newPosition+'-playerA-img').attr('src', 'img/players/'+matchResult.winner.image);
		$('#round-'+matchResult.newRound+'-match-'+matchResult.newPosition+'-playerA-name').val(matchResult.winner.formattedName);
	} else {
	console.log("************** Player B only! ***************");
		$('#round-'+matchResult.newRound+'-match-'+matchResult.newPosition+'-playerB-img').attr('src', 'img/players/'+matchResult.winner.image);
		$('#round-'+matchResult.newRound+'-match-'+matchResult.newPosition+'-playerB-name').val(matchResult.winner.formattedName);
	}
}



function drawWinner(max){

    var position = ($(".matchups").last().position());
    var top = position.top+35;

    var html = "";
    html += '<div class="col-md-'+COL_SIZE+'">';
    html+= '<h4 class="round-title">Winner</h4>';
    html+= '<div id="round-winner" class="matchups" style="position:absolute; top:'+top+'px;">';
    html+= '<table class="playerTable">';
    html+= '<tr>';
    html+= '<td>';
    html+= '<img id="round-winner-img" class="img-thumbnail">';
    html+= '</td>';
    html+= '<td>';
    html+= '<input type="text" id="round-winner-name">';
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
    html+="</div>";

    $("#drawtable").append(html);

}

function drawRounds(numMatches, pos, max) {
    var topOffset, currTopOffset, currTopOffsetInc;
    if(pos == 0) {
        // if we are dealing with the first round, then establish the initial top offset
        topOffset = 50;
        currTopOffsetInc = MATCHUP_SPACING;
    } else if (numMatches == 1) { // finals
        // for the finals, find the midpoint and draw the matchup
        topOffset = ($("#drawtable").height() / 2)-100;
    } else {
        // when dealing with the other rounds
        // calculate the top offset based on the round for nesting the matchups
        topOffset = (pos+1)*((pos+1)*32);
        // get the size of the largest round
        var sizeVal = (max*MATCHUP_SPACING);
        // find the relative distance for which to position nested matchups
        currTopOffsetInc = sizeVal/numMatches;
    }
    // set the intial top offset for the first matchup, this will subsequently be incremented in the loop
    currTopOffset = topOffset;
    
    var html = "";
    html+= '<div class="col-md-'+COL_SIZE+'">';

    html+= '<h4 class="round-title">'+getRoundName(numMatches, pos)+'</h4>';

    for(var i=0; i<numMatches; i++) {
        html+= '<div id="round-'+pos+'match-'+i+'" class="matchups" style="position:absolute; top:'+currTopOffset+'px;">';
        html+= '<table class="playerTable">';
        html+= '<tr>';
        html+= '<td>';
        html+= '<img id="round-'+pos+'-match-'+i+'-playerA-img" class="img-thumbnail">';
        html+= '</td>';
        html+= '<td>';
        html+= '<input type="text" id="round-'+pos+'-match-'+i+'-playerA-name">';
        html+= '</td>';
        html+= '</tr>';
        html+= '<tr>';
        html+= '<td>';
        html+= '<img id="round-'+pos+'-match-'+i+'-playerB-img" class="img-thumbnail">';
        html+= '</td>';
        html+= '<td>';
        html+= '<input type="text" id="round-'+pos+'-match-'+i+'-playerB-name">';
        html+= '</td>';
        html+= '</tr>';
        html+= '<tr>';
        html+= '<td>';
        html+= '</td>';
        html+= '<td>';
        html+= '<span id="round-'+pos+'-match-'+i+'-score"></span>';
        html+= '</td>';
        html+= '</tr>';
        html+= '</table>';
        html+= '</div>';
        currTopOffset=currTopOffset+currTopOffsetInc;
    }

    html+="</div>";

    $("#drawtable").append(html);
}

function getRoundName(numOfMatches, pos) {
    switch(numOfMatches) {
    case 8:
        return "Round of 16";
    case 4:
        return "Quarter Finals";
    case 2:
        return "Semi Finals";
    case 1:
        return "Finals";        
    default:
        return "Round "+pos;
    } 
}