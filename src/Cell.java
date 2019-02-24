public class Cell {
		static int cellSize = 20;//50;
	
		private char cellContent;
		private boolean containsBomb;
		
		public Cell(char cellContent) {
			this.cellContent = cellContent;
			containsBomb = false;
		}
		
		/***Setters and Getters****/
		
		public char getCellContent() {
			return cellContent;
		}
		public void setCellContent(char newContent) {
			cellContent = newContent;
		}
		
		public boolean getContainsBomb() {
			return containsBomb;
		}
		public void setContainsBomb(boolean containsBomb) {
			this.containsBomb = containsBomb;
		}
		/*************************/
	}
