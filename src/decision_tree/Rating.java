package decision_tree;

public class Rating {
	private int userID, movieID, rating;
	
	public Rating(int uid, int mid, int r){
		userID = uid;
		movieID = mid;
		rating = r;
	}
	
	public int getMovieID(){
		return movieID;
	}
	
	public int getUserID(){
		return userID;
	}
}
