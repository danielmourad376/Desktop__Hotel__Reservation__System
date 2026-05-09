package back_end_classes;

import java.sql.*;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:hotel_database.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {

        String createGuests = "CREATE TABLE IF NOT EXISTS guests (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "dob TEXT, " +
                "gender TEXT, " +
                "balance REAL, " +
                "address TEXT, " +
                "room_preferences TEXT" +
                ");";

        String createStaff = "CREATE TABLE IF NOT EXISTS staff (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "dob TEXT, " +
                "role TEXT NOT NULL, " +
                "working_hours INTEGER" +
                ");";

        String createRoomTypes = "CREATE TABLE IF NOT EXISTS room_types (" +
                "type_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "base_price REAL NOT NULL, " +
                "description TEXT, " +
                "max_occupancy INTEGER" +
                ");";

        String createAmenities = "CREATE TABLE IF NOT EXISTS amenities (" +
                "amenity_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "price_per_day REAL NOT NULL, " +
                "type TEXT" +
                ");";

        String createRooms = "CREATE TABLE IF NOT EXISTS rooms (" +
                "room_number INTEGER PRIMARY KEY, " +
                "type_id TEXT NOT NULL, " +
                "is_available BOOLEAN NOT NULL, " +
                "FOREIGN KEY(type_id) REFERENCES room_types(type_id)" +
                ");";

        String createRoomAmenities = "CREATE TABLE IF NOT EXISTS room_amenities (" +
                "room_number INTEGER, " +
                "amenity_id TEXT, " +
                "PRIMARY KEY (room_number, amenity_id), " +
                "FOREIGN KEY(room_number) REFERENCES rooms(room_number), " +
                "FOREIGN KEY(amenity_id) REFERENCES amenities(amenity_id)" +
                ");";

        String createReservations = "CREATE TABLE IF NOT EXISTS reservations (" +
                "reservation_id INTEGER PRIMARY KEY, " +
                "guest_username TEXT NOT NULL, " +
                "room_number INTEGER NOT NULL, " +
                "check_in TEXT NOT NULL, " +
                "check_out TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY(guest_username) REFERENCES guests(username), " +
                "FOREIGN KEY(room_number) REFERENCES rooms(room_number)" +
                ");";

        String createInvoices = "CREATE TABLE IF NOT EXISTS invoices (" +
                "invoice_id INTEGER PRIMARY KEY, " +
                "reservation_id INTEGER NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "paid BOOLEAN NOT NULL, " +
                "issue_date TEXT NOT NULL, " +
                "payment_method TEXT, " +
                "payment_date TEXT, " +
                "FOREIGN KEY(reservation_id) REFERENCES reservations(reservation_id)" +
                ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createGuests);
            stmt.execute(createStaff);
            stmt.execute(createRoomTypes);
            stmt.execute(createAmenities);
            stmt.execute(createRooms);
            stmt.execute(createRoomAmenities);
            stmt.execute(createReservations);
            stmt.execute(createInvoices);
            System.out.println("SQLite: All database tables checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }


    public static void loadFromSQLite() {
        HotelDatabase inMemoryDB = HotelDatabase.getInstance();
        inMemoryDB.setLoading(true);

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            //Load Room Types
            ResultSet rsTypes = stmt.executeQuery("SELECT * FROM room_types");
            while (rsTypes.next()) {
                // THE FIX: Intercept the DB ID and force the counter to match before creating
                String dbId = rsTypes.getString("type_id");
                int num = Integer.parseInt(dbId.substring(3)); // "RT-1" -> 1
                RoomType.setIdCounter(num);

                inMemoryDB.addRoomType(new RoomType(
                        rsTypes.getString("name"),
                        rsTypes.getDouble("base_price"),
                        rsTypes.getString("description"),
                        rsTypes.getInt("max_occupancy")
                ));
            }

            //Load Amenities
            ResultSet rsAmenities = stmt.executeQuery("SELECT * FROM amenities");
            while (rsAmenities.next()) {
                String dbId = rsAmenities.getString("amenity_id");
                int num = Integer.parseInt(dbId.substring(1));
                Amenity.setIdCounter(num);

                inMemoryDB.addAmenity(new Amenity(
                        rsAmenities.getString("name"),
                        rsAmenities.getDouble("price_per_day"),
                        rsAmenities.getString("type")
                ));
            }

            //Load Staff & Guests
            ResultSet rsStaff = stmt.executeQuery("SELECT * FROM staff");
            while (rsStaff.next()) {
                String role = rsStaff.getString("role");
                if (role.equals("ADMIN")) {
                    inMemoryDB.addStaffMember(new Admin(rsStaff.getString("username"), rsStaff.getString("password"), LocalDate.parse(rsStaff.getString("dob")), rsStaff.getInt("working_hours")));
                } else {
                    inMemoryDB.addStaffMember(new Receptionist(rsStaff.getString("username"), rsStaff.getString("password"), LocalDate.parse(rsStaff.getString("dob")), rsStaff.getInt("working_hours")));
                }
            }

            ResultSet rsGuests = stmt.executeQuery("SELECT * FROM guests");
            while (rsGuests.next()) {
                inMemoryDB.addGuest(new Guest(
                        rsGuests.getString("username"), rsGuests.getString("password"),
                        LocalDate.parse(rsGuests.getString("dob")), Gender.valueOf(rsGuests.getString("gender")),
                        rsGuests.getDouble("balance"), rsGuests.getString("address"), rsGuests.getString("room_preferences")
                ));
            }

            //Load Rooms & Junction
            ResultSet rsRooms = stmt.executeQuery("SELECT * FROM rooms");
            while (rsRooms.next()) {
                int rNum = rsRooms.getInt("room_number");
                RoomType type = HotelDatabaseSearch.findRoomTypeById(rsRooms.getString("type_id"));

                if (type != null) {
                    Room r = new Room(rNum, type);
                    r.setAvailable(rsRooms.getBoolean("is_available"));

                    try (PreparedStatement pstmt = conn.prepareStatement("SELECT amenity_id FROM room_amenities WHERE room_number = ?")) {
                        pstmt.setInt(1, rNum);
                        ResultSet rsJunction = pstmt.executeQuery();
                        while (rsJunction.next()) {
                            Amenity a = HotelDatabaseSearch.findAmenityById(rsJunction.getString("amenity_id"));
                            if (a != null) r.addAmenity(a);
                        }
                    }
                    inMemoryDB.addRoom(r);
                }
            }

            //Load Reservations
            ResultSet rsRes = stmt.executeQuery("SELECT * FROM reservations");
            while (rsRes.next()) {
                Guest g = (Guest) HotelDatabaseSearch.findUserByUsername(rsRes.getString("guest_username"));
                Room r = HotelDatabaseSearch.findRoomByNumber(rsRes.getInt("room_number"));

                if (g != null && r != null) {
                    int dbId = rsRes.getInt("reservation_id");
                    Reservation.setIdCounter(dbId - 1);

                    Reservation res = new Reservation(g, r, LocalDate.parse(rsRes.getString("check_in")), LocalDate.parse(rsRes.getString("check_out")));
                    res.setStatus(ReservationStatus.valueOf(rsRes.getString("status")));
                    inMemoryDB.addReservation(res);
                }
            }

            //Load Amenities
            ArrayList<Amenity> loadedAmenities = inMemoryDB.getAmenities();
            int maxAmenityNum = 0;
            for (int i = 0; i < loadedAmenities.size(); i++) {
                String id = loadedAmenities.get(i).getAmenityId();
                int num = Integer.parseInt(id.substring(1));
                if (num > maxAmenityNum) maxAmenityNum = num;
            }
            Amenity.setIdCounter(maxAmenityNum + 1);

            //Load RoomTypes
            ArrayList<RoomType> loadedTypes = inMemoryDB.getRoomTypes();
            int maxTypeNum = 0;
            for (int i = 0; i < loadedTypes.size(); i++) {
                String id = loadedTypes.get(i).getTypeId();
                int num = Integer.parseInt(id.substring(3));
                if (num > maxTypeNum) maxTypeNum = num;
            }
            RoomType.setIdCounter(maxTypeNum + 1);

            //Load Reservations
            ArrayList<Reservation> loadedRes = inMemoryDB.getReservations();
            int maxResId = 1000;
            for (int i = 0; i < loadedRes.size(); i++) {
                int id = loadedRes.get(i).getReservationId();
                if (id > maxResId) maxResId = id;
            }
            Reservation.setIdCounter(maxResId);

            ArrayList<Invoice> loadedInvoices = inMemoryDB.getInvoices();
            int maxInvId = 5000;
            for (int i = 0; i < loadedInvoices.size(); i++) {
                int id = loadedInvoices.get(i).getInvoiceId();
                if (id > maxInvId) maxInvId = id;
            }
            Invoice.setIdCounter(maxInvId);

        } catch (SQLException e) {
            System.err.println("Load Error: " + e.getMessage());
        } finally {
            inMemoryDB.setLoading(false);
        }
    }

    public static void insertStaff(Staff s) {
        String sql = "INSERT INTO staff(username, password, dob, role, working_hours) VALUES(?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getUsername());
            pstmt.setString(2, s.getPassword());
            pstmt.setString(3, s.getDateOfBirth().toString());
            pstmt.setString(4, s.getRole().toString());
            pstmt.setInt(5, s.getWorkingHours());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting staff: " + e.getMessage());
        }
    }

    public static void insertGuest(Guest g) {
        String sql = "INSERT INTO guests(username, password, dob, gender, balance, address, room_preferences) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, g.getUsername());
            pstmt.setString(2, g.getPassword());
            pstmt.setString(3, g.getDateOfBirth().toString());
            pstmt.setString(4, g.getGender().toString());
            pstmt.setDouble(5, g.getBalance());
            pstmt.setString(6, g.getAddress());
            pstmt.setString(7, g.getRoomPreferences());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting guest: " + e.getMessage());
        }
    }

    public static void insertRoomType(RoomType rt) {
        String sql = "INSERT INTO room_types(type_id, name, base_price, description, max_occupancy) VALUES(?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rt.getTypeId());
            pstmt.setString(2, rt.getName());
            pstmt.setDouble(3, rt.getBasePrice());
            pstmt.setString(4, rt.getDescription());
            pstmt.setInt(5, rt.getMaxOccupancy());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting room type: " + e.getMessage());
        }
    }

    public static void insertAmenity(Amenity a) {
        String sql = "INSERT INTO amenities(amenity_id, name, price_per_day, type) VALUES(?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getAmenityId());
            pstmt.setString(2, a.getName());
            pstmt.setDouble(3, a.getPricePerDay());
            pstmt.setString(4, a.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting amenity: " + e.getMessage());
        }
    }

    public static void insertRoomAmenity(int roomNumber, String amenityId) {
        String sql = "INSERT OR IGNORE INTO room_amenities(room_number, amenity_id) VALUES(?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomNumber);
            pstmt.setString(2, amenityId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error linking amenity to room: " + e.getMessage());
        }
    }

    public static void insertRoom(Room r) {
        String sql = "INSERT INTO rooms(room_number, type_id, is_available) VALUES(?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, r.getRoomNumber());
            pstmt.setString(2, r.getRoomType().getTypeId());
            pstmt.setBoolean(3, r.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting room: " + e.getMessage());
        }
    }

    public static void insertReservation(Reservation res) {
        String sql = "INSERT INTO reservations(reservation_id, guest_username, room_number, check_in, check_out, status) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, res.getReservationId());
            pstmt.setString(2, res.getGuest().getUsername());
            pstmt.setInt(3, res.getRoom().getRoomNumber());
            pstmt.setString(4, res.getCheckInDate().toString());
            pstmt.setString(5, res.getCheckOutDate().toString());
            pstmt.setString(6, res.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting reservation: " + e.getMessage());
        }
    }

    public static void insertInvoice(Invoice inv) {
        String sql = "INSERT INTO invoices(invoice_id, reservation_id, total_amount, paid, issue_date, payment_method, payment_date) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, inv.getInvoiceId());
            pstmt.setInt(2, inv.getReservation().getReservationId());
            pstmt.setDouble(3, inv.getTotalAmount());
            pstmt.setBoolean(4, inv.isPaid());
            pstmt.setString(5, inv.getIssueDate().toString());
            pstmt.setString(6, inv.getPaymentMethod() != null ? inv.getPaymentMethod().toString() : null);
            pstmt.setString(7, inv.getPaymentDate() != null ? inv.getPaymentDate().toString() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error inserting invoice: " + e.getMessage());
        }
    }

    public static void deleteRoom(int roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        String junctionSql = "DELETE FROM room_amenities WHERE room_number = ?";
        try (Connection conn = connect()) {
            try (PreparedStatement p1 = conn.prepareStatement(junctionSql)) {
                p1.setInt(1, roomNumber); p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement(sql)) {
                p2.setInt(1, roomNumber); p2.executeUpdate();
            }
        } catch (SQLException e) { System.err.println("Delete Error: " + e.getMessage()); }
    }

    public static void deleteRoomType(String typeId) {
        String sql = "DELETE FROM room_types WHERE type_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeId); pstmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Delete Error: " + e.getMessage()); }
    }

    public static void deleteAmenity(String amenityId) {
        String sql = "DELETE FROM amenities WHERE amenity_id = ?";
        String junctionSql = "DELETE FROM room_amenities WHERE amenity_id = ?";
        try (Connection conn = connect()) {
            try (PreparedStatement p1 = conn.prepareStatement(junctionSql)) {
                p1.setString(1, amenityId); p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement(sql)) {
                p2.setString(1, amenityId); p2.executeUpdate();
            }
        } catch (SQLException e) { System.err.println("Delete Error: " + e.getMessage()); }
    }

    public static void updateReservation(Reservation res) {
        String sql = "UPDATE reservations SET guest_username = ?, room_number = ?, check_in = ?, check_out = ?, status = ? WHERE reservation_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getGuest().getUsername());
            pstmt.setInt(2, res.getRoom().getRoomNumber());
            pstmt.setString(3, res.getCheckInDate().toString());
            pstmt.setString(4, res.getCheckOutDate().toString());
            pstmt.setString(5, res.getStatus().toString());
            pstmt.setInt(6, res.getReservationId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error updating reservation: " + e.getMessage());
        }
    }

    public static void updateGuest(Guest g) {
        String sql = "UPDATE guests SET password = ?, dob = ?, gender = ?, balance = ?, address = ?, room_preferences = ? WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, g.getPassword());
            pstmt.setString(2, g.getDateOfBirth().toString());
            pstmt.setString(3, g.getGender().toString());
            pstmt.setDouble(4, g.getBalance());
            pstmt.setString(5, g.getAddress());
            pstmt.setString(6, g.getRoomPreferences());
            pstmt.setString(7, g.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error updating guest: " + e.getMessage());
        }
    }

    public static void updateStaff(Staff s) {
        String sql = "UPDATE staff SET password = ?, dob = ?, role = ?, working_hours = ? WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getPassword());
            pstmt.setString(2, s.getDateOfBirth().toString());
            pstmt.setString(3, s.getRole().toString());
            pstmt.setInt(4, s.getWorkingHours());
            pstmt.setString(5, s.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error updating staff: " + e.getMessage());
        }
    }

    public static void updateRoom(Room r) {
        String sql = "UPDATE rooms SET type_id = ?, is_available = ? WHERE room_number = ?";
        String deleteJunction = "DELETE FROM room_amenities WHERE room_number = ?";
        String insertJunction = "INSERT OR IGNORE INTO room_amenities(room_number, amenity_id) VALUES(?,?)";

        try (Connection conn = connect()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, r.getRoomType().getTypeId());
                pstmt.setBoolean(2, r.isAvailable());
                pstmt.setInt(3, r.getRoomNumber());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pDel = conn.prepareStatement(deleteJunction)) {
                pDel.setInt(1, r.getRoomNumber());
                pDel.executeUpdate();
            }

            try (PreparedStatement pIns = conn.prepareStatement(insertJunction)) {
                for (Amenity a : r.getAmenities()) {
                    pIns.setInt(1, r.getRoomNumber());
                    pIns.setString(2, a.getAmenityId());
                    pIns.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error updating room: " + e.getMessage());
        }
    }

    public static void updateRoomType(RoomType rt) {
        String sql = "UPDATE room_types SET name = ?, base_price = ?, description = ?, max_occupancy = ? WHERE type_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rt.getName());
            pstmt.setDouble(2, rt.getBasePrice());
            pstmt.setString(3, rt.getDescription());
            pstmt.setInt(4, rt.getMaxOccupancy());
            pstmt.setString(5, rt.getTypeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error updating room type: " + e.getMessage());
        }
    }

    public static void updateAmenity(Amenity a) {
        String sql = "UPDATE amenities SET name = ?, price_per_day = ?, type = ? WHERE amenity_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getName());
            pstmt.setDouble(2, a.getPricePerDay());
            pstmt.setString(3, a.getType());
            pstmt.setString(4, a.getAmenityId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error updating amenity: " + e.getMessage());
        }
    }
}