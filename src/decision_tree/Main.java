package decision_tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	final static int N_ATTR = 5;
	
	//Atributos
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int GENRE = 3;
	final static int STARS = 4;
	final static int TARGET = 5;
	
	final static int ATTR_PRIORI = 2;
	final static int MOVIE_ID = 0;
	final static int RATING = 1;
	
	public static void main(String args[]){
		HashMap<Integer, Movie> movies = new HashMap<Integer, Movie>();
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		//Lista de ID's de filmes para ser iterada
		ArrayList<Integer> moviesID =  new ArrayList<Integer>();
		//Lista com os generos
		HashMap<String, Integer> genres = new HashMap<String, Integer>();
		
		int ratingNum = readData(movies, users, moviesID, genres);
		ArrayList<int[]>examples = generateExampleSet(movies,users, moviesID, genres);
		//int[] target = generateTargetSet(movies, moviesID);		
		
		//Classificacao para arvore de decisao
		Tree decTree = new Tree(examples,genres);
		decTree.build();
		System.out.println("noAttr: "+Node.noAttrCounter+"  noExam: "+Node.noExamCounter+"  sameClass: "+Node.sameClassCounter);
		int[][] conf = decTree.evaluate();
		System.out.println("Arvore de decisao com base de dados");
		decTree.printAnalytics();
		
		System.out.println("Arvore de decisao com base personalizada");
		conf = decTree.personalEvaluate();
		decTree.printAnalytics();
		
		//Classificacao para classificador a priori
		aPrioriClassifier apriori = new aPrioriClassifier();
		apriori.train(movies, moviesID);
		conf = apriori.evaluate();
		System.out.println("Classificador a priori com base de dados");
		apriori.printAnalytics();
		
		/*
		//Imprime Matriz de confusão:
		double[][] confDouble = new double[5][5];
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				confDouble[i][j] = (double)conf[i][j] / (double) apriori.getTestSetSize();
			}
		}
		for(int i = 0; i < 5; i++){
			System.out.printf(" %.2f %.2f %.2f %.2f %.2f\n", confDouble[i][0],confDouble[i][1],confDouble[i][2],confDouble[i][3],confDouble[i][4]);
		}
		*/
		conf = apriori.personalEvaluate();
		System.out.println("Classificador a priori com base personalizada");
		apriori.printAnalytics();
	}

	private static int readData(HashMap<Integer, Movie> movies, 
			HashMap<Integer, User> users,
			ArrayList<Integer> moviesID,
			HashMap<String, Integer> genres){
		String moviesFile = "C:\\Users\\Ana Cuder\\workspace\\CTC17-Projeto3\\movies.dat";
		String ratingsFile = "C:\\Users\\Ana Cuder\\workspace\\CTC17-Projeto3\\ratings.dat";
		String usersFile = "C:\\Users\\Ana Cuder\\workspace\\CTC17-Projeto3\\users.dat";
        BufferedReader br = null;
        String line = "";
        String datSplitBy = "::";
        String genreSplitBy = "\\|";
        String[] movieData, genreData,userData, ratingData;
        
        //Lendo filmes
        try {
            br = new BufferedReader(new FileReader(moviesFile));
            int genreCounter = 0;
            while ((line = br.readLine()) != null) {
                movieData = line.split(datSplitBy);
                //genreData = movieData[2].split(genreSplitBy); 
                Movie aux = new Movie(Integer.parseInt(movieData[0]), movieData[1]);
                //for(int i=0; i < genreData.length; i++)
                //	aux.addGenre(genreData[i]);
                aux.addGenre(movieData[2]);
                if(!genres.containsKey(movieData[2])){
                	genres.put(movieData[2], genreCounter);
                	genreCounter++;
                }
                movies.put(aux.getID(), aux);
                moviesID.add(Integer.parseInt(movieData[0]));
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        //Lendo Ratings
        int ratingNum = 0;
        try {
            br = new BufferedReader(new FileReader(ratingsFile));
            while ((line = br.readLine()) != null) {
                ratingData = line.split(datSplitBy); 
                Rating aux = new Rating(Integer.parseInt(ratingData[0]), Integer.parseInt(ratingData[1]), Integer.parseInt(ratingData[2]));
                movies.get(aux.getMovieID()).addRating(aux);
                ratingNum++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        //Lendo usuarios
        try {
            br = new BufferedReader(new FileReader(usersFile));
            while ((line = br.readLine()) != null) {
                userData = line.split(datSplitBy); 
                User aux = new User(Integer.parseInt(userData[0]), //user ID
                		userData[1], //Genero
                		Integer.parseInt(userData[2]), //Idade
                		Integer.parseInt(userData[3]), //Ocupacao
                		userData[4]);//CEP
                users.put(aux.getID(), aux);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ratingNum;
	}
	
	private static ArrayList<int[]> generateExampleSet(HashMap<Integer, Movie> movies, 
			HashMap<Integer, User> users,
			ArrayList<Integer> moviesID,
			HashMap<String, Integer> genres){
		
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
			for(int j = 0; j < allMovieRatings.size(); j++){
				auxRate = allMovieRatings.get(j);
				uid = auxRate.getUserID();
				userRate = auxRate.getRate();
				auxUser = users.get(uid);
				int[] example = new int[N_ATTR];
				example[GENDER] = auxUser.getGender()-1;
				example[OCCUPATION] = auxUser.getOccupation();
				//Para que o vetor torne-se continuo, divide-se por 8 as idades
				int aux = auxUser.getAge()/8;
				if(aux != 0)
					aux = aux - 1;
				example[AGE] = aux;
				example[GENRE] = genres.get(auxMovie.getGenre());
				example[STARS] = userRate-1;
				examples.add(example);
			}
		}
		return examples;
	}
	
	
	//Contabiliza o numero de ocorrencia de filmes de 1 estrela, 2, ... , 5 estrelas
	private static int[] generateTargetSet(HashMap<Integer, Movie> movies,
									ArrayList<Integer> moviesID){
		//6 posicoes pois a 0 nao sera usada
		int[] target = new int[STARS+1];
		int mid;
		for(int i = 0; i < moviesID.size(); i++){
			mid = moviesID.get(i);
			target[movies.get(mid).getStars()]++;
		}
		return target;
	}
}

