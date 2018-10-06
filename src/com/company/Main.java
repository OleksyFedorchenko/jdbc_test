package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Random;

public class Main {
    static Connection conn;

    public static void main(String[] args) throws SQLException, IOException {
        String url = "jdbc:mysql://localhost:3306/jdbc_test?useSSL=false";
        String user = "root";
        String pass = "root";

        conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Connection? " + !conn.isClosed());

        createTablePerson();
        createTableCity();
        createTableCountry();
        System.out.println();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        do {
            System.out.println("1-Add country \n"
                    + "2-Add city\n"
                    + "3-Add person\n"
                    + "4-Show list of person\n"
                    + "5-Show list of cities\n"
                    + "6-Show list of countries\n"
                    + "7-Show person information by id\n"
                    + "8-Show city information by id\n"
                    + "9-Show country information by id\n"
                    + "10-Show info about people from one city\n"
                    + "11-Show info about cities which are from one country\n"
                    + "12-Find person\n"
                    + "13-Random adding tables\n"
                    + "14-Exit");


            input = br.readLine();
            switch (input) {
                case "1":
                    System.out.println("Input country name:");
                    String cn = br.readLine();
                    insertCountry(cn);
                    break;
                case "2":
                    System.out.println("Input city name:");
                    String citn = br.readLine();
                    System.out.println("Input id of country:");
                    int idc = Integer.parseInt(br.readLine());
                    insertCity(citn, idc);
                    break;
                case "3":
                    System.out.println("Input first name:");
                    String fn = br.readLine();
                    System.out.println("Input last name:");
                    String ln = br.readLine();
                    System.out.println("Input your age:");
                    int a = Integer.parseInt(br.readLine());
                    System.out.println("Where are you from (city_id)?:");
                    int c = Integer.parseInt(br.readLine());
                    insertPerson(fn, ln, a, c);
                    break;
                case "4":
                    selectPerson("ORDER BY first_name");
                    break;
                case "5":
                    selectCity("ORDER BY name DESC");
                    break;
                case "6":
                    selectCountry("ORDER BY id");
                    break;
                case "7":
                    System.out.println("Input id of person:");
                    int id = Integer.parseInt(br.readLine());
                    selectPerson("WHERE id LIKE " + id);
                    break;
                case "8":
                    System.out.println("Input id of city:");
                    id = Integer.parseInt(br.readLine());
                    selectCity("WHERE id LIKE " + id);
                    break;
                case "9":
                    System.out.println("Input id of country:");
                    id = Integer.parseInt(br.readLine());
                    selectCountry("WHERE id LIKE " + id);
                    break;
                case "10":
                    System.out.println("Input name of city to see all from this place");
                    String in = br.readLine();
                    int idct = idOfCity("WHERE name = '" + in + "'");
                    selectPersonFromOneCity(idct);
                    break;
                case "11":
                    System.out.println("Input name of country to see all cities from it");
                    String inc = br.readLine();
                    int idctc = idOfCountry("WHERE name = '" + inc + "'");
                    cityOfCountry(idctc);
                    break;
                case "12":
                    System.out.println("Input some letters of person name:");
                    String match = br.readLine();
                    selectPerson("WHERE first_name LIKE '%" + match + "%'");
                    break;
                case "13":
                    fileReadRandom();
                    selectCountry("ORDER BY id");
                    selectCity("ORDER BY id");
                    selectPerson("ORDER BY id");
                    break;
            }
        } while (!input.equals("14"));

        conn.close();
    }

    public static void createTablePerson() throws SQLException {
        String dropQuery = "DROP TABLE IF EXISTS person;";
        String query = "CREATE TABLE person(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "first_name VARCHAR(80) NOT NULL, " +
                "last_name VARCHAR(80) NOT NULL, " +
                "age INT NOT NULL, " +
                "city_id INT NOT NULL)";

        Statement stmt = conn.createStatement();
        stmt.execute(dropQuery);
        stmt.execute(query);
        System.out.println("Table 'person' created!");
        stmt.close();
    }

    public static void createTableCity() throws SQLException {
        String dropQuery = "DROP TABLE IF EXISTS city;";
        String query = "CREATE TABLE city(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(80) NOT NULL, country_id INT NOT NULL)";

        Statement stmt = conn.createStatement();
        stmt.execute(dropQuery);
        stmt.execute(query);
        System.out.println("Table 'city' created!");
        stmt.close();
    }

    public static void createTableCountry() throws SQLException {
        String dropQuery = "DROP TABLE IF EXISTS country;";
        String query = "CREATE TABLE country(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(80) NOT NULL)";

        Statement stmt = conn.createStatement();
        stmt.execute(dropQuery);
        stmt.execute(query);
        System.out.println("Table 'country' created!");
        stmt.close();
    }

    public static void selectCountry(String order) throws SQLException {
        String query = "SELECT * FROM country " + order;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + "\t | " +
                    "Country name: " + rs.getString("name"));
        }
    }

    public static void selectPerson(String order) throws SQLException {
        String query = "SELECT * FROM person " + order;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + "\t | " +
                    "First name: " + rs.getString("first_name") + "\t | " +
                    "Last name: " + rs.getString("last_name") + "\t | " +
                    "Age: " + rs.getInt("age") + "\t | " +
                    "City_ID: " + rs.getInt("city_id"));
        }
    }

    public static void insertPerson(String fn, String ln, int a, int c) throws SQLException {
        String query = "INSERT INTO person(first_name, last_name, age, city_id) VALUES(?, ?, ?, ?);";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, fn);
        pstmt.setString(2, ln);
        pstmt.setInt(3, a);
        pstmt.setInt(4, c);

        pstmt.executeUpdate();
        System.out.println("Add is ok");
        pstmt.close();
    }

    public static void insertCountry(String cn) throws SQLException {
        String query = "INSERT INTO country(name) VALUES(?);";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, cn);

        pstmt.executeUpdate();
        System.out.println("Add is ok");
        pstmt.close();
    }

    public static void selectCity(String order) throws SQLException {
        String query = "SELECT * FROM city " + order;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + "\t | " +
                    "City name: " + rs.getString("name") + "\t | " +
                    "Country_ID: " + rs.getInt("country_id"));
        }
    }

    public static void insertCity(String citn, int idc) throws SQLException {
        String query = "INSERT INTO city(name,country_id) VALUES(?,?);";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, citn);
        pstmt.setInt(2, idc);

        pstmt.executeUpdate();
        System.out.println("Add is ok");
        pstmt.close();
    }

    public static void fileReadRandom() throws IOException, SQLException {

        List<String> lines = Files.readAllLines(Paths.get("countries.txt"));
        for (String line : lines) {
            insertCountry(line);
        }

        Random rand = new Random();
        lines = Files.readAllLines(Paths.get("cityes.txt"));
        for (String line : lines) {
            int cn = 1 + rand.nextInt(count("country") - 1);
            insertCity(line, cn);
        }


        FileReader fr = new FileReader("names.txt");
        BufferedReader br = new BufferedReader(fr);

        String line;
        String strs[];
        while ((line = br.readLine()) != null) {
            strs = line.split(" ");
            for (int i = 0; i < strs.length; i += 2) {
                insertPerson(strs[i], strs[i + 1], 1 + rand.nextInt(89), 1 + rand.nextInt(count("city") - 1));
            }
        }
        br.close();
        fr.close();
    }

    public static void selectPersonFromOneCity(int idct) throws SQLException {
        String query = "SELECT person.first_name,person.last_name,person.age,city.name FROM person,city WHERE (person.city_id LIKE " + idct + ") AND (city.id LIKE " + idct + ")";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("First name: " + rs.getString("person.first_name") + "\t | " +
                    "Last name: " + rs.getString("person.last_name") + "\t | " +
                    "Age: " + rs.getInt("person.age") + "\t | " +
                    "City_Name: " + rs.getString("city.name"));
        }
    }

    public static int count(String table) throws SQLException {
        String query = "SELECT COUNT(*) AS row_count FROM " + table;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int a = rs.getInt("row_count");
        return a;
    }

    public static int idOfCity(String order) throws SQLException {
        String query = "SELECT id FROM city " + order;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int a = rs.getInt("id");
        return a;
    }

    public static int idOfCountry(String order) throws SQLException {
        String query = "SELECT id FROM country " + order;
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int a = rs.getInt("id");
        return a;
    }

    public static void cityOfCountry(int idctc) throws SQLException {
        String query = "SELECT country.name,city.name FROM country,city WHERE (country.id LIKE " + idctc + ") AND (city.country_id LIKE " + idctc + ")";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("Country name: " + rs.getString("country.name") + "\t | " +
                    "City_Name: " + rs.getString("city.name"));
        }
    }

}
