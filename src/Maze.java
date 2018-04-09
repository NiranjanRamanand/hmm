package hmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Maze {
	int [][] coloring;
	int numColors;
	double [][] probs;
	final double errorRate = .12;

	public Maze(int [][] coloring, int numColors){
		this.coloring = coloring;
		this.numColors = numColors;
		int walls = 0;

		for(int [] row : coloring){
			for(int entry : row){
				if(entry == -1)
					walls++;
			}
		}

		double prob = (coloring.length * coloring[0].length - walls);
		probs = new double[coloring.length][coloring[0].length];

		for(int i = 0; i < coloring.length; i++){
			for(int j = 0; j < coloring[0].length; j++){
				if(coloring[i][j] != -1)
					probs[i][j] = 1/prob;
			}
		}



	}

	public void update(int color){
		double [] probMovesCorrect = new double [4]; //N, S, E, W
		double [] probMovesIncorrect = new double [4]; //N, S, E, W
		double [] [] updatedProbs = new double[probs.length][probs[0].length];

		//Find the probabilities of moving in a certain direction
		for(int i = 0; i < coloring.length; i++){
			for(int j = 0; j < coloring[0].length; j++){

				if(coloring[i][j] != -1){
					boolean hasNorth = false, hasSouth = false, hasEast = false, hasWest = false;
					int surrounding = 0;
				/*   HANDLING CASE WHERE SENSOR IS CORRECT   */
					//get number of surrounding color
					if((coloring[i][j] == color && (i == 0 || coloring[i - 1][j] == -1)) || (i != 0 && coloring[i - 1][j] == color)){//Not north-most or south of wall
						surrounding++;
						hasNorth = true;
					}

					if((coloring[i][j] == color && (i == coloring.length - 1 || coloring[i + 1][j] == -1)) ||(i != coloring.length - 1 && coloring[i + 1][j] == color)){
						surrounding++;
						hasSouth = true;
					}

					if((coloring[i][j] == color && (j == 0 || coloring[i][j - 1] == -1)) || (j != 0 && coloring[i][j - 1] == color)){
						surrounding++;
						hasWest = true;
					}

					if((coloring[i][j] == color && (j == coloring[0].length - 1 || coloring[i][j + 1] == -1)) || ( j != coloring[0].length - 1) && coloring[i][j + 1] == color){
						surrounding++;
						hasEast = true;
					}

					//updating probs
					if(surrounding > 0){
						if(hasNorth){
							probMovesCorrect[0] += probs[i][j]/surrounding;
						}

						if(hasSouth){
							probMovesCorrect[1] += probs[i][j]/surrounding;
						}

						if(hasEast){
							probMovesCorrect[2] += probs[i][j]/surrounding;
						}

						if(hasWest){
							probMovesCorrect[3] += probs[i][j]/surrounding;
						}
					}
					hasNorth = false;
					hasSouth = false;
					hasEast = false;
					hasWest = false;

					surrounding = 0;
					/*   HANDLING CASE WHERE SENSOR IS NOT CORRECT   */

					if((coloring[i][j] != color && (i == 0 || coloring[i - 1][j] == -1)) || (i != 0 && coloring[i - 1][j] != color)){//Not north-most or south of wall
						surrounding++;
						hasNorth = true;
					}

					if((coloring[i][j] != color && (i == coloring.length - 1 || coloring[i + 1][j] == -1)) ||(i != coloring.length - 1 && coloring[i + 1][j] != color)){
						surrounding++;
						hasSouth = true;
					}

					if((coloring[i][j] != color && (j == 0 || coloring[i][j - 1] == -1)) || (j != 0 && coloring[i][j - 1] != color)){
						surrounding++;
						hasWest = true;
					}

					if((coloring[i][j] != color && (j == coloring[0].length - 1 || coloring[i][j + 1] == -1)) || ( j != coloring[0].length - 1) && coloring[i][j + 1] != color){
						surrounding++;
						hasEast = true;
					}
					//updating probs
					if(surrounding > 0){
						if(hasNorth){
							probMovesIncorrect[0] += probs[i][j]/surrounding;
						}

						if(hasSouth){
							probMovesIncorrect[1] += probs[i][j]/surrounding;
						}

						if(hasEast){
							probMovesIncorrect[2] += probs[i][j]/surrounding;
						}

						if(hasWest){
							probMovesIncorrect[3] += probs[i][j]/surrounding;
						}
					}
				}
			}
		}

		//Update probabilities
		for(int i = 0; i < coloring.length; i++){
			for(int j = 0; j < coloring[0].length; j++){
				if(coloring[i][j] != -1){
					//Moving up
					if(((i == 0 || coloring[i - 1][j] == -1) && coloring[i][j] == color)){
						updatedProbs[i][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[0];
					} else if(i != 0 && coloring[i - 1][j] == color){//Not north-most or south of wall
						updatedProbs[i - 1][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[0];
					} else if (((i == 0 || coloring[i - 1][j] == -1) && coloring[i][j] != color)){
						updatedProbs[i][j] += errorRate * probs[i][j] * probMovesIncorrect[0];
					} else if(i != 0 && coloring[i - 1][j] != color && coloring[i - 1][j] != -1){
						updatedProbs[i - 1][j] += errorRate * probs[i][j] * probMovesIncorrect[0];
					}

					//Move to from above
					if(((i == coloring.length - 1 || coloring[i + 1][j] == -1) && coloring[i][j] == color)){
						updatedProbs[i][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[1];
					} else if(i != coloring.length - 1 && coloring[i + 1][j] == color){
						updatedProbs[i + 1][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[1];
					} else if(((i == coloring.length - 1 || coloring[i + 1][j] == -1) && coloring[i][j] != color)){
						updatedProbs[i][j] += errorRate * probs[i][j] * probMovesIncorrect[1];
					} else if(i != coloring.length - 1 && coloring[i + 1][j] != color && coloring[i + 1][j] != -1){
						updatedProbs[i + 1][j] += errorRate * probs[i][j] * probMovesIncorrect[1];
					}

					//Move to from west
					if(((j == 0 || coloring[i][j - 1] == -1) && coloring[i][j] == color)){
						updatedProbs[i][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[3];
					} else if(j != 0 && coloring[i][j - 1] == color){
						updatedProbs[i][j - 1] += (1 - errorRate) * probs[i][j] * probMovesCorrect[3];
					} else if(((j == 0 || coloring[i][j - 1] == -1) && coloring[i][j] != color)){
						updatedProbs[i][j] += errorRate * probs[i][j] * probMovesIncorrect[3];
					} else if(j != 0 && coloring[i][j - 1] != color && coloring[i][j - 1] != -1){
						updatedProbs[i][j - 1] += errorRate * probs[i][j] * probMovesIncorrect[3];
					}
					//Move to from east
					if(((j == coloring[0].length - 1 || coloring[i][j + 1] == -1) && coloring[i][j] == color)){
						updatedProbs[i][j] += (1 - errorRate) * probs[i][j] * probMovesCorrect[2];
					} else if(j != coloring[0].length - 1 && coloring[i][j + 1] == color){
						updatedProbs[i][j + 1] += (1 - errorRate) * probs[i][j] * probMovesCorrect[2];
					} else if(((j == coloring[0].length - 1 || coloring[i][j + 1] == -1) && coloring[i][j] != color)){
						updatedProbs[i][j] += errorRate * probs[i][j] * probMovesIncorrect[2];
					} else if(j != coloring[0].length - 1 && coloring[i][j + 1] != color && coloring[i][j + 1] != -1){
						updatedProbs[i][j + 1] += errorRate * probs[i][j] * probMovesIncorrect[2];
					}
				}
			}
		}

	probs = normalize(updatedProbs);

	}

	public double [][] normalize(double [][] matrix){
		double total = 0;

		for(double [] arr : matrix){
			for(double d : arr){
				total += d;
			}
		}

		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[0].length; j++){
				matrix[i][j] /= total;
			}
		}

		return matrix;
	}

	public void printMaze(){
		for(int i = 0; i < probs.length; i++){
			for(int j = 0; j < probs[0].length; j++){
				if(probs[i][j] != 0)
					System.out.print(String.format("%.2f", probs[i][j]) + " (" + coloring[i][j]+ ") \t");
				else
					System.out.print("#\t\t");
			}
			System.out.println();
		}
	}

	public int colorAt(int x, int y){
		return coloring[x][y];
	}

	public int [] getMax(){
		double max = 0;
		int [] coords = new int[2];

		for(int i = 0; i < probs.length; i++){
			for(int j = 0; j < probs[0].length; j++){
				if(probs[i][j] > max){
					max = probs[i][j];
					coords[0] = j;
					coords[1] = i;
				}
			}
		}
		return coords;
	}

	public boolean maxesContain(int [] arr, int top){
		ArrayList<double []> coords = new ArrayList<>();
		for(int i = 0; i < probs.length; i++){
			for(int j = 0; j < probs[0].length; j++){
				double [] array = new double[3];
				array[0] = i;
				array[1] = j;
				array[2] = probs[i][j];

				coords.add(array);
			}
		}
		coords.sort(new Comparator<double []>(){

			@Override
			public int compare(double[] o1, double[] o2) {
				return (int)(-100000*((o1)[2] - (o2)[2]));
			}

		});

		for(int i = 0; i < top; i++){
			if(coords.get(i)[0] == arr[0] && coords.get(i)[1] == arr[1])
				return true;
		}

		return false;
	}

	public double getMaxVal(){
		double max = 0;
		int [] coords = new int[2];

		for(int i = 0; i < probs.length; i++){
			for(int j = 0; j < probs[0].length; j++){
				if(probs[i][j] > max){
					max = probs[i][j];
					coords[0] = j;
					coords[1] = i;
				}
			}
		}
		return max;
	}
}

class Run {
	public static void main(String [] args){
		int [][] coloring = {{1,2,3,4},{4,3,2,-1},{1,2,4,3},{4,-1,3,2}};
		Maze maze = new Maze(coloring, 4);
		maze.printMaze();
		BlindRobot bp = new BlindRobot(maze);
		bp.runPath(100, 0, 0);

		int corrCount = 0;
		int totalCount = 0;
		double max = 0;
		for(int i = 0; i < bp.sensedPath.size(); i++){
			maze.update(bp.sensedPath.get(i));
			System.out.println("Sensed color: " + bp.sensedPath.get(i));
			System.out.println("MAZE (Robot at): (" + bp.realPath.get(i)[0] + ", " + bp.realPath.get(i)[1] + ")");
			System.out.println(maze.probs[bp.realPath.get(i)[1]][bp.realPath.get(i)[0]]);

				//if(maze.getMax()[0] == bp.realPath.get(i)[0] && maze.getMax()[1] == bp.realPath.get(i)[1]){
				if(maze.maxesContain(bp.realPath.get(i), 2)){
					corrCount++;
					System.out.println("yes");
				}
				totalCount++;
			maze.printMaze();
		}

		System.out.println(corrCount + "/" + totalCount);
		System.out.println(max);
	}

}