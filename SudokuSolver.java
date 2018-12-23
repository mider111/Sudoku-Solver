import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SudokuSolver {
    private int [][] myClue;
    private int [][] mySolution;
    /** Symbol used to indicate a blank grid position */
    public static final int BLANK = 0;
    /** Overall size of the grid */
    public static final int DIMENSION = 9;
    /** Size of a sub region */
    public static final int REGION_DIM = 3;

    // For debugging purposes -- see solve() skeleton.
    private Scanner kbd;
    private static final boolean DEBUG = false;
    
    /**
     * Run the solver. If args.length >= 1, use args[0] as the name of
     * a file containing a puzzle, otherwise, allow the user to browse
     * for a file.
     */
    public static void main(String [] args){
        String filename = null;
        if (args.length < 1) {
            // file dialog
            //filename = args[0];
            JFileChooser fileChooser = new JFileChooser();
            try {
                File f = new File(new File(".").getCanonicalPath());
                fileChooser.setCurrentDirectory(f);
            } catch (Exception ex) { System.out.println(ex.getMessage()); }
                        
            int retValue = fileChooser.showOpenDialog(new JFrame());
            
            if (retValue == JFileChooser.APPROVE_OPTION) {
                File theFile = fileChooser.getSelectedFile();
                filename = theFile.getAbsolutePath();
            } else {
                System.out.println("No file selected: exiting.");
                System.exit(0);
            }
        } else {
            filename = args[0];
        }
        
        SudokuSolver s = new SudokuSolver(filename);
        if (DEBUG)
            s.print();
        
        if (s.solve(0,0)){
	    // Pop up a window with the clue and the solution.
            s.display();
        } else {
            System.out.println("No solution is possible.");
        }
        
    }
    
 
    public SudokuSolver(String puzzleName){
        myClue = new int[DIMENSION][DIMENSION];
        mySolution = new int[DIMENSION][DIMENSION];
	// Set up keyboard input if we need it for debugging.
        if (DEBUG)
            kbd = new Scanner(System.in);
        
        File pf = new File(puzzleName);
        Scanner s = null;
        try {
            s = new Scanner(pf);
        } catch (FileNotFoundException f){
            System.out.println("Couldn't open file.");
            System.exit(1);
        }
        
        for (int i = 0; i < DIMENSION; i++){
            for (int j = 0; j < DIMENSION; j++){
                myClue[i][j] = s.nextInt();
            }
        }
        
        // Copy to solution
        for (int i = 0; i < DIMENSION; i++){
            for (int j = 0; j < DIMENSION; j++){
                mySolution[i][j] = myClue[i][j];
            }
        }
    }
    
    /**
     * Starting at a given grid position, generate values for all remaining
     * grid positions that do not violate the game constraints.
     *
     * @param row The row of the position to begin with
     * @param col The column of the position to begin with.
     *
     * @return true if a solution was found starting from this position,
     *          false if not.
     */
    public boolean solve(int row, int col){
	// This code will print the solution array and then wait for 
	// you to type "Enter" before proceeding. Helpful for debugging.
	// Set the DEBUG constant to true at the top of the class
	// declaration to turn this on.
        if (DEBUG) {
            System.out.println("solve(" + row + ", " + col + ")");
            print();
            kbd.nextLine();
        }
        
    	//Returns true if there are no more rows to check
        if(row>DIMENSION-1) {        
        	return true;
        }
        
        //If statement that checks if there is already a number assigned to a specific row and column
        if(mySolution[row][col] == BLANK){
        	
        	//For loop that tries all numbers from 1 to 9
        	for(int i=1;i<=DIMENSION;i++) {
        		
        		//Checks if the value is possible and assigns it if it is
        		if(positionIsOk(row,col,i)) {
        			mySolution[row][col]=i;
        			
        			//Check if the last column has been reached
        			if(col>=DIMENSION-1) {
        				
        				//If there is a possible solution for the next row return true
        				if(solve(row+1,0)) {
        					
        					return true;
        				}
        			}
        			else {
        				
        				//If there is a possible solution for the same row return true
        				if(solve(row,col+1)) {
        					
        					return true;
        				}
        			}
        		}
        	}
        }
        
        //If the row and column have already assigned values
        else {
        	
        	//Checks if last column was reached
        	if(col>=DIMENSION-1) {
        		
        		//If we can find solution for the next row returns true
        		return solve(row+1,0);
        	}
        	else {
        		
        		//If we can find solution for the same row returns true
        		return solve(row,col+1);
        	}
        }
        
        //If the number cannot be assigned makes the cell BLANK
        mySolution[row][col] = BLANK;
        return false;
    }
	
    /**
     * Checks if a number fits into a row
     * @param row Row to check
     * @param number Number to check
     * @return Returns false if the number can't be in given position and true otherwise 
     */
    private boolean checkRow(int row, int number) {
		for (int i = 0; i < DIMENSION; i++) {
			if (mySolution[row][i] == number)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if a number fits into a column.
	 * @param col Column to check
	 * @param number Number to check
	 * @return Returns false if the number can't be in given position and true otherwise 
	 */
	private boolean checkCol(int col, int number) {
		for (int i = 0; i < DIMENSION; i++) {
			if (mySolution[i][col] == number)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if a number fits into a 3x3 sub region
	 * @param row Row to check
	 * @param col Column to check
	 * @param number Number to check
	 * @return Returns false if the number can't be in given position and true otherwise 
	 */
	private boolean checkSubRegion(int row, int col, int number) {
		int r = row - row % 3;
		int c = col - col % 3;
		
		for (int i = r; i < r + REGION_DIM; i++) {
			for (int j = c; j < c + REGION_DIM; j++) {
				if (mySolution[i][j] == number)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Combines all check methods with a given row, column and number 
	 * and determines if the number can be in the given position
	 * @param row Row to check
	 * @param col Column to check
	 * @param number Number to check
	 * @return Returns true if the number can be in the given position and false otherwise 
	 */
	private boolean positionIsOk(int row, int col, int number) {
		return !checkRow(row, number)  &&  !checkCol(col, number)  &&  !checkSubRegion(row, col, number);
	}
	
    /**
     * Print a character-based representation of the solution array
     * on standard output.
     */
    public void print(){
        System.out.println("+---------+---------+---------+");
        for (int i = 0; i < DIMENSION; i++){
            System.out.println("|         |         |         |");
            System.out.print("|");
            for (int j = 0; j < DIMENSION; j++){
                System.out.print(" " + mySolution[i][j] + " ");
                if (j % REGION_DIM == (REGION_DIM - 1)){
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i % REGION_DIM == (REGION_DIM - 1)){
                System.out.println("|         |         |         |");
                System.out.println("+---------+---------+---------+");
            }
        }
    }
    
    /**
     * Pop up a window containing a nice representation of the original
     * puzzle and out solution.
     */
    public void display(){
        JFrame f = new DisplayFrame();
        f.pack();
        f.setVisible(true);
    }
    
    /**
     * GUI display for the clue and solution arrays.
     */
    private class DisplayFrame extends JFrame implements ActionListener {
        private JPanel mainPanel;
        
        private DisplayFrame(){
            mainPanel = new JPanel();
            mainPanel.add(buildBoardPanel(myClue, "Clue"));
            mainPanel.add(buildBoardPanel(mySolution, "Solution"));
            add(mainPanel, BorderLayout.CENTER);
            
            JButton b = new JButton("Quit");
            b.addActionListener(this);
            add(b, BorderLayout.SOUTH);
        }
        
        private JPanel buildBoardPanel(int [][] contents, String label){
            JPanel holder = new JPanel();
            JLabel l = new JLabel(label);
            BorderLayout b = new BorderLayout();
            holder.setLayout(b);
            holder.add(l, BorderLayout.NORTH);
            JPanel board = new JPanel();
            GridLayout g = new GridLayout(9,9);
            g.setHgap(0);
            g.setVgap(0);
            board.setLayout(g);
            Color [] colorChoices = new Color[2];
            colorChoices[0] = Color.WHITE;
            colorChoices[1] = Color.lightGray;
            int colorIdx = 0;
            int rowStartColorIdx = 0;
            
            for (int i = 0; i < DIMENSION; i++){
                if (i > 0 && i % REGION_DIM == 0)
                    rowStartColorIdx = (rowStartColorIdx+1)%2;
                colorIdx = rowStartColorIdx;
                for (int j = 0; j < DIMENSION; j++){
                    if (j > 0 && j % REGION_DIM == 0)
                        colorIdx = (colorIdx+1)%2;
                    JTextField t = new JTextField(""+ contents[i][j]);
                    if (contents[i][j] == 0)
                        t.setText("");
                    t.setPreferredSize(new Dimension(35,35));
                    t.setEditable(false);
                    t.setHorizontalAlignment(JTextField.CENTER);
                    t.setBackground(colorChoices[colorIdx]);
                    board.add(t);
                }
            }
            holder.add(board, BorderLayout.CENTER);
            return holder;
        }
        
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    }
}