import java.util.Vector;

/**
 * Class that hold the dynamic elements of a board and
 * provides the methods to work with them.
 * 
 * @author Erik
 *
 */
public class State {
	public final Board board;
	public final BoardPosition playerPosition;
	public final Vector<BoardPosition> boxPositions;
	
	public final BoardConnectivity connectivity;
	public final State rootState;
	public long hash = 0;
	
	/**
	 * Construct a new state from a static board, the players postion and
	 * a list of box positions.
	 * 
	 * @param board the static board
	 * @param playerPosition the player's initial position
	 * @param boxPositions the boxes' initial positions
	 */
	public State(Board board, BoardPosition playerPosition,
			Vector<BoardPosition> boxPositions) {
		this.board = board;
		this.playerPosition = playerPosition;
		this.boxPositions 	= boxPositions;
		this.rootState 		= null;
		this.connectivity 	= new BoardConnectivity(this);
		setHash();
	}
	
	/**
	 * Constructs a new state by pushing a box.
	 * 
	 * @param parent the parent state
	 * @param playerPosition the original location of the box being pushed
	 * @param move the move made to push the box, from <code>BoardConnectivity</code>
	 */
	public State(State parent, BoardPosition oldBoxPosition, byte move) {
		this.rootState	 	= parent;
		this.board 			= parent.board;
		this.playerPosition = new BoardPosition(oldBoxPosition);
		
		Vector<BoardPosition> bpv = new Vector<BoardPosition>();
		for(BoardPosition bp : parent.boxPositions) {
			if(!bp.equals(oldBoxPosition)) {
				bpv.add(new BoardPosition(bp));
			}
		}
		this.boxPositions = bpv;
		
		byte row = (byte) (oldBoxPosition.row + BoardConnectivity.rowMask[move]);
		byte col = (byte) (oldBoxPosition.col + BoardConnectivity.colMask[move]);
		
		BoardPosition newBox = new BoardPosition(row, col);
		boxPositions.add(newBox);
		
		connectivity = new BoardConnectivity(this);
		setHash();
	}
	
	/**
	 * Calculates the hash value for the current state.
	 * 
	 * This should conform to the definition of state equality.
	 */
	private void setHash() {
		for(byte i=0; i<Board.rows+2; i++) {
			for(byte j=0; j<Board.cols+2; j++) {
				if(connectivity.isReachable(i,j)) {
					hash ^= Board.zValues[i][j];
				}
			}
		}
		for (BoardPosition bp : boxPositions) {
			hash ^= Board.zValues[bp.row][bp.col];
		}
	}
	
	/**
	 * Checks to see if the board has been solved.
	 * 
	 * @return
	 */
	public final boolean isSolved() {
		int boxesOnGoal = boxesOnGoals();
	
		if (boxesOnGoal == Board.goalPositions.size()) {
			return true;
		}

		return false;
	}
	
	/**
	 * Counts the number of boxes on goal squares.
	 * 
	 * @return
	 */
	public byte boxesOnGoals() {
		byte sum = 0;
		for (BoardPosition boxCoordinate : boxPositions) {
			if (board.goalAt(boxCoordinate.row, boxCoordinate.col)) {
				sum++;
			}
		}

		return sum;
	}
	
	/**
	 * Returns true if the player can't enter the square.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isOccupied(byte row, byte col) {
		return board.wallAt(row, col) || boxAt(row, col);
	}

	public boolean boxAt(byte row, byte col) {
		for (BoardPosition bc : boxPositions) {
			if (bc.row == row && bc.col == col) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			return equals((State) obj);
		}

		return false;
	}

	/**
	 * Two states are considered equal if they have the same connectivity
	 * and the boxes are at the same locations.
	 * 
	 * @param state
	 * @return
	 */
	private final boolean equals(State state) {
		boolean hasSameBoxPos = state.boxPositions.containsAll(this.boxPositions);
		boolean hasSameConnectivity = state.connectivity.equals(this.connectivity);
		
		return hasSameBoxPos && hasSameConnectivity;
	}
}