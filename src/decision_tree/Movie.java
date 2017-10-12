package decision_tree;

import java.util.ArrayList;

public class Movie {
	private ArrayList<String> genres;
	private ArrayList<Rating> ratings;
	private int movieID;
	private String title;
	
	public Movie(int id, String title){
		genres = new ArrayList<String>();
		ratings = new ArrayList<Rating>();
		movieID = id;
		this.title = title; 
	}
	
	public int getID(){
		return movieID;
	}
	
	public void addGenre(String genre){
		if(!genres.contains(genre))
			genres.add(genre);
	}
	
	public void addRating(Rating r){
		ratings.add(r);
	}
}
