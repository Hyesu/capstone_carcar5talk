package com.carcar5talk.compute;

public class StraightLineEquation {
	public double intercept_y;
	public double gradient;

	public StraightLineEquation(double intercept_y, double gradient) {
		this.intercept_y = intercept_y;
		this.gradient = gradient;
	}
	
	public StraightLineEquation(double[] position, Vector vector) {
		if(vector.x == 0) {
			// 여기는 세로로 직선인 그래프 모양이겠지?
			
		}
		else if(vector.y == 0) {
			// 여기는 가로로 직선인 그래프 모양이겠지?
			intercept_y = position[1];
			gradient = 0;
		}
		else {
			this.gradient = vector.y / vector.x;
			this.intercept_y = position[1] - (this.gradient * position[0]);
		}
	}
	
	public StraightLineEquation getPerpendicularStraightLineEquation(double[] position) {
		double intercept_y;
		double gradient; 
		
		gradient = (double) -1 / this.gradient;
		intercept_y = position[1] - (gradient * position[0]);
		
		return new StraightLineEquation(intercept_y, gradient);
	}
	
	public int compareToStraightLine(double[] position) {
		double rValue = this.gradient * position[0] + this.intercept_y;
		
		if(position[1] > rValue)
			return 1;
		else if(position[1] < rValue)
			return -1;
		else 
			return 0;
	}
}
