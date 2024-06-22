import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
        public static final String DB_URL ="";
        public static final String username ="root";
        public static final String password ="";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }


        try{
            Connection connection = DriverManager.getConnection(DB_URL,username,password);
            Statement st = connection.createStatement();
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(st, sc);
                        break;
                    case 2:
                        viewReservations(st);
                        break;
                    case 3:
                        getRoomNumber(st, sc);
                        break;
                    case 4:
                        updateReservation(st, sc);
                        break;
                    case 5:
                        deleteReservation(st, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveRoom(Statement st, Scanner sc) {
        System.out.print("Enter guest name: ");
        String guestName = sc.next();
        sc.nextLine();
        System.out.print("Enter room number: ");
        int roomNumber = sc.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = sc.next();


        String query = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

        try{
            int personInsert = st.executeUpdate(query);
            if(personInsert>0){
                System.out.println("Reservation successful!");
            }
            else{
                System.out.println("Reservation failed.");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Statement st) {
        String query ="SELECT reservation_id, guest_name, room_number, contact_number, reservation_date" +
                " FROM reservation";

        try{
            ResultSet rs = st.executeQuery(query);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()){
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Statement st, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Statement st, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline character

        if (!reservationExists(st, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = sc.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = sc.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = sc.next();

        String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                "room_number = " + newRoomNumber + ", " +
                "contact_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try{
            int affectedRows = st.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }


    private static void deleteReservation(Statement st, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(st, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try{
                int affectedRows = st.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Statement st, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (ResultSet resultSet = st.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
    private static void exit() {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using our Hotel Reservation System!!!");
    }


}
