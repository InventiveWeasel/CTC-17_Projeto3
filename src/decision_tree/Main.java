package decision_tree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static void main(String args[]){
		HashMap<Integer, Movie> movies = new HashMap<Integer, Movie>();
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		readData(movies, users);
		int a = 2;
	}

	private static void readData(HashMap<Integer, Movie> movies, 
			HashMap<Integer, User> users){
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
        try {
            br = new BufferedReader(new FileReader(ratingsFile));
            while ((line = br.readLine()) != null) {
                ratingData = line.split(datSplitBy); 
                Rating aux = new Rating(Integer.parseInt(ratingData[0]), Integer.parseInt(ratingData[1]), Integer.parseInt(ratingData[2]));
                movies.get(aux.getMovieID()).addRating(aux);
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
                User aux = new User(Integer.parseInt(userData[0]), userData[1],userData[2],userData[3], userData[4]);
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
        
	}
}

