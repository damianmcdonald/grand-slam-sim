package com.github.damianmcdonald.grandslamsim.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class Player implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String surname;
	
	@Column(nullable = false)
	private String represents;
			
	@Column(nullable = false)
	private String image;
	
	@Transient
	private int seed;
	
	@Transient
	private int drawPosition;
	
	@Transient
	private String formattedName;
	
	public Player() { }
	
	public Player(final String firstName, final String surname, final String represents, final String image) {
		this.firstName = firstName;
		this.surname = surname;
		this.represents = represents;
		this.image = image;
	}
	
	public Long getId() { return id; }

	public String getFirstName() { return firstName; }

	public String getSurname() { return surname; }

	public String getRepresents() { return represents; }

	public int getSeed() { return seed;	}
	
	public void setSeed(int seed) {	this.seed = seed; }

	public int getDrawPosition() { return drawPosition;	}
	
	public void setDrawPosition(int drawPosition) { this.drawPosition = drawPosition; }

	public String getImage() { return image; }

	public String getFormattedName() {
		this.formattedName =  getFirstName().charAt(0)+". "+getSurname();
		return formattedName;
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
    		.append(id)
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
        		.append(id, other.id)
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
	
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}

}
