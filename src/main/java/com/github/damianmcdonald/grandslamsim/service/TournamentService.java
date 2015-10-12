package com.github.damianmcdonald.grandslamsim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.damianmcdonald.grandslamsim.dispatchers.WebSocketMessageDispatcher;
import com.github.damianmcdonald.grandslamsim.domain.Player;
import com.github.damianmcdonald.grandslamsim.domain.Tournament;
import com.github.damianmcdonald.grandslamsim.simengine.TournamentEngine;

@Service
public class TournamentService {
	
	@Autowired private WebSocketMessageDispatcher messageDispatcher;
	@Autowired private PlayerRepository playerRepository;
	private static final Map<String, TournamentEngine> tournamentMap = new ConcurrentHashMap<String, TournamentEngine>();
	
	public Tournament createTournament(final String tournamentName, final boolean randomize) {
		final TournamentEngine engine = new TournamentEngine(tournamentName, messageDispatcher);
		final List<Player> players = new ArrayList<Player>(loadPlayers());
		if(randomize) {
			final long seed = System.nanoTime();
			Collections.shuffle(players, new Random(seed));
		}
		IntStream.range(0, players.size()).forEach(i -> players.get(i).setSeed(i+1));
		final Tournament tournament = engine.createTournament(tournamentName, getDrawPositions(players));
		tournamentMap.put(tournamentName,  engine);
		return tournament;
	}
	
	public Tournament createTournament(final String tournamentName, final List<Player> players) {
		final TournamentEngine engine = new TournamentEngine(tournamentName, messageDispatcher);
		final Tournament tournament = engine.createTournament(tournamentName, getDrawPositions(players));
		tournamentMap.put(tournamentName,  engine);
		return tournament;
	}
	
	public Tournament getTournament(final String tournamentName) {
		final TournamentEngine engine = tournamentMap.get(tournamentName);
		if(engine == null || engine.isStarted()) {
			return null;
		}
		return tournamentMap.get(tournamentName).getTournament();
	}
	
	public void removeTournament(final String tournamentName) {
		tournamentMap.entrySet().removeIf(e-> e.getKey().equalsIgnoreCase(tournamentName));
	}
	
	public boolean verifyUniqueTournamentName(final String tournamentName) {
		return !tournamentMap.containsKey(tournamentName);
	}
	
	public List<Tournament> listTournaments() {
		return tournamentMap.values()
				.stream()
				.filter(p -> !p.isStarted())
				.map(e -> e.getTournament())
				.collect(Collectors.toList());
	}
	
	public void simulateRounds(final String tournamentName, final List<Player> players) {
		final TournamentEngine engine = tournamentMap.get(tournamentName);
		engine.simulateRounds(players, 0);
		tournamentMap.entrySet().removeIf(e-> e.getKey().equalsIgnoreCase(tournamentName));
	}
		
	public List<Player> loadPlayers() {
		final List<Player> players = StreamSupport.stream(playerRepository.findAll().spliterator(), false)
									 .collect (Collectors.toList());
		players.sort((p1, p2) -> (int) p1.getSeed() - p2.getSeed());
		return players;
	}
	
	public List<Player> getDrawPositions(final List<Player> players) {
		final int DRAW_SIZE = players.size()+1;
		players.forEach(p -> p.setDrawPosition(seedPlayer(p.getSeed(), DRAW_SIZE)));
		players.sort((p1, p2) -> (int) p1.getDrawPosition() - p2.getDrawPosition());
		return players;
	}
	
	private int seedPlayer(final int rank, final int partSize) {
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

}
