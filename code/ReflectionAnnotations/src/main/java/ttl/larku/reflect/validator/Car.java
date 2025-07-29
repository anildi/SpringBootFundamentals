package ttl.larku.reflect.validator;

public class Car
{
	@MyNotNull
    @MyLength(min = 4)
    private String modelName;

	@MyStockNumber
	private String stockNumber;

    private int numWheels;
    
    private int numDoors;

    public String getModelName ()
    {
        return modelName;
    }

    public void setModelName (String modelName)
    {
        this.modelName = modelName;
    }

    public int getNumWheels ()
    {
        return numWheels;
    }

    public void setNumWheels (int numWheels)
    {
        this.numWheels = numWheels;
    }

    public int getNumDoors ()
    {
        return numDoors;
    }

    public void setNumDoors (int numDoors)
    {
        this.numDoors = numDoors;
    }

    public String getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}

	@Override
	public String toString() {
		return "Car [modelName=" + modelName + ", stockNumber=" + stockNumber + ", numWheels=" + numWheels
				+ ", numDoors=" + numDoors + "]";
	}
    
    
}
