import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Guireplay extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Guireplay(State state) // the frame constructor method
	{
		super("Sokoban Solution Display: ");

		setVisible(true); // display this frame
		

		setBounds(100, 100, Board.cols*40, Board.rows*40);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container con = this.getContentPane(); // inherit main frame

		Stack<State> solutionStack = new Stack<State>();

		solutionStack.push(state);
		while (state.parent != null) {
			state = state.parent;
			solutionStack.push(state);
		}

		State parent = solutionStack.pop();
		while(solutionStack.size()>0) {
		    if(parent.playerPosition != null) {
		        con.add(getPlot(parent, parent.playerPosition));
		        revalidate();
		        pause();
		    }

			State child = solutionStack.pop();

			List<BoardPosition> positionSequence = child.getPositionSequence();
			for(BoardPosition pos : positionSequence) {
				con.removeAll();
				con.add(getPlot(parent, pos));
				revalidate();
				pause();
			}

			parent = child;
		}
		con.removeAll();
		con.add(getPlot(parent, parent.playerPosition));
		revalidate();
		pause();
	}

	private JPanel getPlot(State state, BoardPosition playerPos)
	{
		JPanel pane = new JPanel(new GridLayout(Board.rows, Board.cols));
		for (byte i = 1; i <= Board.rows; i++) {
			for (byte j = 1; j <= Board.cols; j++) {
				JButton entity = new JButton();
				if (playerPos.equals(new BoardPosition(i, j)))
					entity.setBackground(Color.BLUE);
				else if (state.boxAt(i, j) && Board.goalAt(i, j))
					entity.setBackground(new Color(205,133,63));
				else if (state.boxAt(i, j))
					entity.setBackground(new Color(235,173,83));
				else if (Board.goalAt(i, j))
					entity.setBackground(Color.YELLOW);
				else if (Board.wallAt(i, j))
					entity.setBackground(Color.DARK_GRAY);
				else
					entity.setBackground(Color.LIGHT_GRAY);

				pane.add(entity);
			}
		}
		return pane;
	}

	private void pause() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void revalidate() {
	    invalidate();
	    validate();
	}
}
