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

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
    		
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {

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