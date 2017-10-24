package decision_tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class aPrioriClassifier {
	private HashMap<Integer, Movie> movies;
	final static int ATTR_PRIORI = 2;
	final static int MOVIE_ID = 0;
	final static int RATING = 1;
	
	private HashMap<Integer, Integer> trainSet;
	private ArrayList<int[]> testSet, personalSet;
	final static double TRAIN_PERC = 0.85;
	private int[][] conf;
	private double squareError;
	private double accuracy;
	private double kappa;
	
	
	public aPrioriClassifier(){
		trainSet = new HashMap<Integer, Integer>();
		testSet = new ArrayList<int[]>();
		personalSet = new ArrayList<int[]>();
		
		personalSet = new ArrayList<int[]>();
		//filmes a serem adicionados
		//Independence day
		personalSet.add(new int[]{780 ,3}); // 780
		// Ghost in the shell
		personalSet.add(new int[]{741,4});	//741
		//Oliver e companhia
		personalSet.add(new int[]{709,3}); // 709
		//Missao impossivel
		personalSet.add(new int[]{648,4}); //648
		//O silencio dos inocentes
		personalSet.add(new int[]{593,4});  //593
		//Exterminador do futuro 2
		personalSet.add(new int[]{589,4}); //589
		//Blade Runner
		personalSet.add(new int[]{541,4});  //541
		//Jurassic Park
		personalSet.add(new int[]{480,4});  //480
		//Free Willy
		personalSet.add(new int[]{455,2}); //455
		//Mortal Kombat
		personalSet.add(new int[]{44,1}); //44
	}
	
	public int getTestSetSize(){
		return testSet.size();
	}
	
	public void train(HashMap<Integer, Movie> movies, 
			ArrayList<Integer> moviesID){
		
		ArrayList<Rating> allMovieRatings;
		int mid; //ID do filme
		int uid; //ID do usuario
		Rating auxRate;
		int userRate;
		User auxUser;
		Movie auxMovie;
		
		ArrayList<int[]> examples = new ArrayList<int[]>();
		for(int i = 0; i < moviesID.size(); i++){
			mid = moviesID.get(i);
			auxMovie = movies.get(mid);
			allMovieRatings = auxMovie.getAllRatings();
			int acc = 0;
			int maxIndex = (int) Math.round((double)allMovieRatings.size() * TRAIN_PERC);
			Collections.shuffle(allMovieRatings);
			int[] counter = new int[5];
			for(int j = 0; j < maxIndex; j++){
				auxRate = allMovieRatings.get(j);
				//acc +=auxRate.getRate();
				counter[auxRate.getRate()-1]++;
			}
			if(maxIndex == 0)
				trainSet.put(mid, 10);
			else{
				//trainSet.put(mid, acc/maxIndex - 1);
				//Seleciona o valor maximo
				int max = 0, maxInd = 0;;
				for(int k = 0; k < counter.length; k++){
					if(counter[k] > max){
						max = counter[k];
						maxInd = k;
					}
				}
				trainSet.put(mid, maxInd);
			}
				
			//example[MOVIE_ID] = mid;
			//example[RATING] = acc / maxIndex;
			//trainSet.add(example);
			for(int j = maxIndex; j < allMovieRatings.size(); j++){
				auxRate = allMovieRatings.get(j);
				testSet.add(new int[]{mid, auxRate.getRate()-1});
			}
		}
		//Determinando valor padrao
		int[] counter = new int[6];
		for(int i = 0; i < moviesID.size(); i++){
			int movieid = moviesID.get(i);
			int val = trainSet.get(movieid);
			if(val != 10)
				counter[val]++;
		}
		
		//Seleciona o valor maximo
		int max = 0, maxIndex = 0;;
		for(int i = 0; i < counter.length; i++){
			if(counter[i] > max){
				max = counter[i];
				maxIndex = i;
			}
		}
		
		//Atribui padrao
		int standard = maxIndex-1;
		for(int i = 0; i < moviesID.size(); i++){
			int moid = moviesID.get(i);
			int val = trainSet.get(moid);
			if(val == 10){
				trainSet.remove(moid);
				trainSet.put(moid, standard);
			}
		}
	}
	
	public int[][] evaluate(){
		int[][] confusionMat = new int[5][5];
		double sqrtErrAcc = 0;
		int acc = 0;
		for(int i = 0; i < testSet.size(); i++){
			int[] example = testSet.get(i);
			int target = trainSet.get(example[MOVIE_ID]);
			confusionMat[target][example[RATING]]++;
			sqrtErrAcc += Math.pow(example[RATING] - target,2);
		}
		this.squareError = sqrtErrAcc / (double) testSet.size();
		this.conf = confusionMat;
		this.accuracy = 0;
		for(int i = 0; i < 5; i++){
			this.accuracy += conf[i][i];
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
		return conf;
	}
	
	public int[][] personalEvaluate(){
		testSet = personalSet;
		return evaluate();
	}
	
	public void printAnalytics(){
		System.out.println("Accuracy: "+accuracy);
		System.out.println("SqrtErr: "+squareError);
		System.out.println("kappa: "+kappa);
	}
	
}
