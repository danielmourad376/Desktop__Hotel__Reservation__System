package back_end_classes;

public class RoomType {
    //private data fields
    private final String typeId;
    private String name;
    private double basePrice;
    private String description;
    private int maxOccupancy;
    private static int idCounter = 1;

    //constructor
    public RoomType(String name, double price,String description , int maxOccupancy){
        this.typeId = "RT-" + idCounter;
        idCounter++;
        setName(name);
        setBasePrice(price);
        setDescription(description);
        setMaxOccupancy(maxOccupancy);
    }

    //getters and setters
    public String getTypeId() { return typeId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Room type name cannot be empty.");
        this.name = name.trim();
    }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) {
        if (basePrice < 0)
            throw new IllegalArgumentException("Base price cannot be negative.");
        this.basePrice = basePrice;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Room type description cannot be empty.");
        this.description = description.trim();
    }

    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int maxOccupancy) {
        if (maxOccupancy <= 0)
            throw new IllegalArgumentException("Max occupancy must be at least 1.");
        this.maxOccupancy = maxOccupancy;
    }
}
