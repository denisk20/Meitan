package com.meitan.lubov.services.test;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class NumberBean implements Serializable {

	protected final static Random rand = new Random();

	protected int min;
	protected int max;
	protected int guess;
	protected int actual;

    protected String testString;
	// Default Constructor

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public NumberBean() {
		this.min = 1;
		this.max = 10;
	}

	// called by JSF to validate user input
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		// coerce the value to an int
		try {
			int param = Integer.parseInt(value.toString());

			// validate param
			if (param > this.max || param < this.min) {
				FacesMessage msg = new FacesMessage("Guess must be between " + this.min + " and " + this.max);
				throw new ValidatorException(msg);
			}
		} catch (NumberFormatException e) {
			FacesMessage msg = new FacesMessage("Must be a number");
			throw new ValidatorException(msg);
		}
	}

	// lazy generate our actual value
	public synchronized int getActual() {
		if (this.actual == 0) {
			this.actual = rand.nextInt(this.max - this.min);
			this.actual += this.min;
		}
		return this.actual;
	}

	// our message for the response
	public String getMessage() {
		if (this.guess == this.getActual()) {
			return "Sweet, you got it right!";
		} else if (this.guess < this.getActual()) {
			return "Sorry, try something higher";
		} else {
			return "Too bad, go lower";
		}
	}

	// other bean properties
	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}

	public int getGuess() {
		return this.guess;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setGuess(int guess) {
		this.guess = guess;
	}

	public boolean getTrueValue() {
		return true;
	}

	public boolean getFalseValue() {
		return false;
	}

	public ArrayList getArrayList() {
		ArrayList result = new ArrayList();
		result.add(new Item("one", true));
		result.add(new Item("two", false));
		result.add(new Item("three", true));

		return result;
	}

}