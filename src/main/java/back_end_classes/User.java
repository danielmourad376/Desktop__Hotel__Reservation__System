package back_end_classes;
import java.time.LocalDate;

public abstract class User {
    //protected dataFields
    private String username;
    private String password;
    private LocalDate dateOfBirth;

    //Constructor
        public User(String username, String password, LocalDate dob){
            setUsername(username);
            setPassword(password);
            setDateOfBirth(dob);
        }
    //methods
    public static User login(String uname, String pass){
        User foundUser = HotelDatabaseSearch.findUserByUsername(uname);

        if (foundUser != null && foundUser.validatePassword(pass)) {
            return foundUser;
        }
        return null;
    }

    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    //getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }



    public final void setUsername(String username) {
        if (username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public final void setPassword(String password) {
        if (password == null || password.isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (password.contains(" ")) {
            throw new IllegalArgumentException("Password cannot contain spaces.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        this.password = password;
    }

    public final void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null){
            throw new IllegalArgumentException("Date of birth cannot be null.");}
        if (dateOfBirth.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Date of birth cannot be in the future.");}
        this.dateOfBirth = dateOfBirth;
    }
}
