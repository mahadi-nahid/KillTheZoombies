package com.sust.game;

public class Data {
	private String	name;
	private int		score;

	public Data() {
		this("", 0);
	}

	public Data(String name, int score) {
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
