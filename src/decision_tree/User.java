package decision_tree;

public class User {
	private int userID;
	private String gender, age, occupation, zipcode;
	
	public User(int id, String g, String a,String o, String z){
		userID = id;
		gender = g;
		age = a;
		occupation = o;
		zipcode = z;
	}
	
	public int getID(){
		return userID;
	}
}
