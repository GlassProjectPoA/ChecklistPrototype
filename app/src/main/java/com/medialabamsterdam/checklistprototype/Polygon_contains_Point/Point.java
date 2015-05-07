package com.medialabamsterdam.checklistprototype.Polygon_contains_Point;

/**
 * Point on 2D landscape
 * 
 * @author Roman Kushnarenko (sromku@gmail.com)</br>
 */
public class Point
{
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public final double x;
	public final double y;

	@Override
	public String toString()
	{
		return String.format("(%.6f,%.6f)", x, y);
	}
}