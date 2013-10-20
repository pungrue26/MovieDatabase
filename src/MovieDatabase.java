import java.io.*;

/**
 * My implementation of MovieDatabase class can be illustrated like below.
 * ('G' represents a genre node, and 'M' represents a movie node.)
 * ---------------------------------------------------------------
 * sInitialGenreNode
 *   |
 * G(Action) - M(BATMAN BEGINS) - M(THE MATRIX) - null
 *   |
 * G(Drama) - M(MILLION DOLLAR BABY) - null
 *   |
 *  null 
 */
public class MovieDatabase {

	private static Genre sInitialGenreNode;
	
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			}
			catch (Exception e) {
				System.out.println("Exception occured : " + e.toString());
			}
		}
	}

	private static void command(String input) {
		String [] s = input.split("%");
		String inputCommand = s[0];
		if(inputCommand.contains("PRINT")) {
			handlePrint();
		} else if(inputCommand.contains("SEARCH")) {
			String key = s[1];
			handleSearch(key);
		} else if(inputCommand.contains("INSERT")) {
			String inputGenreName = s[1];
			// s[2] is just white spaces. Cause "input" looks like "INSERT %ACTION% %BATMAN BEGINS%"
			// so the title is stored in s[3].
			String inputMovieTitle = s[3];
			handleInsert(inputGenreName, inputMovieTitle);
		} else if(inputCommand.contains("DELETE")) {
			String inputGenreName = s[1];
			String inputMovieTitle = s[3];
			handleDelete(inputGenreName, inputMovieTitle);
		} else {
			System.out.println("Input isn't valid. :)");
		}
	}

	private static void handlePrint()	 {
		Genre currentGenre = sInitialGenreNode;
		if(currentGenre == null) {
			System.out.println("EMPTY");
			return;
		}
		while(currentGenre != null) {
			MovieNode currentMovie = currentGenre.linkToFirstMovieNode;
			while(currentMovie != null) {
				System.out.println("(" + currentGenre.genreName + ", " + currentMovie.title + ")");
				currentMovie = currentMovie.nextNode;
			}
			currentGenre = currentGenre.nextGenreNode;
		}
	}
	
	private static void handleSearch(String key) {
		Genre currentGenre = sInitialGenreNode;
		if(currentGenre == null) {
			System.out.println("EMPTY");
			return;
		}
		
		boolean isFound = false;
		while(currentGenre != null) {
			MovieNode currentMovie = currentGenre.linkToFirstMovieNode;
			while(currentMovie != null) {
				if(currentMovie.title.contains(key)) {
					isFound = true;
					System.out.println("(" + currentGenre.genreName + ", " + currentMovie.title + ")");
				}
				currentMovie = currentMovie.nextNode;
			}
			currentGenre = currentGenre.nextGenreNode;
		}
		if(!isFound) {
			System.out.println("EMPTY");
		}		
	}

	private static void handleInsert(String inputGenreName, String inputMovieTitle) {
		Genre currentGenre = sInitialGenreNode;
		if(currentGenre == null) {
			// There is NO Genre Node. Let's create the first one! :)
			sInitialGenreNode = new Genre(inputGenreName, inputMovieTitle);
			return;
		}
		
		boolean isAlreadyExistGenre = false;
		while(currentGenre != null) {
			if(currentGenre.genreName.equals(inputGenreName)) {
				isAlreadyExistGenre = true;
				break;
			}
			currentGenre = currentGenre.nextGenreNode;
		}
		
		if(isAlreadyExistGenre) {
			if(currentGenre.hasTitle(inputMovieTitle)) {
				// There is a movie that has exactly same title in this Genre.
				// In this case, insertion can't be performed.
				System.out.println("There is a movie ALREADY in DB, whose genre is \"" + inputGenreName + "\", and title is \"" + inputMovieTitle + "\".");
				return;
			} else {
				currentGenre.insert(new MovieNode(inputGenreName, inputMovieTitle));					
			}
		} else {
			currentGenre = sInitialGenreNode;
			if(currentGenre.nextGenreNode == null) {
				// There is ONLY ONE genre node. 
				if(currentGenre.genreName.compareTo(inputGenreName) < 0) {
					currentGenre.nextGenreNode = new Genre(inputGenreName, inputMovieTitle);
				} else {
					sInitialGenreNode = new Genre(inputGenreName, inputMovieTitle);
					sInitialGenreNode.nextGenreNode = currentGenre;
				}
			} else {
				// find right place to insert new Genre node.
				// Target position is currentGenre - HERE - currentGenre.nextGenreNode
				while(currentGenre.nextGenreNode.genreName.compareTo(inputGenreName) < 0) {
					if(currentGenre.nextGenreNode == null)
						break;
					else
						currentGenre = currentGenre.nextGenreNode;
				}
				Genre newGenreNode = new Genre(inputGenreName, inputMovieTitle);
				newGenreNode.nextGenreNode = currentGenre.nextGenreNode;
				currentGenre.nextGenreNode = newGenreNode;
			}
		}
	}
	
	private static void handleDelete(String inputGenreName, String inputMovieTitle) {
		Genre currentGenre = sInitialGenreNode;
		if(currentGenre == null) {
			System.out.println("There is NO item in DB.");
			return;
		}
		
		// handle case in which there is ONLY ONE genre node in DB.
		if(currentGenre.nextGenreNode == null) {
			if(currentGenre.genreName.equals(inputGenreName)) {
				currentGenre.delete(inputMovieTitle);
				// check if there is no movie left after deletion.
				if(currentGenre.linkToFirstMovieNode == null) {
					// No Genre & Movie Node left in DB after deletion.
					// reset sInitialGenreNode to null! :)
					sInitialGenreNode = null;
				}
				return;
			} else {
				System.out.println("There is No \"" + inputGenreName +"\" Genre in DB.");
				return;
			}
		}
		
		// handle case in which there are more than 2 genre nodes in DB.
		// First find the correct Genre Node in DB.
		if(currentGenre.genreName.equals(inputGenreName)) {
			currentGenre.delete(inputMovieTitle);
			// check if there is no movie left in this genre after deletion.
			if(currentGenre.linkToFirstMovieNode == null) {
				sInitialGenreNode = currentGenre.nextGenreNode;
			}
			return;
		}
		boolean isFound = false;
		while(currentGenre.nextGenreNode != null) {
			if(currentGenre.nextGenreNode.genreName.equals(inputGenreName)) {
				// Found the target movie node in currentGenre.nextGenreNode :-)
				isFound = true;
				break;
			}
			currentGenre = currentGenre.nextGenreNode;
		}
		
		if(isFound) {
			currentGenre.nextGenreNode.delete(inputMovieTitle);
			// check if there is no movie left in this genre after deletion.
			if(currentGenre.nextGenreNode.linkToFirstMovieNode == null) {
				currentGenre.nextGenreNode = currentGenre.nextGenreNode.nextGenreNode;
			}			
		} else {
			System.out.println("There is No \"" + inputGenreName +"\" Genre in DB.");
		}
	}
}

class MovieNode {
	String genre;
	String title;
	MovieNode nextNode;
	
	public MovieNode(String genre, String title) {
		this.genre = genre;
		this.title = title;
		this.nextNode = null;
	}
}

class Genre {
	String genreName;
	MovieNode linkToFirstMovieNode;
	Genre nextGenreNode;
	
	public Genre(String genreName, String movieTitle) {
		this.genreName = genreName;
		this.linkToFirstMovieNode = new MovieNode(genreName, movieTitle);
		this.nextGenreNode = null;
	}
	
	public boolean hasTitle(String key) {
		MovieNode currentNode = linkToFirstMovieNode;
		if(currentNode == null)
			return false;
		
		while(!currentNode.title.equals(key)) {
			if(currentNode.nextNode == null) {
				return false;
			}
			currentNode = currentNode.nextNode;
		}
		return true;
	}

	public boolean isEmpty() {
		return (linkToFirstMovieNode == null);
	}
	
	public void insert(MovieNode newNode) {
		MovieNode currentNode = linkToFirstMovieNode;
		if(currentNode == null) {
			linkToFirstMovieNode = newNode;
			return;
		}
		// check if the first postion is rigth place to be inserted.
		if(currentNode.title.compareTo(newNode.title) > 0) {
			newNode.nextNode = currentNode;
			linkToFirstMovieNode = newNode;
			return;
		}
		// find right position to insert.
		// Target position is currentNode - HERE - correntNode.nextNode
		while(currentNode.nextNode != null &&
				currentNode.nextNode.title.compareTo(newNode.title) < 0) {
			currentNode = currentNode.nextNode;
		}
		// then insert.
		newNode.nextNode = currentNode.nextNode;
		currentNode.nextNode = newNode;
	}
	
	public void delete(String key) {
		MovieNode currentNode = linkToFirstMovieNode;
		if(currentNode == null) {
			// There is No such Movie in this Genre.
			System.out.println("There is NO movie whose title is \"" + key + "\" in DB. :)");
			return;
		}
		
		// handle case in which movie node count is 1 
		if(currentNode.nextNode == null) {
			if(currentNode.title.equals(key)) {
				// the first node is the target node.
				linkToFirstMovieNode = null;
				return;
			} else {
				System.out.println("There is NO movie whose title is \"" + key + "\" in DB. :)");
				return;
			}
		}
		
		// handle case in which movie node count is equal or greater than 2 
		if(currentNode.title.equals(key)) {
			linkToFirstMovieNode = currentNode.nextNode;
			return;
		}
		
		while(!currentNode.nextNode.title.equals(key)) {
			if(currentNode.nextNode.nextNode == null) {
				System.out.println("There is NO movie whose title is \"" + key + "\" in DB. :)");
				return;
			}
			currentNode = currentNode.nextNode;
		}
		// Target Movie to be deleted is currentNode.nextNode! 
		currentNode.nextNode = currentNode.nextNode.nextNode;
	}
}