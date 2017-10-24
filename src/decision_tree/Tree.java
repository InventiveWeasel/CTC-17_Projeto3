package decision_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Tree {
	final static int N_ATTR = 5;
	final static int COUNTER_SIZE = 305;
	
	//Atributos
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int GENRE = 3;
	final static int STARS = 4;
	final static int TARGET = 5;
	
	//Quantidades de selecoes diferentes para cada tipo de atributo dentro dos contadores
	final static int GENDER_END = 1;
	final static int GENDER_INI = 0;
	final static int AGE_END = 6;
	final static int AGE_INI = 0;
	final static int OCCUPATION_END = 20;
	final static int OCCUPATION_INI = 0;
	final static int STARS_END = 4;
	final static int STARS_INI = 0;
	final static int TARGET_END = 5;
	final static int TARGET_INI = 1;
	
	
	private ArrayList<int[]>examples;
	private ArrayList<int[]> counters;
	private int[] iniIndex, endIndex, target;
	//Associa a string do genero ao seu id
	private HashMap<String, Integer> genres;
	//Marcam os atributos que ja foram utilizados na classificacao
	private boolean[] attrMark;
	private double[] bestAttr;
	private ArrayList<ArrayList<Integer>> attributes;
	private Node root;
	
	ArrayList<int[]> trainSet, testSet;
	final static double TRAIN_PERC = 0.85;
	
	private int[][] conf;
	private double squareError;
	private double accuracy;
	private double kappa;
	
	ArrayList<int[]> personalSet;
	
	public Tree(ArrayList<int[]> examples, HashMap<String, Integer> genres){
		this.examples = examples;
		counters = accountExamples();
		iniIndex = new int[] {GENDER_INI, AGE_INI, OCCUPATION_INI, 0 , STARS_INI, TARGET_INI};
		endIndex = new int[] {GENDER_END, AGE_END, OCCUPATION_END, genres.size()-1 ,STARS_END, TARGET_END};
		this.genres = genres;
		
		attributes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> aux;
		for(int attr = GENDER; attr <= GENRE; attr++){
			aux = new ArrayList<Integer>();
			for(int j = iniIndex[attr]; j <= endIndex[attr]; j++){
				aux.add(j);
			}
			attributes.add(aux);
		}
		
		attrMark = new boolean[N_ATTR-1];
		for(int i = 0; i < attrMark.length; i++){
			attrMark[i] = false;
		}
		bestAttr = generateBestAttrArray();
		
		//Separando os conjuntos de treino e teste
		Collections.shuffle(examples);
		trainSet = new ArrayList<int[]>();
		testSet = new ArrayList<int[]>();
		int nTrain = (int) Math.round(examples.size() * TRAIN_PERC);
		for(int i = 0; i < nTrain; i++){
			trainSet.add(examples.get(i));
		}
		for(int i = nTrain; i < examples.size(); i++){
			testSet.add(examples.get(i));
		}
		
		
		personalSet = new ArrayList<int[]>();
		//filmes a serem adicionados
		//Independence day
		personalSet.add(new int[]{0, 1, 17, genres.get("Action|Sci-Fi|War"),3}); // 780
		// Ghost in the shell
		personalSet.add(new int[]{0, 1, 17, genres.get("Animation|Sci-Fi"),4});	//741
		//Oliver e companhia
		personalSet.add(new int[]{0, 1, 17, genres.get("Animation|Children's"),3}); // 709
		//Missao impossivel
		personalSet.add(new int[]{0, 1, 17, genres.get("Action|Adventure|Mystery"),4}); //648
		//O silencio dos inocentes
		personalSet.add(new int[]{0, 1, 17, genres.get("Drama|Thriller"),4});  //593
		//Exterminador do futuro 2
		personalSet.add(new int[]{0, 1, 17, genres.get("Action|Sci-Fi|Thriller"),4}); //589
		//Blade Runner
		personalSet.add(new int[]{0, 1, 17, genres.get("Film-Noir|Sci-Fi"),4});  //541
		//Jurassic Park
		personalSet.add(new int[]{0, 1, 17, genres.get("Action|Adventure|Sci-Fi"),4});  //480
		//Free Willy
		personalSet.add(new int[]{0, 1, 17, genres.get("Adventure|Children's|Drama"),2}); //455
		//Mortal Kombat
		personalSet.add(new int[]{0, 1, 17, genres.get("Action|Adventure"),1});
		
	}
	
	public void build(){
		int best = chooseBestAttr();
		int standard =  getMaxTargetValue();
		boolean[] mark = new boolean[N_ATTR-1];
		double[] bestAtt = new double[N_ATTR-1];
		System.arraycopy( attrMark, 0, mark, 0, attrMark.length );
		System.arraycopy( bestAttr, 0, bestAtt, 0, bestAttr.length );
		root = new Node(mark, bestAtt);
		root.setAttr(best);
		root.build(trainSet, attributes, standard);
		int a = 1;
	}
	
	public int[][] evaluate(){
		int[][] confusionMat = new int[5][5];
		double sqrtErrAcc = 0;
		int acc = 0;
		int retVal;
		for(int i = 0; i < testSet.size(); i++){
			int[] example = testSet.get(i);
			retVal = root.evaluate(example);
			confusionMat[example[STARS]][retVal]++;
			sqrtErrAcc += Math.pow(example[STARS] - retVal,2);
		}
		
		//Aproveitar para calcular as metricas pedidas
		this.squareError = sqrtErrAcc / (double) testSet.size();
		this.conf = conf;
		this.accuracy = 0;
		for(int i = 0; i < 5; i++){
			this.accuracy += confusionMat[i][i];
		}
		this.accuracy /= (double) testSet.size();
		
		//Calculo de kappa
		double[] distVec = new double[5];
		double[] totalVec = new double[5];
		double total = 0;
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				distVec[i] += confusionMat[i][j];
			}
			total+=distVec[i];
			totalVec[i] = distVec[i];
		}
		//obtem-se a distribuicao esperada
		for(int i = 0; i < 5; i++){
			distVec[i] /= total;
		}
		//montando a diagonal principal esperado
		double[] expecDiag = new double[5];
		for(int i = 0; i < 5; i++){
			expecDiag[i] = distVec[i] * totalVec[i];
		}
		double po = 0, pe = 0;
		for(int i = 0; i < 5; i++){
			po += confusionMat[i][i];
			pe += expecDiag[i];
		}
		po /= total;
		pe /= total;
		this.kappa = (po-pe)/(1-pe);
		return confusionMat;
	}
	
	public int[][] personalEvaluate(){
		testSet = personalSet;
		return evaluate();
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
			
			//Genero
			counter = counters.get(GENRE);
			counter[example[GENRE]]++;
			
			//Avaliacao
			counter = counters.get(STARS);
			counter[example[STARS]]++;
		}
		return counters;
	}
	
	private double[] generateBestAttrArray(){
		int[] counter;
		//Desconsiderar STARS, pois eh o alvo
		double [] gain = new double[N_ATTR-1];
		double targetEntropy = entropy(STARS, counters.get(STARS));
		for(int attr = 0; attr < N_ATTR; attr++){
			if(attr != STARS){
				counter = counters.get(attr);
				gain[attr] = targetEntropy - entropyWithTarget(attr, counter);
			}
		}
		return gain;
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
			for(int nStars = 0; nStars <= 4; nStars++){
				int exampleCounter = 0;
				for(int k = 0; k < examples.size(); k++){
					int[] example = examples.get(k);
					if(example[target] == nStars && example[attr] == attrField)
						exampleCounter++;
				}
				aux = (double) exampleCounter / (double) total;
				if(aux == 0)
					accField += 0;
				else
					accField += -(aux) * Math.log(aux);
			}
			
			acc += (double) counter[attrField]/(double) examples.size() * accField;
		}
		return acc;
		
	}
	
	private int chooseBestAttr(){
		double bestGain = 0;
		int bestA = 0;
		for(int attr = GENDER; attr <= GENRE; attr++){
			if(bestAttr[attr] > bestGain && !attrMark[attr]){
				bestA = attr;
				bestGain = bestAttr[attr];
			}
				
		}
		attrMark[bestA] = true;
		return bestA;
	}
	
	private int getMaxTargetValue(){
		int[] counter = counters.get(STARS);
		int max = 0;
		int maxIndex = -1;
		for(int i = 0; i < 5; i++){
			if(counter[i] > max){
				max = counter[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public void printAnalytics(){
		System.out.println("Accuracy: "+accuracy);
		System.out.println("SqrtErr: "+squareError);
		System.out.println("kappa: "+kappa);
	}
}
