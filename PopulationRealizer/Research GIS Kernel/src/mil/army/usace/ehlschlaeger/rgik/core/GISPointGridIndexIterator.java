package mil.army.usace.ehlschlaeger.rgik.core;


public class GISPointGridIndexIterator<T extends GISPoint> {
	
	private int							gridToDo, pointToDo;
	private GISPointGridIndex<T>		gridIndex;
	private int[] pointsInGrid;
	private T[] toDoLisTs;
	
	
	public GISPointGridIndexIterator(GISPointGridIndex<T> gi) {
		this.gridIndex = gi;
		this.gridToDo = 0;
        this.pointToDo = 0;
        this.pointsInGrid = gi.getNumberOfPointsInGrid();
    }
	
	 /**
     * Returns next point in order, or null if done.
     * @return next point or null if done
     */
    public T next() {
    	while(this.pointToDo >= pointsInGrid[this.gridToDo])
    	{
    		this.pointToDo = 0;
    		this.gridToDo ++;
    		
    		if(gridToDo >= this.pointsInGrid.length)
    		{
    			return null;
    		}
    	}
    	
    	if(this.pointToDo == 0)
    	{
    		this.toDoLisTs = this.gridIndex.getPointsInGrid(this.gridToDo);
    	}
    	
    	T temp = this.toDoLisTs[this.pointToDo];
    	this.pointToDo ++;
    	
    	return temp;
        
    }


}
