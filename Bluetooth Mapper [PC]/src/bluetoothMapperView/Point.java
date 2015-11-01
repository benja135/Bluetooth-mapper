/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

/**
 * Type point
 * 
 */
public class Point {
	
	public int x;
	public int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Point point) {	
		return (this.x == point.x) && (this.y == point.y);
	}
	
	public String toString() {	
		return this.x + ":" + this.y;
	}
	
}