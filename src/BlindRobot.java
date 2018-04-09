package hmm;

import java.util.ArrayList;

public class BlindRobot {
	public Maze maze;
	final double errorRate = 0;
	ArrayList<Integer> sensedPath = new ArrayList<>();
	ArrayList<int []> realPath = new ArrayList<>();

	public BlindRobot(Maze maze){
		this.maze = maze;
	}

	public void runPath(int length, int x, int y){
		sensedPath = new ArrayList<>();
		realPath = new ArrayList<>();

		int [] coords = {x, y};
		realPath.add(coords);

		ArrayList<Integer> colors = new ArrayList<>();
		ArrayList<Integer> colorsPath = new ArrayList<>();
		colorsPath.add(maze.colorAt(x, y));

		for(int i = 1; i <= maze.numColors; i++){
			colors.add(i);
		}

		for(int i = 0; i < length; i++){
			ArrayList<int []> reachable = getReachable(coords[0], coords[1]);

			coords = reachable.get((int)(Math.random() * reachable.size()));
			realPath.add(coords);

			int color = maze.colorAt(coords[0], coords[1]);
			colors.remove(Integer.valueOf(maze.colorAt(coords[0], coords[1])));

			if(Math.random() < errorRate){
				colorsPath.add(colors.get((int)Math.random() * (colors.size())));
			} else {
				colorsPath.add(color);
			}
			colors.add(color);
		}

		sensedPath = colorsPath;


	}

	public ArrayList<Integer> returnSensedPath(){
		return sensedPath;
	}

	public ArrayList<int []> returnRealPath(){
		return realPath;
	}

	public ArrayList<int []> getReachable(int x, int y){
		ArrayList<int []> reachable = new ArrayList<>();
		boolean againstWall = false;

		if(isValid(x + 1, y) != -1){
			if(isValid(x + 1, y) == 0 && !againstWall){
				againstWall = true;
				reachable.add(new int [] {x, y});
			} else if (isValid(x + 1, y) == 1) {
				reachable.add(new int [] {x + 1, y});
			}
		}


		if(isValid(x - 1, y) != -1){
			if(isValid(x - 1, y) == 0 && !againstWall){
				againstWall = true;
				reachable.add(new int [] {x, y});
			} else if (isValid(x - 1, y) == 1) {
				reachable.add(new int [] {x - 1, y});
			}
		}


		if(isValid(x, y + 1) != -1){
			if(isValid(x, y + 1) == 0 && !againstWall){
				againstWall = true;
				reachable.add(new int [] {x, y});
			} else if (isValid(x, y + 1) == 1) {
				reachable.add(new int [] {x, y + 1});
			}
		}


		if(isValid(x + 1, y) != -1){
			if(isValid(x + 1, y) == 0 && !againstWall){
				againstWall = true;
				reachable.add(new int [] {x, y});
			} else if (isValid(x, y - 1) == 1) {
				reachable.add(new int [] {x, y - 1});
			}
		}

		return reachable;
	}

	public int isValid(int x, int y){
		if( x < 0 || x >= maze.coloring[0].length || y < 0 || y >= maze.coloring.length)
			return -1;
		if(maze.coloring[x][y] == -1)
			return 0;
		return 1;
	}


}
