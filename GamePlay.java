import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

//Note the presence of implicit recursion, minimax method calls minSearch, minSearch calls maxSearch, and maxSearch calls minSearch
public class GamePlay {
	
	public static Board gameBoard;
	
	public static void init() {
		gameBoard = new Board();
	}
	
	public static void main(String[] args) {
		init();

		// default port and delay
        int port = 8083;
		
		// parse command line arguments to override defaults
        if (args.length > 0)
        {
            try
            {
                port = Integer.parseInt(args[0]);
            }
			catch (NumberFormatException ex)
            {
                System.err.println("USAGE: java CheckersService [port]");
                System.exit(1);
            }
		}
		
		// set up an HTTP server to listen on the selected port
		try
		{
			InetSocketAddress addr = new InetSocketAddress(port);
			HttpServer server = HttpServer.create(addr, 1);
       
			server.createContext("/move.html", new MoveHandler());
        
			server.start();
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
			System.err.println("Could not start server");
		}
	}

	//This is it. The Heart. The Minimax algorithm.
	public static List<Integer> minimax(int depth) {
		//best heuristic, we need to maximize this
		int bestHeur = -99999;
		List<List<Integer>> holder = new ArrayList<List<Integer>>(1);  //holding all possible moves as we go by
		List<Integer> toRet = new ArrayList<Integer>(2);  //best move
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				//go through each piece on the board
				//if white, that is, computer's turn, we need to 'max' because we are the computer
				if((gameBoard.cboard[i][j] == 1) || (gameBoard.cboard[i][j] == 3)) {
					//the monster getMoves function from the Board class being used here
					holder = gameBoard.getMoves(i, j);
					//For each move possible, ie, moving through the holder arraylist
					for(int k = 0; k < holder.size(); k++) {
						int listSize = holder.get(k).size();
						int destj = holder.get(k).get(listSize-1); //destj and desti are the final locations of piece (i, j) here
						int desti = holder.get(k).get(listSize-2); //we do this because the previous elements in the sub-list of the
						//(contd)arraylist holder (because it is a list of lists) might be the pieces taken (killed) by (i, j) in the turn
						//Do move
						boolean kinged = false;
						gameBoard.cboard[desti][destj] = gameBoard.cboard[i][j];
						if((gameBoard.cboard[i][j] == 1) && (desti == 7)) {
							gameBoard.cboard[desti][destj] = 3; //Kinged if we reach the opposite side of the board!
							kinged = true;
						}
						gameBoard.cboard[i][j] = 0; //becaause we moved
						
						//removeds here holds the rest of 'holder' after the final destination i, j have been seen
						List<Integer> removeds = new ArrayList<Integer>(1);
						//if removeds' size is still 1 it means nothing has been removed!
						for(int l = 0; l < listSize-2; l += 2) {
							int remi = holder.get(k).get(l);
							int lnext = l + 1;
							int remj = holder.get(k).get(lnext);
							int piece = gameBoard.cboard[remi][remj];
							gameBoard.cboard[remi][remj] = 0;
							removeds.add(remi);
							removeds.add(remj);
							removeds.add(piece);
						}
						//Move done
						
						//minSearch is called after minimax because now we assume that at next stage of the game tree, opponent will
						//(contd) minimize our (computer's) heuristic
						int value = minSearch(depth-1);
						if(value > bestHeur) {
							toRet = holder.get(k);
							toRet.add(i);
							toRet.add(j);
							bestHeur = value;
						}
						
						//Undo move
						gameBoard.cboard[i][j] = gameBoard.cboard[desti][destj];
						if(kinged) gameBoard.cboard[i][j] = 1;  //Unkinged if had been kinged in previous move
						gameBoard.cboard[desti][destj] = 0;
						if(removeds.size() > 1) {
							for(int m = 0; m < removeds.size(); m += 3) {
								int remi = removeds.get(m);
								int mn1 = m + 1;
								int mn2 = m + 2;
								int remj = removeds.get(mn1);
								gameBoard.cboard[remi][remj] = removeds.get(mn2);
							}
						}
						//Move undone
					}
				}
				//If can do any take, do it. Remove those pieces from board
				if(toRet.size() > 4) {
					int retSize = toRet.size();
					int fromi = toRet.get(retSize-2);
					int fromj = toRet.get(retSize-1);
					int toi = toRet.get(retSize-4);
					int toj = toRet.get(retSize-3);
					gameBoard.cboard[toi][toj] = gameBoard.cboard[fromi][fromj];
					if((gameBoard.cboard[toi][toj] == 1) && (toi == 7)) gameBoard.cboard[toi][toj] = 3;
					if((gameBoard.cboard[toi][toj] == 2) && (toi == 0)) gameBoard.cboard[toi][toj] = 4;
					gameBoard.cboard[fromi][fromj] = 0;
					for(int k = 0; k < retSize-4; k += 2) {
						int remoi = toRet.get(k);
						int remoj = toRet.get(k+1);
						gameBoard.cboard[remoi][remoj] = 0;
					}
					return toRet;
				}
			}
		}
		int retSize = toRet.size();
		int fromi = toRet.get(retSize-2);
		int fromj = toRet.get(retSize-1);
		int toi = 0;
		int toj = 0;
		if(retSize > 2) {
			toi = toRet.get(retSize-4);
			toj = toRet.get(retSize-3);
		}
		gameBoard.cboard[toi][toj] = gameBoard.cboard[fromi][fromj];
		if((gameBoard.cboard[toi][toj] == 1) && (toi == 7)) gameBoard.cboard[toi][toj] = 3;
		if((gameBoard.cboard[toi][toj] == 2) && (toi == 0)) gameBoard.cboard[toi][toj] = 4;
		gameBoard.cboard[fromi][fromj] = 0;
		for(int i = 0; i < retSize-4; i += 2) {
			int remoi = toRet.get(i);
			int remoj = toRet.get(i+1);
			gameBoard.cboard[remoi][remoj] = 0;
		}
		return toRet;
	}
	
	//minSearch is pretty much the same as minimax, but looking from the opponent's view
	public static int minSearch(int depth) {
		
		if(depth == 0) return gameBoard.score();
		//hence bestHeur is +infinity, the opponent will minimize this
		int bestHeur = 99999;
		List<List<Integer>> holder = new ArrayList<List<Integer>>(1);
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				
				//if user's turn, that is, black
				if((gameBoard.cboard[i][j] == 2) || (gameBoard.cboard[i][j] == 4)) {
					holder = gameBoard.getMoves(i, j);
					//For each move
					for(int k = 0; k < holder.size(); k++) {
						int listSize = holder.get(k).size();
						int destj = holder.get(k).get(listSize-1);
						int desti = holder.get(k).get(listSize-2);
		
						//Do move
						boolean kinged = false;
						gameBoard.cboard[desti][destj] = gameBoard.cboard[i][j];
						if((gameBoard.cboard[i][j] == 2) && (desti == 0)) {
							gameBoard.cboard[desti][destj] = 4; //Kinged
							kinged = true;
						}
						gameBoard.cboard[i][j] = 0;
						List<Integer> removeds = new ArrayList<Integer>(1);
						//if removeds' size is still 1 it means nothing has been removed!
						for(int l = 0; l < listSize-2; l += 2) {
							int remi = holder.get(k).get(l);
							int lnext = l + 1;
							int remj = holder.get(k).get(lnext);
							int piece = gameBoard.cboard[remi][remj];
							gameBoard.cboard[remi][remj] = 0;
							removeds.add(remi);
							removeds.add(remj);
							removeds.add(piece);
						}
						//Move done
						
						//call maxSearch here, to get back to the computer's viewpoint.
						//note: minimax calls minSearch and minSearch calls maxSearch, and maxSearch calls minSearch.
						//implicit recursion here
						int value = maxSearch(depth-1);
						if(value < bestHeur) {
							bestHeur = value;
						}
						
						//Undo move
						gameBoard.cboard[i][j] = gameBoard.cboard[desti][destj];
						if(kinged) gameBoard.cboard[i][j] = 2;
						gameBoard.cboard[desti][destj] = 0;
						if(removeds.size() > 1) {
							for(int m = 0; m < removeds.size(); m += 3) {
								int remi = removeds.get(m);
								int mn1 = m + 1;
								int mn2 = m + 2;
								int remj = removeds.get(mn1);
								gameBoard.cboard[remi][remj] = removeds.get(mn2);
							}
						}
						//Move undone
					}
				}
			}
		}
		return bestHeur;
	}
	
	//maxSearch! back to computer's viewpoint. difference is that minsearch and maxsearch return the optimized heuristics from
	//each of their perspectives, then minimax transforms them into the best move and returns the moves arraylist (takens and destinations)
	public static int maxSearch(int depth) {
		
		if(depth == 0) return gameBoard.score();
		
		int bestHeur = -99999;
		List<List<Integer>> holder = new ArrayList<List<Integer>>(1);
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				
				//if computer's turn, white.
				if((gameBoard.cboard[i][j] == 1) || (gameBoard.cboard[i][j] == 3)) {
					holder = gameBoard.getMoves(i, j);
					//For each move
					for(int k = 0; k < holder.size(); k++) {
						int listSize = holder.get(k).size();
						int destj = holder.get(k).get(listSize-1);
						int desti = holder.get(k).get(listSize-2);
		
						//Do move
						boolean kinged = false;
						gameBoard.cboard[desti][destj] = gameBoard.cboard[i][j];
						if((gameBoard.cboard[i][j] == 1) && (desti == 7)) {
							gameBoard.cboard[desti][destj] = 3; //Kinged
							kinged = true;
						}
						gameBoard.cboard[i][j] = 0;
						List<Integer> removeds = new ArrayList<Integer>(1);
						//if removeds' size is still 1 it means nothing has been removed!
						for(int l = 0; l < listSize-2; l += 2) {
							int remi = holder.get(k).get(l);
							int lnext = l + 1;
							int remj = holder.get(k).get(lnext);
							int piece = gameBoard.cboard[remi][remj];
							gameBoard.cboard[remi][remj] = 0;
							removeds.add(remi);
							removeds.add(remj);
							removeds.add(piece);
						}
						//Move done
						
						int value = minSearch(depth-1);
						if(value > bestHeur) {
							bestHeur = value;
						}
						
						//Undo move
						gameBoard.cboard[i][j] = gameBoard.cboard[desti][destj];
						if(kinged) gameBoard.cboard[i][j] = 1;
						gameBoard.cboard[desti][destj] = 0;
						if(removeds.size() > 1) {
							for(int m = 0; m < removeds.size(); m += 3) {
								int remi = removeds.get(m);
								int mn1 = m + 1;
								int mn2 = m + 2;
								int remj = removeds.get(mn1);
								gameBoard.cboard[remi][remj] = removeds.get(mn2);
							}
						}
						//Move undone
					}
				}
			}
		}
		return bestHeur;
	}

    public static class MoveHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange ex) throws IOException
        {
        	
        	System.err.println(ex.getRequestURI());
            String q = ex.getRequestURI().getQuery();

            char[] cha = q.toCharArray();
        	int leg = cha.length;
        	int[] inta = new int[leg];
        	for(int i = 0; i < leg; i++) {
        	    inta[i] = Character.getNumericValue(cha[i]);
        	}

        	int fromi = inta[0];
        	int fromj = inta[1];
        	int toi = inta[2];
        	int toj = inta[3];
        	boolean taken = false;
        	if((Math.abs(fromi - toi)) > 1) {
        		taken = true;
        		if(fromi !=  9) {
        			int takeni = (fromi + toi) / 2;
        			int takenj = (fromj + toj) / 2;
        			gameBoard.cboard[takeni][takenj] = 0;
        		}
    		}
        	if(fromi != 9) {
        		gameBoard.cboard[toi][toj] = gameBoard.cboard[fromi][fromj];
        		if((gameBoard.cboard[toi][toj] == 1) && (toi == 7)) gameBoard.cboard[toi][toj] = 3;
        		if((gameBoard.cboard[toi][toj] == 2) && (toi == 0)) gameBoard.cboard[toi][toj] = 4;
        		gameBoard.cboard[fromi][fromj] = 0;
        	}
    		
    		List<Integer> results = new ArrayList<Integer>(2);
    		if((taken) && (fromi == 9)) results = minimax(3);
    		if(!taken) results = minimax(3);
 		
            // write the response as JSON
    		String encoded = "{\"board\":\"";
    		for(int i = 0; i < 8; i++) {
    			for(int j = 0; j < 8; j++) encoded = encoded + Integer.toString(gameBoard.cboard[i][j]);
    		}
			encoded = encoded + "\"}";
			ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            byte[] responseBytes = encoded.getBytes();
            ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            ex.getResponseBody().write(responseBytes);
            ex.close();
        }
	}	
	
}
