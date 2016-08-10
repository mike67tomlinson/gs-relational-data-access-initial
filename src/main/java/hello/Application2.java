package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class Application2 implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application2.class);

    public static void main(String args[]) {
    	    SpringApplication.run(Application2.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {

    	System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"letmein");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}

    	
    		log.info("Creating tables");

    		
    		
        jdbcTemplate.execute("DROP TABLE cars IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE cars(" +
                "id SERIAL, make VARCHAR(255), model VARCHAR(255), engine INT(4))");

        // Split up the array of whole names into an array of make/model/engine size
        List<Object[]> splitUpNames = Arrays.asList("Ford Fiesta 1100", "Ford Focus 1600", "VW Golf 1700", "BMW 318 1800", "BMW 520 2000").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting car record for %s %s %s", name[0], name[1], name[2])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO cars(make, model, engine) VALUES (?,?,?)", splitUpNames);

        log.info("Querying for cars records where make = 'Ford':");
        jdbcTemplate.query(
                "SELECT id, make, model, engine FROM cars WHERE make = ?", new Object[] { "Ford" },
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("make"), rs.getString("model"), rs.getInt("engine"))
        ).forEach(car -> log.info(car.toString()));
    }
}