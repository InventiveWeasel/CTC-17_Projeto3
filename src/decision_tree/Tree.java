package decision_tree;

import java.util.ArrayList;

public class Tree {
	final static int N_ATTR = 4;
	final static int COUNTER_SIZE = 30;
	
	//Atributos
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int STARS = 3;
	
	
	private ArrayList<int[]>examples;
	ArrayList<int[]> counters;
	
	public Tree(ArrayList<int[]> examples){
		this.examples = examples;
		counters = accountExamples();
		
	}
	
	public void build(){
		
		int best = chooseBestAttr();
	}
	
	private ArrayList<int[]> accountExamples(){
		//Cada um dos elementos do arraylist eh um vetor
		//Cada cada campo dos vetores guarda a quantidade de 
		//occorencias de um determinado tipo de atributo
		
		for(int i = 0; i < N_ATTR; i++){
			counters.add(new int[COUNTER_SIZE]);
		}
		
		//Percorrendo os exemplos
		int[] example, counter;
		for(int i = 0; i < examples.size(); i++){
			example = examples.get(i);
			//Contabilizando genero
			counter = counters.get(GENDER);
			counter[example[GENDER]]++;
			
			//Faixa etaria
			counter = counters.get(AGE);
			counter[example[AGE]]++;
			
			//Emprego
			counter = counters.get(OCCUPATION);
			counter[example[OCCUPATION]]++;
			
			//Avaliacao
			counter = counters.get(STARS);
			counter[example[STARS]]++;
		}
		return counters;
	}
	
	private int chooseBestAttr(){
		
	}
}
