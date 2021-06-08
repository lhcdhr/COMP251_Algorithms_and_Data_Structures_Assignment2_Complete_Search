import java.util.*;
import java.lang.*;
import java.io.*;
//Haochen Liu
//COMP 251 A2 Q1

public class Game {
	
	Board sudoku;
	
	public class Cell{
		private int row = 0;
		private int column = 0;
		
		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
		}
		public int getRow() {
			return row;
		}
		public int getColumn() {
			return column;
		}
	}
	
	public class Region{
		private Cell[] matrix;
		private int num_cells;
		public Region(int num_cells) {
			this.matrix = new Cell[num_cells];
			this.num_cells = num_cells;
		}
		public Cell[] getCells() {
			return matrix;
		}
		public void setCell(int pos, Cell element){
			matrix[pos] = element;
		}
		
	}
	
	public class Board{
		private int[][] board_values;
		private Region[] board_regions;
		private int num_rows;
		private int num_columns;
		private int num_regions;
		
		public Board(int num_rows,int num_columns, int num_regions){
			this.board_values = new int[num_rows][num_columns];
			this.board_regions = new Region[num_regions];
			this.num_rows = num_rows;
			this.num_columns = num_columns;
			this.num_regions = num_regions;
		}
		
		public int[][] getValues(){
			return board_values;
		}
		public int getValue(int row, int column) {
			return board_values[row][column];
		}
		public Region getRegion(int index) {
			return board_regions[index];
		}
		public Region[] getRegions(){
			return board_regions;
		}
		public void setValue(int row, int column, int value){
			board_values[row][column] = value;
		}
		public void setRegion(int index, Region initial_region) {
			board_regions[index] = initial_region;
		}	
		public void setValues(int[][] values) {
			board_values = values;
		}

	}
	//check which region does this cell belongs to
	private Region allocateRegion(int row, int column){
		for(int i=0;i < sudoku.num_regions;i++){
			Region tmpRegion = sudoku.getRegion(i);
			for(int j=0;j < tmpRegion.num_cells;j++){
				Cell tmpCell = tmpRegion.getCells()[j];
				if(tmpCell.getRow()==row && tmpCell.getColumn() == column) {
					return tmpRegion;
				}
			}
		}
		return null;
	}
	//check whether the current value of the cell fits its region
	private boolean checkRegion(int value, int row, int column){
		Region region = allocateRegion(row,column);
		Cell[] cells = region.getCells();
		int cIndex = 0;
		for(int i=0;i<region.num_cells;i++){
			if(cells[i].getRow()==row && cells[i].getColumn()==column){
				cIndex = i;
				break;
			}
		}
		for(int i=0;i< region.num_cells;i++){
			Cell tCell = region.getCells()[i];
			if(cIndex != i &&sudoku.getValue(tCell.row,tCell.column)==value){
				return false;
			}
		}
		return true;
	}
	// check whether the current value of the cell is different from its neighbors
	private boolean checkNeighbors(int value, int row, int column){
		//left top
		if(row-1 >= 0 && column -1 >= 0){
			if(sudoku.getValue(row-1,column-1)==value){
				return false;
			}
		}
		//mid top
		if(row-1 >= 0){
			if(sudoku.getValue(row-1,column)==value){
				return false;
			}
		}
		//right top
		if(row-1 >= 0 && column+1 < sudoku.num_columns){
			if(sudoku.getValue(row-1,column+1)==value){
				return false;
			}
		}
		//left mid
		if(column-1>=0){
			if(sudoku.getValue(row,column-1)==value){
				return false;
			}
		}
		//right mid
		if(column+1 < sudoku.num_columns){
			if(sudoku.getValue(row,column+1)==value){
				return false;
			}
		}
		//left bottom
		if(row+1 < sudoku.num_rows && column-1 >= 0){
			if(sudoku.getValue(row+1,column-1)==value){
				return false;
			}
		}
		//mid bottom
		if(row+1 < sudoku.num_rows){
			if(sudoku.getValue(row+1,column)==value){
				return false;
			}
		}
		//right bottom
		if(row+1 < sudoku.num_rows && column+1 < sudoku.num_columns){
			if(sudoku.getValue(row+1,column+1)==value){
				return false;
			}
		}
		return true;
	}

	private boolean filling(int row,int column){
		//terminate signal(at the end)
		if(row == sudoku.num_rows-1 && column == sudoku.num_columns){
			return true;
		}
		//go to the next row
		if(column==sudoku.num_columns){
			column=0;
			row++;
		}
		//if current is filled, then fill the next one
		if(sudoku.getValue(row,column) != -1){
			return filling(row,column+1);
		}
		// locate the region
		Region currentRegion = allocateRegion(row,column);
		// fill the current cell
		for(int j=1; j<=currentRegion.num_cells; j++){
			if(checkNeighbors(j,row,column) && checkRegion(j,row,column)){
				sudoku.setValue(row,column,j);
				if(filling(row,column+1)){
					return true;
				}
			}
			sudoku.setValue(row,column,-1);
		}
		// signal for backtracking
		return false;
	}

	public int[][] solver() {
		//To Do => Please start coding your solution here
		filling(0,0);
		return sudoku.getValues();
	}


	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int rows = sc.nextInt();
		int columns = sc.nextInt();
		int[][] board = new int[rows][columns];
		//Reading the board
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				String value = sc.next();
				if (value.equals("-")) {
					board[i][j] = -1;
				}else {
					try {
						board[i][j] = Integer.valueOf(value);
					}catch(Exception e) {
						System.out.println("Ups, something went wrong");
					}
				}	
			}
		}
		int regions = sc.nextInt();
		Game game = new Game();
	    game.sudoku = game.new Board(rows, columns, regions);
		game.sudoku.setValues(board);
		for (int i=0; i< regions;i++) {
			int num_cells = sc.nextInt();
			Game.Region new_region = game.new Region(num_cells);
			for (int j=0; j< num_cells; j++) {
				String cell = sc.next();
				String value1 = cell.substring(cell.indexOf("(") + 1, cell.indexOf(","));
				String value2 = cell.substring(cell.indexOf(",") + 1, cell.indexOf(")"));
				Game.Cell new_cell = game.new Cell(Integer.valueOf(value1)-1,Integer.valueOf(value2)-1);
				new_region.setCell(j, new_cell);
			}
			game.sudoku.setRegion(i, new_region);
		}
		int[][] answer = game.solver();
		for (int i=0; i<answer.length;i++) {
			for (int j=0; j<answer[0].length; j++) {
				System.out.print(answer[i][j]);
				if (j<answer[0].length -1) {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
}


