package decision_tree;

public class User {
	final int MALE = 1;
	final int FEMALE = 2;
	
	
	private int userID, age, occupation;
	private String gender, zipcode;
	
	public User(int id, String g, int a, int o, String z){
		userID = id;
		gender = g;
		age = a;
		occupation = o;
		zipcode = z;
	}
	
	public int getID(){
		return userID;
	}
	
	public int getGender(){
		return gender.equals("M") ? MALE : FEMALE; 
	}
	
	public int getOccupation(){
		return occupation;
	}
	
	public int getAge(){
		return age;
	}
}
