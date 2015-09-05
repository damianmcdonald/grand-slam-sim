package com.github.damianmcdonald.grandslamsim.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Player {

	private String firstName;
	private String surname;
	private String represents;
	private int seed;
	private int drawPosition;
	private String image;
	private String formattedName;
	
	public Player(final String firstName, final String surname, final String represents, final int seed, final int drawPosition, final String image) {
		this.firstName = firstName;
		this.surname = surname;
		this.represents = represents;
		this.seed = seed;
		this.drawPosition = drawPosition;
		this.image = image;
	}

	public String getFirstName() { return firstName; }

	public String getSurname() { return surname; }

	public String getRepresents() { return represents; }

	public int getSeed() {
		return seed;
	}

	public int getDrawPosition() {
		return drawPosition;
	}

	public String getImage() {
		return image;
	}

	public String getFormattedName() {
		this.formattedName =  getFirstName().charAt(0)+". "+getSurname();
		return formattedName;
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
	        .append(firstName)
			.append(surname)
			.append(represents)
	        .append(seed)
	        .append(drawPosition)
	        .append(image)
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof Player){
	        final Player other = (Player) obj;
	        return new EqualsBuilder()
	            .append(firstName, other.firstName)
				.append(surname, other.surname)
				.append(represents, other.represents)
	            .append(seed, other.seed)
	            .append(drawPosition, other.drawPosition)
	            .append(image, other.image)
	            .isEquals();
	    } else{
	        return false;
	    }
	}

}
