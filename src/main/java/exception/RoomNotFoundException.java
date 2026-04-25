package exception;

public class RoomNotFoundException extends Exception{
    public RoomNotFoundException(int roomNumber) {
        super("Room " + roomNumber + " does not exist in the system.");
    }
}
