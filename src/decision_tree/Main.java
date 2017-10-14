package decision_tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	final static int N_ATTR = 4;
	
	//Atributos
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int STARS = 3;
	final static int TARGET = 4;
	
	public static void main(String args[]){
		HashMap<Integer, Movie> movies = new HashMap<Integer, Movie>();
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		//Lista de ID's de filmes para ser iterada
		ArrayList<Integer> moviesID =  new ArrayList<Integer>();
		
		int ratingNum = readData(movies, users, moviesID);
		ArrayList<int[]>examples = generateExampleSet(movies,users, moviesID);
		//int[] target = generateTargetSet(movies, moviesID);		
		Tree decTree = new Tree(examples);
		decTree.build();
		int a = 2;
	}

	private static int readData(HashMap<Integer, Movie> movies, 
			HashMap<Integer, User> users,
			ArrayList<Integer> moviesID){
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
            while ((line = br.readLine()) != null) {
                movieData = line.split(datSplitBy);
                genreData = movieData[2].split(genreSplitBy); 
                Movie aux = new Movie(Integer.parseInt(movieData[0]), movieData[1]);
                for(int i=0; i < genreData.length; i++)
                	aux.addGenre(genreData[i]);
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
			ArrayList<Integer> moviesID){
		
		ArrayList<Rating> allMovieRatings;
		int mid; //ID do filme
		int uid; //ID do usuario
		Rating auxRate;
		int userRate;
		User auxUser;
		
		ArrayList<int[]> examples = new ArrayList<int[]>();
		for(int i = 0; i < moviesID.size(); i++){
			mid = moviesID.get(i);
			allMovieRatings = movies.get(mid).getAllRatings();
			for(int j = 0; j < allMovieRatings.size(); j++){
				auxRate = allMovieRatings.get(j);
				uid = auxRate.getUserID();
				userRate = auxRate.getRate();
				auxUser = users.get(uid);
				int[] example = new int[N_ATTR];
				example[GENDER] = auxUser.getGender();
				example[OCCUPATION] = auxUser.getOccupation();
				//Para que o vetor torne-se continuo, divide-se por 8 as idades
				int aux = auxUser.getAge()/8;
				if(aux == 0)
					aux = 1;
				example[AGE] = aux;
				example[STARS] = userRate;
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

