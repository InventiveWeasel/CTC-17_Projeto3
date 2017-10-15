package decision_tree;

import java.util.ArrayList;

public class Movie {
	//private ArrayList<String> genres;
	private String genre;
	private ArrayList<Rating> ratings;
	private int movieID;
	private String title;
	private double stars;
	
	public Movie(int id, String title){
		//genres = new ArrayList<String>();
		ratings = new ArrayList<Rating>();
		movieID = id;
		this.title = title; 
		stars = 0;
	}
	
	public int getID(){
		return movieID;
	}
	
	public void addGenre(String genre){
		//if(!genres.contains(genre))
			//genres.add(genre);
		this.genre = genre;
	}
	
	public void addRating(Rating r){
		ratings.add(r);
		stars = (stars*(ratings.size()-1) + r.getRate()) / ratings.size();
	}
	
	public int getStars(){
		return (int) Math.round(stars);
	}
	
	public String getGenre(){
		return genre;
	}
	
	public ArrayList<Rating> getAllRatings(){
		return ratings;
	}
}
