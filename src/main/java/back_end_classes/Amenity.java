package back_end_classes;

public class Amenity {
    //private data fields
    private final String amenityId;
    private String name;
    private double pricePerDay;
    private String type;
    private static int idCounter = 1;

    //constructor
    public Amenity(String name, double price, String type) {
        setName(name);
        setPricePerDay(price);
        this.amenityId = "A" + idCounter++;
        setType(type);
    }

    //getters and setters
    public String getAmenityId() {return amenityId;}

    public String getName(){ return name;}
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Amenity name cannot be empty.");
        this.name = name.trim();
    }

    public double getPricePerDay() {return pricePerDay;}
    public void setPricePerDay(double price) {
        if (price < 0)
            throw new IllegalArgumentException("Amenity price cannot be negative.");
        this.pricePerDay = price;
    }
    public String getType(){return type;}
    public void setType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new IllegalArgumentException("Amenity type cannot be empty.");
        this.type = type.trim();
    }
}
