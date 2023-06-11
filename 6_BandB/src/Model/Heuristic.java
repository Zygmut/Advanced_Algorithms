package Model;

public enum Heuristic {
	BAD_POSITION {
		@Override
		public int apply(Board board) {
			int badPlacedPieces = 0;
			int value = 1;
			int[][] state = board.getState();
			int size = state.length;

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {

					if (state[i][j] != value++) {
						badPlacedPieces++;
					}
				}
			}

			return badPlacedPieces;
		}
	},
	MANHATTAN {
		@Override
		public int apply(Board board) {
			int totalDistance = 0;
			int[][] state = board.getState();
			int size = state.length;

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					int value = state[i][j];

					if (value != -1) {
						int goalRow = (value - 1) / size;
						int goalCol = (value - 1) % size;
						int distance = Math.abs(i - goalRow) + Math.abs(j - goalCol);
						totalDistance += distance;
					}
				}
			}

			return totalDistance;
		}
	},

	LINEAR_CONFLICT {
		@Override
		public int apply(Board board) {
			int linearConflict = 0;
			int[][] state = board.getState();
			int size = state.length;

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					int value = state[i][j];

					if (value != -1) {
						int goalRow = (value - 1) / size;
						int goalCol = (value - 1) % size;

						if (i == goalRow && j == goalCol) {
							continue;
						}

						if (i == goalRow && state[i][goalCol] != goalCol + 1) {
							linearConflict++;
						}

						if (j == goalCol && state[goalRow][j] != (goalRow * size) + j + 1) {
							linearConflict++;
						}
					}
				}
			}

			return linearConflict;
		}
	};

	public int apply(Board board) {
		throw new UnsupportedOperationException();
	}
}
