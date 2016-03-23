//Board setup and moves for 8x8 English draughts aka American checkers. Author: Aneesh Pasricha '16. Last modified: Dec 11, 2014. 

import java.util.*;

public class Board {

    public int[][] cboard; //checkerboard

    public Board() {
    	cboard = new int[8][8];
    	//since black starts first, computer is white
    	//1: white piece, 2: black piece, 3: white king, 4: black king, 0: empty
    	
    	//placing white pieces
    	for(int i = 0; i < 3; i++) {
    		if(i%2 == 0) {
    			for(int j = 1; j < 8; j += 2) cboard[i][j] = 1;
    		}
    		else {
    			for(int j = 0; j < 7; j += 2) cboard[i][j] = 1;
    		}
    	}

    	//placing black pieces
    	for(int i = 5; i < 8; i++) {
    		if(i%2 == 0) {
    			for(int j = 1; j < 8; j += 2) cboard[i][j] = 2;
    		}
    		else {
    			for(int j = 0; j < 7; j += 2) cboard[i][j] = 2;
    		}
    	}
    }
 

    //evaluate score using stronger heuristics, 1 for normal piece, 2 for king
    public int score() {
    	int whitescore = 0;
    	int blackscore = 0;
    	for(int i = 0; i < 8; i++) {
    		for(int j = 0; j < 8; j++) {
    			if((cboard[i][j] == 1) || (cboard[i][j] == 3)) {
    				if(cboard[i][j] == 1) whitescore++;
    				else if(cboard[i][j] == 3) whitescore += 2;
    			}
    			if((cboard[i][j] == 2) || (cboard[i][j] == 4)) {
    				if(cboard[i][j] == 2) blackscore++;
    				else if(cboard[i][j] == 4) blackscore += 2;
    			}
    		}
    	}
    	return (whitescore - blackscore);
    } 

    //could be done better but this makes debugging easier

    public List<List<Integer>> getMoves(int i, int j) {
    	//i and j are current positions of piece
    	int it = i; //it and jt are the i and j I will modify, wanted to keep i and j pure and unmodified. 
    	int jt = j;
    	List<List<Integer>> moves = new ArrayList<List<Integer>>(1); //List of possible moves

    	//White non-king, i < 7 means piece can move. In checkers, non-kings can only move forward
    	//Throughout the method, the i<6 and i>1 and such are meant to ensure that the pieces say within the 8x8 board, i<7 for moving and i<6 for taking (coz taking makes you end up at i+2), same for j
    	if((cboard[i][j] == 1) && (i < 7)) {
    		//in checkers, if a take (kill) can occur, it must be done
    		//if a 'take' ie kill can occur, white piece can kill  2(black) or 4(black king), at i+1 and j+1 or j-1, by moving to i+2 and j+2 or j-2
    		if((i < 6) && (((j < 6) && ((cboard[i+1][j+1] == 2) || (cboard[i+1][j+1] == 4)) && (cboard[i+2][j+2] == 0)) || ((j > 1) && ((cboard[i+1][j-1] == 2) || (cboard[i+1][j-1] == 4)) && (cboard[i+2][j-2] == 0)))) {
    			List<Integer> moves3 = new ArrayList<Integer>(2); //list of pieces taken
    			//one must continue playing once a piece has been taken, until no more pieces can be taken
    			while((it < 6) && (((jt < 6) && ((cboard[it+1][jt+1] == 2) || (cboard[it+1][jt+1] == 4)) && (cboard[it+2][jt+2] == 0)) || ((jt > 1) && ((cboard[it+1][jt-1] == 2) || (cboard[it+1][jt-1] == 4)) && (cboard[it+2][jt-2] == 0)))) {
    				if((it < 6) && ((jt < 6) && ((cboard[it+1][jt+1] == 2) || (cboard[it+1][jt+1] == 4)) && (cboard[it+2][jt+2] == 0))) {
    					moves3.add(it+1); //piece taken!
    					moves3.add(jt+1); //piece taken at i+1, j+1
    					it += 2; //the white piece has moved to (i+2, j+2) after skipping over the taken piece
    					jt += 2;
    				}    
    				else if((it < 6) && ((jt > 1) && ((cboard[it+1][jt-1] == 2) || (cboard[it+1][jt-1] == 4)) && (cboard[it+2][jt-2] == 0))) {
    					moves3.add(it+1);
    					moves3.add(jt-1);
    					it += 2; //the white piece has moved to (i+2, j-2)
    					jt -= 2;
    				}
    			}
    			moves3.add(it); //it and jt are the final ending places of the white pieces after all 'takes' are completed. 
    			moves3.add(jt);
    			moves.add(moves3); //so now moves holds the list of all pieces taken, ending with the final location of the 'taker' piece
    		}
    		
    		//if you can't take, only then do you have all options for moves
    		else {
    			if(((j < 7) && (cboard[i+1][j+1] == 0)) || ((j > 0) && (cboard[i+1][j-1] == 0))) {
    				if((j < 7) && (cboard[i+1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i+1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((j > 0) && (cboard[i+1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i+1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    			}
    		}
    	}
    	//That completes all possibilites for a plain white non-king

    	//Black non-king. everything similar to the white non-king, just switched to be from the black piece's perspective
    	else if((cboard[i][j] == 2) && (i > 0)) {
    		if((i > 1) && (((j < 6) && ((cboard[i-1][j+1] == 1) || (cboard[i-1][j+1] == 3)) && (cboard[i-2][j+2] == 0)) || ((j > 1) && ((cboard[i-1][j-1] == 1) || (cboard[i-1][j-1] == 3)) && (cboard[i-2][j-2] == 0)))) {
    			List<Integer> moves3 = new ArrayList<Integer>(2);
    			while((it > 1) && (((jt < 6) && ((cboard[it-1][jt+1] == 1) || (cboard[it-1][jt+1] == 3)) && (cboard[it-2][jt+2] == 0)) || ((jt > 1) && ((cboard[it-1][jt-1] == 1) || (cboard[it-1][jt-1] == 3)) && (cboard[it-2][jt-2] == 0)))) {
    				if((it > 1) && ((jt < 6) && ((cboard[it-1][jt+1] == 1) || (cboard[it-1][jt+1] == 3)) && (cboard[it-2][jt+2] == 0))) {
    					moves3.add(it-1);
    					moves3.add(jt+1);
    					it -= 2;
    					jt += 2;
    				}    
    				else if((it > 1) && ((jt > 1) && ((cboard[it-1][jt-1] == 1) || (cboard[it-1][jt-1] == 3)) && (cboard[it-2][jt-2] == 0))) {
    					moves3.add(it-1);
    					moves3.add(jt-1);
    					it -= 2;
    					jt -= 2;
    				}
    			}
    			moves3.add(it);
    			moves3.add(jt);
    			moves.add(moves3);
    		}
    		//if it can take it must. else you have all options for moves
    		else {
    			if(((j < 7) && (cboard[i-1][j+1] == 0)) || ((j > 0) && (cboard[i-1][j-1] == 0))) {
    				if((j < 7) && (cboard[i-1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i-1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((j > 0) && (cboard[i-1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i-1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    			}
    		}
    	}

    	//White king. The difference is that normal pieces can only move forward, kings can move forward and backward
    	//all the ifs and moves are the same as that for the non-kings I described above, just with the extra 'backwards' case
    	else if(cboard[i][j] == 3) {
    		//This monster if statement just says 'If a take is possible, do it' using all cases of movement of the king, ie, front-right, front-left, back-right and back-left
    		if(((i < 6) && (((j < 6) && ((cboard[i+1][j+1] == 2) || (cboard[i+1][j+1] == 4)) && (cboard[i+2][j+2] == 0)) || ((j > 1) && ((cboard[i+1][j-1] == 2) || (cboard[i+1][j-1] == 4)) && (cboard[i+2][j-2] == 0)))) || ((i > 1) && (((j < 6) && ((cboard[i-1][j+1] == 2) || (cboard[i-1][j+1] == 4)) && (cboard[i-2][j+2] == 0)) || ((j > 1) && ((cboard[i-1][j-1] == 2) || (cboard[i-1][j-1] == 4)) && (cboard[i-2][j-2] == 0))))) {
        		List<Integer> moves3 = new ArrayList<Integer>(2); //to hold pieces taken and lastly, final location of taker piece
    			while(((it < 6) && (((jt < 6) && ((cboard[it+1][jt+1] == 2) || (cboard[it+1][jt+1] == 4)) && (cboard[it+2][jt+2] == 0)) || ((jt > 1) && ((cboard[it+1][jt-1] == 2) || (cboard[it+1][jt-1] == 4)) && (cboard[it+2][jt-2] == 0)))) || ((it > 1) && (((jt < 6) && ((cboard[it-1][jt+1] == 2) || (cboard[it-1][jt+1] == 4)) && (cboard[it-2][jt+2] == 0)) || ((jt > 1) && ((cboard[it-1][jt-1] == 2) || (cboard[it-1][jt-1] == 4)) && (cboard[it-2][jt-2] == 0))))) {
    				if((it < 6) && (((jt < 6) && ((cboard[it+1][jt+1] == 2) || (cboard[it+1][jt+1] == 4)) && (cboard[it+2][jt+2] == 0)) || ((jt > 1) && ((cboard[it+1][jt-1] == 2) || (cboard[it+1][jt-1] == 4)) && (cboard[it+2][jt-2] == 0)))) {
    					//take towards the front, right
    					if((it < 6) && ((jt < 6) && ((cboard[it+1][jt+1] == 2) || (cboard[it+1][jt+1] == 4)) && (cboard[it+2][jt+2] == 0))) {
    						moves3.add(it+1); //it+1, jt+1 taken!
    						moves3.add(jt+1);
    						it += 2;
    						jt += 2;
    					}    
    					//take towards the front, left
    					else if((it < 6) && ((jt > 1) && ((cboard[it+1][jt-1] == 2) || (cboard[it+1][jt-1] == 4)) && (cboard[it+2][jt-2] == 0))) {
    						moves3.add(it+1);
    						moves3.add(jt-1);
    						it += 2;
    						jt -= 2;
    					}
    				}
    				//back right
    				else if((it > 1) && (((jt < 6) && ((cboard[it-1][jt+1] == 2) || (cboard[it-1][jt+1] == 4)) && (cboard[it-2][jt+2] == 0)) || ((jt > 1) && ((cboard[it-1][jt-1] == 2) || (cboard[it-1][jt-1] == 4)) && (cboard[it-2][jt-2] == 0)))) {
    					if((it > 1) && ((jt < 6) && ((cboard[it-1][jt+1] == 2) || (cboard[it-1][jt+1] == 4)) && (cboard[it-2][jt+2] == 0))) {
    						moves3.add(it-1);
    						moves3.add(jt+1);
    						it -= 2;
    						jt += 2;
    					}   
    					//back left
    					else if((it > 1) && ((jt > 1) && ((cboard[it-1][jt-1] == 2) || (cboard[it-1][jt-1] == 4)) && (cboard[it-2][jt-2] == 0))) {
    						moves3.add(it-1);
    						moves3.add(jt-1);
    						it -= 2;
    						jt -= 2;
    					}
    				}
    			}
				moves3.add(it);
				moves3.add(jt);
				moves.add(moves3);
    		}
    		//if you cannot take. this is the same as the non-king, except with the extra 2 cases of being able to go backwards too
    		else {
    			if(((i < 7) && (j < 7) && (cboard[i+1][j+1] == 0)) || ((i < 7) && (j > 0) && (cboard[i+1][j-1] == 0)) || ((i > 0) && (j < 7) && (cboard[i-1][j+1] == 0)) || ((i > 0) && (j > 0) && (cboard[i-1][j-1] == 0))) {
    				if((i < 7) && (j < 7) && (cboard[i+1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i+1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((i < 7) && (j > 0) && (cboard[i+1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i+1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    				if((i > 0) && (j < 7) && (cboard[i-1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i-1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((i > 0) && (j > 0) && (cboard[i-1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i-1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    			}
    		}
    	}
    	
    	//Black king
    	else if(cboard[i][j] == 4) {
    		if(((i < 6) && (((j < 6) && ((cboard[i+1][j+1] == 1) || (cboard[i+1][j+1] == 3)) && (cboard[i+2][j+2] == 0)) || ((j > 1) && ((cboard[i+1][j-1] == 1) || (cboard[i+1][j-1] == 3)) && (cboard[i+2][j-2] == 0)))) || ((i > 1) && (((j < 6) && ((cboard[i-1][j+1] == 1) || (cboard[i-1][j+1] == 3)) && (cboard[i-2][j+2] == 0)) || ((j > 1) && ((cboard[i-1][j-1] == 1) || (cboard[i-1][j-1] == 3)) && (cboard[i-2][j-2] == 0))))) {
        		List<Integer> moves3 = new ArrayList<Integer>(2);
    			while(((it < 6) && (((jt < 6) && ((cboard[it+1][jt+1] == 1) || (cboard[it+1][jt+1] == 3)) && (cboard[it+2][jt+2] == 0)) || ((jt > 1) && ((cboard[it+1][jt-1] == 1) || (cboard[it+1][jt-1] == 3)) && (cboard[it+2][jt-2] == 0)))) || ((it > 1) && (((jt < 6) && ((cboard[it-1][jt+1] == 1) || (cboard[it-1][jt+1] == 3)) && (cboard[it-2][jt+2] == 0)) || ((jt > 1) && ((cboard[it-1][jt-1] == 1) || (cboard[it-1][jt-1] == 3)) && (cboard[it-2][jt-2] == 0))))) {
    				if((it < 6) && (((jt < 6) && ((cboard[it+1][jt+1] == 1) || (cboard[it+1][jt+1] == 3)) && (cboard[it+2][jt+2] == 0)) || ((jt > 1) && ((cboard[it+1][jt-1] == 1) || (cboard[it+1][jt-1] == 3)) && (cboard[it+2][jt-2] == 0)))) {
    					if((it < 6) && ((jt < 6) && ((cboard[it+1][jt+1] == 1) || (cboard[it+1][jt+1] == 3)) && (cboard[it+2][jt+2] == 0))) {
    						moves3.add(it+1);
    						moves3.add(jt+1);
    						it += 2;
    						jt += 2;
    					}    
    					else if((it < 6) && ((jt > 1) && ((cboard[it+1][jt-1] == 1) || (cboard[it+1][jt-1] == 3)) && (cboard[it+2][jt-2] == 0))) {
    						moves3.add(it+1);
    						moves3.add(jt-1);
    						it += 2;
    						jt -= 2;
    					}
    				}
    				else if((it > 1) && (((jt < 6) && ((cboard[it-1][jt+1] == 1) || (cboard[it-1][jt+1] == 3)) && (cboard[it-2][jt+2] == 0)) || ((jt > 1) && ((cboard[it-1][jt-1] == 1) || (cboard[it-1][jt-1] == 3)) && (cboard[it-2][jt-2] == 0)))) {
    					if((it > 1) && ((jt < 6) && ((cboard[it-1][jt+1] == 1) || (cboard[it-1][jt+1] == 3)) && (cboard[it-2][jt+2] == 0))) {
    						moves3.add(it-1);
    						moves3.add(jt+1);
    						it -= 2;
    						jt += 2;
    					}    
    					else if((it > 1) && ((jt > 1) && ((cboard[it-1][jt-1] == 1) || (cboard[it-1][jt-1] == 3)) && (cboard[it-2][jt-2] == 0))) {
    						moves3.add(it-1);
    						moves3.add(jt-1);
    						it -= 2;
    						jt -= 2;
    					}
    				}
    			}
				moves3.add(it);
				moves3.add(jt);
				moves.add(moves3);
    		}
    		else {
    			if(((i < 7) && (j < 7) && (cboard[i+1][j+1] == 0)) || ((i < 7) && (j > 0) && (cboard[i+1][j-1] == 0)) || ((i > 0) && (j < 7) && (cboard[i-1][j+1] == 0)) || ((i > 0) && (j > 0) && (cboard[i-1][j-1] == 0))) {
    				if((i < 7) && (j < 7) && (cboard[i+1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i+1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((i < 7) && (j > 0) && (cboard[i+1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i+1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    				if((i > 0) && (j < 7) && (cboard[i-1][j+1] == 0)) {
    					List<Integer> moves1 = new ArrayList<Integer>(2);
    					moves1.add(i-1);
    					moves1.add(j+1);
    					moves.add(moves1);
    				}
    				if((i > 0) && (j > 0) && (cboard[i-1][j-1] == 0)) {
    					List<Integer> moves2 = new ArrayList<Integer>(2);
    					moves2.add(i-1);
    					moves2.add(j-1);
    					moves.add(moves2);
    				}
    			}
    		}
    	}
    	
    	return moves;
    }
    
}