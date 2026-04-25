package exception;

public class RoomNotAvailableException extends Exception{
    public RoomNotAvailableException(int roomNumber) {
        super("Room " + roomNumber + " is not available for reservation.");
    }
}

