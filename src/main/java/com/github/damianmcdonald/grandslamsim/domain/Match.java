package com.github.damianmcdonald.grandslamsim.domain;

import java.util.Random;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Match {
	
	private Player playerA;
	private Player playerB;
	private int round;
	private int matchPosition;
	private final long id = new Random().nextLong();
	
	public Match (final Player playerA, final Player playerB, final int round, final int matchPosition) {
		this.playerA = playerA;
		this.playerB = playerB;
		this.round = round;
		this.matchPosition = matchPosition;
	}

	public Player getPlayerA() { return playerA; }

	public Player getPlayerB() { return playerB; }

	public int getRound() {	return round; }

	public int getMatchPosition() {	return matchPosition; }
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
    		.append(id)
	        .append(round)
			.append(matchPosition)
			.append(playerA)
			.append(playerB)
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof Match){
	        final Match other = (Match) obj;
	        return new EqualsBuilder()
        		.append(id, other.id)
	            .append(round, other.round)
				.append(matchPosition, other.matchPosition)
				.append(playerA, other.playerA)
				.append(playerB, other.playerB)
	            .isEquals();
	    } else{
	        return false;
	    }
	}
	
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}

}
