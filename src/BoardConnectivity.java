import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Class used to analyze the connectivity of a board state.
 * 
 * @author Erik
 *
 */
public class BoardConnectivity {

	private Move[][] connectivity;

	/**
	 * Constructs the connectivity matrix for the 
	 * supplied state.
	 * 
	 * @param state State used for connectivity graph
	 */
	public BoardConnectivity(State state) {
		connectivity = new Move[Board.rows+2][Board.cols+2];

		for(int i=0; i<Board.rows+2; i++) {
			for(int j=0; j<Board.cols+2; j++) {
				connectivity[i][j] = Move.NO_MOVE;
			}
		}

		initialize(state);
	}

	/**
	 * Uses a flood-fill algorithm to mark all reachable squares.
	 * 
	 * @param state State checked for connectivity
	 */
	private void initialize(State state) {
	    if(state.playerPosition == null) {
	        // We may start anywhere we like. This is the case for backward search.
	        for(byte i=1; i<connectivity.length; ++i) {
	            for(byte j=0; j<connectivity[i].length; ++j) {
	                if(!state.isOccupied(new BoardPosition(i, j))) {
	                    connectivity[i][j] = Move.NULL;
	                }
	            }
	        }
	    } else {
    		Queue<BoardPosition> positionsToExpand = new LinkedList<BoardPosition>();
    
    		positionsToExpand.add(state.playerPosition);
    		byte playerRow = state.playerPosition.row;
    		byte playerCol = state.playerPosition.col;
    
    		// The players position is reached via the null move.
    		connectivity[playerRow][playerCol] = Move.NULL;
    
    		while(!positionsToExpand.isEmpty()) {
    
    			BoardPosition currenPos = positionsToExpand.poll();
    
    			for(Move move : Move.DIRECTIONS) {
    				BoardPosition toPos = move.stepFrom(currenPos);
    
    				if(!state.isOccupied(toPos) && connectivity[toPos.row][toPos.col] == Move.NO_MOVE) {
    					connectivity[toPos.row][toPos.col] =  move;
    					positionsToExpand.add(toPos);
    				}
    			}
    		}
	    }
	}
	
	public List<Move> backtrackPathMoves(BoardPosition endPos, BoardPosition startPos) {
		List<Move> movesList = new LinkedList<Move>();
		
		if(!isReachable(endPos)) {
			throw new RuntimeException("Backtracking started on unreachable square " + endPos);
		}
		
		BoardPosition pos = endPos;
		Move move = connectivity[pos.row][pos.col];
		
		while(move != Move.NULL) {
			if(move==Move.NO_MOVE) {
				throw new RuntimeException("Backtracking led to unreachable square " + pos);
			}
			movesList.add(move);
			
			pos = move.stepBack(pos);
			move = connectivity[pos.row][pos.col];
		}
		
		return movesList;
	}
	
	public String backtrackPathString(BoardPosition endPos, BoardPosition startPos) {
		String result = "";
		
		List<Move> movesList = backtrackPathMoves(endPos, startPos);
		
		for(Move m : movesList) {
			result += m.moveChar;
		}
		
		return result;
	}
	
	public boolean isReachable(BoardPosition pos) {
		return isReachable(pos.row, pos.col);
	}

	public boolean isReachable(byte row, byte col) {
		return connectivity[row][col] != Move.NO_MOVE;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoardConnectivity) {
			return equals((BoardConnectivity) obj);
		}

		return false;
	}

	/**
	 * Two connectivity objects are seen as equal if
	 * exactly the same squares are reachable in both.
	 * 
	 * @param bc
	 * @return
	 */
	private boolean equals(BoardConnectivity bc) {
		for(int i=1; i<=Board.rows; i++) {
			for(int j=1; j<=Board.cols; j++) {
				if((connectivity[i][j] == Move.NO_MOVE) != (bc.connectivity[i][j] == Move.NO_MOVE)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String result = "";

		for(int i=1; i<=Board.rows; i++) {
			for(int j=1; j<=Board.cols; j++) {
				switch (connectivity[i][j]) {
				case RIGHT:
					result += "R";
					break;
				case UP:
					result += "U";
					break;
				case LEFT:
					result += "L";
					break;
				case DOWN:
					result += "D";
					break;
				case NULL:
					result += "X";
					break;
				case NO_MOVE:
					result += "-";
					break;

				}
			}
			result += "\n";
		}
		
		return result;
	}
}
