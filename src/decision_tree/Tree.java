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
	final static int TARGET = 4;
	
	//Quantidades de selecoes diferentes para cada tipo de atributo dentro dos contadores
	final static int GENDER_END = 2;
	final static int GENDER_INI = 1;
	final static int AGE_END = 7;
	final static int AGE_INI = 1;
	final static int OCCUPATION_END = 20;
	final static int OCCUPATION_INI = 0;
	final static int STARS_END = 5;
	final static int STARS_INI = 1;
	final static int TARGET_END = 5;
	final static int TARGET_INI = 1;
	
	
	private ArrayList<int[]>examples;
	ArrayList<int[]> counters;
	int[] iniIndex, endIndex, target;
	
	public Tree(ArrayList<int[]> examples){
		this.examples = examples;
		counters = accountExamples();
		iniIndex = new int[] {GENDER_INI, AGE_INI, OCCUPATION_INI, STARS_INI, TARGET_INI};
		endIndex = new int[] {GENDER_END, AGE_END, OCCUPATION_END, STARS_END, TARGET_END};
		//this.target = target;
	}
	
	public void build(){
		
		int best = chooseBestAttr();
	}
	
	private ArrayList<int[]> accountExamples(){
		//Cada um dos elementos do arraylist eh um vetor
		//Cada cada campo dos vetores guarda a quantidade de 
		//occorencias de um determinado tipo de atributo
		ArrayList<int[]> counters = new ArrayList<int[]>();
		for(int i = 0; i < N_ATTR; i++){
			counters.add(new int[COUNTER_SIZE]);
		}
		
		//Percorrendo os exemplos
		int[] example, counter;
		int aux;
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
			
			//Target
			//counter = counters.get(TARGET);
			//counter[example[TARGET]]++;
		}
		//counters.add(target);
		return counters;
	}
	
	private int chooseBestAttr(){
		int[] counter;
		double [] gain = new double[N_ATTR];
		double targetEntropy = entropy(STARS, counters.get(STARS));
		for(int attr = 0; attr < N_ATTR; attr++){
			if(attr != STARS){
				counter = counters.get(attr);
				gain[attr] = targetEntropy - entropyWithTarget(attr, counter);
			}
		}
		return 0;
	}
	
	private double entropy(int attr, int[] counter){
		double total = examples.size();
		double acc = 0;
		for(int i = iniIndex[attr]; i <= endIndex[attr]; i++){
			double aux = counter[i]/total;
			acc += -(aux) * Math.log(aux);
		}
		return acc;
	}
	
	private double entropyWithTarget(int attr, int[] counter){
		int target = STARS;
		double total;
		double acc = 0, aux;
		for(int attrField = iniIndex[attr]; attrField <= endIndex[attr]; attrField++){
			double accField = 0;
			total = counter[attrField];
			for(int nStars = iniIndex[target]; nStars <= endIndex[target]; nStars++){
				int exampleCounter = 0;
				for(int k = 0; k < examples.size(); k++){
					int[] example = examples.get(k);
					if(example[target] == nStars && example[attr] == attrField)
						exampleCounter++;
				}
				aux = (double) exampleCounter / (double) total;
				accField += -(aux) * Math.log(aux);
				if(attr == AGE)
					System.out.println("nStars: "+nStars+"   aux = "+aux+"   accField = "+accField+"   exampleCounter: "+exampleCounter+"   total: "+total);
				
			}
			
			acc += (double) counter[attrField]/(double) examples.size() * accField;
			//if(attr == OCCUPATION){
			//	System.out.println("counter[occ] = "+counter[attrField]+ "  example size: "+ examples.size()+ "   accField: "+accField);
			//}
		}
		//if(attr == OCCUPATION)
			//System.out.println("acc = "+acc);
		return acc;
		
	}
}
