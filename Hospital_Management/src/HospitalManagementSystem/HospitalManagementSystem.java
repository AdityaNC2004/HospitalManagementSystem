package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "8329854189";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patients ");
                System.out.println("2. View patients ");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. View Appointment");
                System.out.println("6. Exit ");
                System.out.println("Enter your choice");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
//                        Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;

                    case 4:
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        viewAppointment(connection);
                        System.out.println();
                        break;

                    case 6:
                        return;

                    default:
                        System.out.println("Enter correct Choice!!");

                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){

        System.out.print("Enter patient Id: ");
        int pid = scanner.nextInt();

        System.out.print("Enter Doctor Id: ");
        int did = scanner.nextInt();

        System.out.print("Enter appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(pid) && doctor.getDoctorById(did)){
            if(checkDoctorAvailability(did,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try {

                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,pid);
                    preparedStatement.setInt(2,did);
                    preparedStatement.setString(3,appointmentDate);

                    int rowAffected = preparedStatement.executeUpdate();
                    if (rowAffected>0){
                        System.out.println("Appointment Booked!!");
                    }else {
                        System.out.println("Failed to book Appointment");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on this date!!");
            }
        }else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }



    }

    public static void viewAppointment(Connection connection) {
        String query = "select * from appointments";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("+------------+-----------+------------------+");
            System.out.println("| patient_id | doctor_id | appointment_date |");
            System.out.println("+------------+-----------+------------------+");
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                int doctorId = resultSet.getInt("doctor_id");
                String appointmentDate = resultSet.getString("appointment_date");
                System.out.printf("| %-10s | %-9s | %-16s |\n",patientId,doctorId,appointmentDate);
                System.out.println("+------------+-----------+------------------+");
            }
        }catch(SQLException e){
                e.printStackTrace();
            }

    }

    public static boolean checkDoctorAvailability(int did,String appointmentDate,Connection connection){
        String query = "select COUNT(*) FROM appointments WHERE doctor_id = ? AND  appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,did);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count==0){
                    return true;
                }else {
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }return false;

    }
}
