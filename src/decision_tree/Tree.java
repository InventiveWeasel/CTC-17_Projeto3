package decision_tree;

import java.util.ArrayList;

public class Tree {
	final static int N_ATTR = 4;
	
	//Atributos
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int STARS = 3;
	
	
	private ArrayList<int[]>examples;
	
	public Tree(ArrayList<int[]> examples){
		this.examples = examples;
	}
	
	public void build(){
		int best = chooseBestAttr();
	}
	
	public int chooseBestAttr(){
		
	}
}
