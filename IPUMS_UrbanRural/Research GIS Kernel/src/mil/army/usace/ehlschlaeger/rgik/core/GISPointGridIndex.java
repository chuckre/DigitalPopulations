package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class GISPointGridIndex<T extends GISPoint> extends GISData implements Iterable<T>, Serializable {
	
	protected double gridSize;
	
	protected int gridXDim;
	protected int gridYDim;
	
	int minPoints = 10;
	
	protected int pointsInGrid[];
	
	protected T indexedPoints [][];
	
	
	@SuppressWarnings("unchecked")
	public GISPointGridIndex(double west, double north, double east, double south, double indexGridSize, int minPointsInGrid)
	{
		super(west, north, east, south);
		if(minPointsInGrid < 1)
			this.minPoints = 1;
		else
			this.minPoints = minPointsInGrid;
        
        this.gridSize = indexGridSize;
        
        this.gridXDim = (int)Math.ceil((east - west) / this.gridSize);
        this.gridYDim = (int)Math.ceil((north - south) / this.gridSize);
        
        this.pointsInGrid = new int[this.gridXDim * this.gridYDim];
        this.indexedPoints = (T[][]) new GISPoint[this.gridXDim * this.gridYDim][];
        
        for(int i = 0; i < this.gridXDim * this.gridYDim; i++)
        {
        	this.pointsInGrid[i] = 0;
        	this.indexedPoints[i] = (T[]) new GISPoint[minPoints];
        }
        
//        System.out.println("gridXDim: " + gridXDim + "\tgridYDim " + gridYDim);
//        System.out.println("gridSize: " + gridSize);
//        System.out.println("MinPoints: " + minPoints);
	}
	
	public GISPointGridIndex (GISData region, double indexGridSize, int minPointsInGrid)
	{
		this(region.getWestEdge(), region.getNorthEdge(), region.getEastEdge(), region.getSouthEdge(), indexGridSize, minPointsInGrid);
	}
	
	public void addPoint(T point)
	{	
		int gridX = (int)((point.getEasting() - this.getWestEdge())/this.gridSize);
		int gridY = (int)((point.getNorthing() - this.getSouthEdge())/this.gridSize);
		
		if(gridX < 0 || gridX >= this.gridXDim || gridY < 0 || gridY >= this.gridYDim)
		{
			System.out.println("("+gridX+","+gridY+"+) is out of bound");
			return;
		}
		
		int gridID = gridY * this.gridXDim + gridX;
		
		//Allocated memory when is full
		if(this.indexedPoints[gridID].length == this.pointsInGrid[gridID])
		{			
			@SuppressWarnings("unchecked")
			T[] temp = (T[]) new GISPoint[this.pointsInGrid[gridID] * 2];
			for(int i = 0; i < this.pointsInGrid[gridID]; i++)
			{
				temp[i] = this.indexedPoints[gridID][i];
			}
			this.indexedPoints[gridID] = temp;
			
			this.indexedPoints[gridID][this.pointsInGrid[gridID]] = point;
			this.pointsInGrid[gridID] ++;
			
		}
		else 
		{
			this.indexedPoints[gridID][this.pointsInGrid[gridID]] = point;
			this.pointsInGrid[gridID] ++;
		}
	}
	
	public int getNumberPoints()
	{
		int numberPoints = 0;
		
		for(int i = 0; i < this.gridXDim * this.gridYDim; i++)
		{
			numberPoints += this.pointsInGrid[i];
		}	
		
		return numberPoints;
	}
	
	public LinkedList<T> getPoints( GISPoint goal, double distance)
	{
		LinkedList<T> list = new LinkedList<T>();
		int gridDistance = (int)Math.ceil(distance/this.gridSize);
		double goalX = goal.getEasting();
		double goalY = goal.getNorthing();
		
		int goalGridX = (int)((goalX - this.getWestEdge())/this.gridSize);
		int goalGridY = (int)((goalY - this.getSouthEdge())/this.gridSize);
		
		T pointToCheckT;
		
		for(int i = goalGridY - gridDistance; i <= goalGridY + gridDistance; i++)
		{
			if(i < 0)
			{
				i = 0;
			}
			else if(i >= this.gridYDim)
			{
				break;
			}
			
			for(int j = goalGridX - gridDistance; j <= goalGridX + gridDistance; j++)
			{
				if(j < 0)
				{
					j = 0;
				}
				else if(j >= this.gridXDim)
				{
					break;
				}
				
				int gridID = i * this.gridXDim + j;
				for(int k = 0; k < this.pointsInGrid[gridID]; k++)
				{
					pointToCheckT = this.indexedPoints[gridID][k];
					if((pointToCheckT.getEasting() - goalX) * (pointToCheckT.getEasting() - goalX) + (pointToCheckT.getNorthing() - goalY) * (pointToCheckT.getNorthing() - goalY) <= distance * distance)
					{
						list.add(this.indexedPoints[gridID][k]);
					}
				}
				
			}
		}
		
		return list;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			GISPointGridIndexIterator<T> bfi = new GISPointGridIndexIterator(GISPointGridIndex.this);
			T prev = null;
            T next = (T) bfi.next();
            
            public boolean hasNext() {
                return next != null;
            }
            
            public T next() {
                if(next == null)
                    throw new NoSuchElementException();
                else {
                    prev = next;
                    next = (T) bfi.next();
                    return prev;
                }
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
		};
	}
	
	public void swapPoints(T point1, T point2)
	{
		boolean changed = false;
		
		double x1 = point1.getEasting();
		double y1 = point1.getNorthing();
		
		double x2 = point2.getEasting();
		double y2 = point2.getNorthing();
		
		int gridID = (int)((x1 - this.getWestEdge())/this.gridSize) + this.gridXDim * (int)((y1 - this.getSouthEdge())/this.gridSize);
		
		for(int i = 0; i < this.pointsInGrid[gridID]; i++)
		{
			if(this.indexedPoints[gridID][i] == point1)
			{
				this.indexedPoints[gridID][i] = point2;
				changed = true;
				break;
			}
		}
		
		if(!changed)
		{
			throw new DataException("Can't find target household in the indexed points");
		}
		
		
		
		changed = false;
		gridID = (int)((x2 - this.getWestEdge())/this.gridSize) + this.gridXDim * (int)((y2 - this.getSouthEdge())/this.gridSize);
		for(int i = 0; i < this.pointsInGrid[gridID]; i++)
		{
			if(this.indexedPoints[gridID][i] == point2)
			{
				this.indexedPoints[gridID][i] = point1;
				changed = true;
				break;
			}
		}
		
		if(!changed)
		{
			throw new DataException("Can't find target household in the indexed points");
		}
		
		
		point1.setEasting(x2);
		point1.setNorthing(y2);
		point2.setEasting(x1);
		point2.setNorthing(y1);
	}
	
	public int[] getNumberOfPointsInGrid()
	{
		return this.pointsInGrid;
	}
	
	public T[] getPointsInGrid(int gridID)
	{
		if(gridID >= this.gridXDim * this.gridYDim)
		{
			return null;
		}
		else 
		{
			return this.indexedPoints[gridID];
		}
	}
	

}
