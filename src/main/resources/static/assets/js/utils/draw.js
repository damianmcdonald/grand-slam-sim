// size of the spacing to display between each round matchup
var MATCHUP_SPACING = 155;
var ROUNDS_WIDTH = 225;
var TOPOFFSET_BASE = 140;
var ROUNDS_SPACE_BUFFER = 60;
var html = '';
var finalsRoundId;

function initTournament(rounds, maxRoundsSize, matches) {
	// reset the html buffer
	html = '';
	
    var height = ((rounds[0]*MATCHUP_SPACING));
	var width = ((rounds.length+1)*ROUNDS_WIDTH);
    document.getElementById("tournament-div").style.height=height+"px";
    document.getElementById("tournament-div").style.width=width+"px";

	html+='<table id="tournament-draw-table" class="table table-striped">';
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
	
	// position the winner round
    var topValue = getPixelInteger(finalsRoundId, 'top');
    var winnerTop = topValue+(ROUNDS_SPACE_BUFFER/2);
    document.getElementById("round-winner").style.top = winnerTop+"px";
	
    var tournamentRounds = document.getElementsByClassName("tournament-round");
    for(var i=0; i<tournamentRounds.length; i++) {
        tournamentRounds[i].style.height = height+"px";
    }
	
	fillRounds(matches, 0);
}

function fillRounds(matches, pos) {
	for(var i=0; i<matches.length; i++) {
        document.getElementById('round-'+pos+'-match-'+i+'-playerA-img').setAttribute('src', 'assets/img/players/'+matches[i].playerA.image);
		document.getElementById('round-'+pos+'-match-'+i+'-playerA-name').value = matches[i].playerA.formattedName;
        document.getElementById('round-'+pos+'-match-'+i+'-playerB-img').setAttribute('src', 'assets/img/players/'+matches[i].playerA.image);
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
	
    html+= '<div id="round-winner" class="matchups" style="position:absolute; top:'+TOPOFFSET_BASE+'px;">';
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
	
    topOffset = (((getPixelInteger('tournament-div', 'height')+TOPOFFSET_BASE) / (numMatches*2)))+TOPOFFSET_BASE;
	// find the relative distance for which to position nested matchups
	currTopOffsetInc = getPixelInteger('tournament-div', 'height')/numMatches;
	
    // set the intial top offset for the first matchup, this will subsequently be incremented in the loop
    currTopOffset = topOffset;
    
    for(var i=0; i<numMatches; i++) {

		if(isFinalRound) finalsRoundId = 'round-'+pos+'match-'+i;

        html+= '<div id="round-'+pos+'match-'+i+'" class="matchups" style="position:absolute; top:'+currTopOffset+'px;">';
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

 function getPixelInteger(elemId, style) {
    var pixels = window
        .getComputedStyle(document.getElementById(elemId),null)
        .getPropertyValue(style)
        .match(/\d+/);
    return parseInt(pixels[0]);
 }