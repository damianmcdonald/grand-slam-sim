package com.github.damianmcdonald.grandslamsim.simengine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.domain.Match;
import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.domain.TournamentWinner;

@Component
public class TournamentEngine {
	
	@Autowired private WebSocketMessageDispatcher messageDispatcher;
	private final static List<MatchUp> matchups = new ArrayList<MatchUp>();
	private final static List<Player> players = new ArrayList<Player>();
	
	public Tournament getTournament() {
		matchups.clear();
		players.clear();
		final List<MatchUp> initialMatchups = getInitialMatchUps(loadPlayers());
		final List<Match> matches = initialMatchups
		.stream()
		.map(m -> new Match(m.getPlayerA(), m.getPlayerB(),m.getRound(), m.getMatchPosition()))
		.collect(Collectors.toList()); 
		return new Tournament("Australian Open", players, matches);
	}
	
	private List<Player> loadPlayers() {
		final int DRAW_SIZE = 16;
		players.add(new Player("Novak", "Djokovic", "SRB", 1, seedPlayer(1, DRAW_SIZE)+1, "djokovic.png"));
		players.add(new Player("Ozgur", "Cakir", "TUR",  2, seedPlayer(2, DRAW_SIZE)+1, "cakir.png"));
		players.add(new Player("Andy", "Murray", "GBR", 3, seedPlayer(3, DRAW_SIZE)+1, "murray.png"));
		players.add(new Player("Rafael", "Nadal", "ESP", 4, seedPlayer(4, DRAW_SIZE)+1, "nadal.png"));
		players.add(new Player("Stan", "Wawrinka", "SUI", 5, seedPlayer(5, DRAW_SIZE)+1, "wawrinka.png"));
		players.add(new Player("Thomas", "Berdych", "CZH", 6, seedPlayer(6, DRAW_SIZE)+1, "berdych.png"));
		players.add(new Player("Milos", "Raonic", "CAN",  7, seedPlayer(7, DRAW_SIZE)+1, "raonic.png"));
		players.add(new Player("Richard", "Gasquet", "FRA",  8, seedPlayer(8, DRAW_SIZE)+1, "gasquet.png"));
		players.add(new Player("Marcos", "Baghdatis", "CYR", 9, seedPlayer(9, DRAW_SIZE)+1, "baghdatis.png"));
		players.add(new Player("Mario", "Cilic", "CRO", 10, seedPlayer(10, DRAW_SIZE)+1, "cilic.png"));
		players.add(new Player("Grigor", "Dimitrov", "BUL", 11, seedPlayer(11, DRAW_SIZE)+1, "dimitrov.png"));
		players.add(new Player("Alexander", "Dolgopolov", "UKR", 12, seedPlayer(12, DRAW_SIZE)+1, "dolgopolov.png"));
		players.add(new Player("Roger", "Federer", "SUI", 13, seedPlayer(13, DRAW_SIZE)+1, "federer.png"));
		players.add(new Player("Fabio", "Fognini", "ESP", 14, seedPlayer(14, DRAW_SIZE)+1, "fognini.png"));
		players.add(new Player("Sam", "Groth", "AUS", 15, seedPlayer(15, DRAW_SIZE)+1, "groth.png"));
		players.add(new Player("David", "Ferrer", "ESP", 16, seedPlayer(16, DRAW_SIZE)+1, "ferrer.png"));
		// players.add(new Player("Ernest", "Gulbis", "LAT", 16, seedPlayer(16, DRAW_SIZE)+1, "gulbis.png"));


		players.sort((p1, p2) -> (int) p1.getDrawPosition() - p2.getDrawPosition());
		return players;
	}
	
	private List<MatchUp> getInitialMatchUps(List<Player> roundPlayers) {
		final List<MatchUp> initialMatchups = new ArrayList<MatchUp>();
		for (int i=0; i<roundPlayers.size(); i+=2) {
			final int roundPos = (i==0) ? 0 : i/2;
			initialMatchups.add(new MatchUp(messageDispatcher, roundPlayers.get(i), roundPlayers.get(i+1), 5, getRoundName(roundPlayers.size()), 0, roundPos));
		}
		return initialMatchups;
	}
	
	public void simulateRounds(List<Player> roundPlayers, int round){
		if(roundPlayers.size() == 1) {
			final String message = String.format("Tournament winner is: %s", roundPlayers.get(0).getFormattedName());
			messageDispatcher.dispatchMatchStats(message);
			
			final TournamentWinner winner = new TournamentWinner("", round, roundPlayers.get(0));
			messageDispatcher.dispatchTournamentWinner(winner);
			return;
		}
		matchups.clear();

		for (int i=0; i<roundPlayers.size(); i+=2) {
		   final String roundName = getRoundName(roundPlayers.size());
		   final String message = String.format("%s match up: %s vs %s at position: playerA = %s_%d , playerA = %s_%d", getRoundName(roundPlayers.size()),
			roundPlayers.get(i).getFormattedName(), roundPlayers.get(i+1).getFormattedName(), roundName, i, roundName, i+1);
		   messageDispatcher.dispatchMatchStats(message);
		   
		   final int roundPos = (i==0) ? 0 : i/2;
		   matchups.add(new MatchUp(messageDispatcher, roundPlayers.get(i), roundPlayers.get(i+1), 5, roundName, round, roundPos));
		}
		matchups.stream().forEach(m -> m.simulateMatchUp());
		matchups.stream().forEach(m -> messageDispatcher.dispatchMatchResult(m));
		List<Player> roundWinners = matchups.stream()
		        .map(p -> p.getWinner())
		        .collect(Collectors.toList()); 
		roundWinners.stream().forEach(w -> {
			   final String message = String.format("Winner %s", w.getFormattedName());
			   messageDispatcher.dispatchMatchStats(message);
		});

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		simulateRounds(roundWinners, round + 1);
	}
	
	private int seedPlayer(int rank, int partSize) {
	    // base case, if rank == 1, return position 0
	    if (rank <= 1) {
	        return 0;
	    }

	    // if our rank is even we need to put the player into the right part
	    // so we add half the part size to his position
	    // and make a recursive call with half the rank and half the part size
	    if (rank % 2 == 0) {
	        return partSize / 2 + seedPlayer(rank / 2, partSize / 2);
	    }

	    // if the rank is uneven, we put the player in the left part
	    // since rank is uneven we need to add + 1 so that it stays uneven
	    return seedPlayer(rank / 2 + 1, partSize / 2);
	}
	
	private String getRoundName(final int round) {
		switch (round) {
			case 128:
				return "1st Round";
			case 64:
				return "2nd Round";
			case 32:
				return "3rd Round";
			case 16:
				return "Round of 16";
			case 8:
				return "Quarter Finals";
			case 4:
				return "Semi Finals";
			case 2:
				return "Finals";
			case 1:
				return "Winner";
			default:
				return "Unknown Round";
		}
	}
}
