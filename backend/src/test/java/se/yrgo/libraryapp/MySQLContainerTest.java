
package se.yrgo.libraryapp;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MySQLContainerTest {

    @Test
    void testMySQLDatabase() throws Exception {

        try (MySQLContainer<?> mysql =
                new MySQLContainer<>("mysql:8.0.36")
                    .withDatabaseName("librarytest")
                    .withUsername("test")
                    .withPassword("test")) {

            mysql.start();

            assertTrue(mysql.isRunning());

            try (Connection connection =
                    java.sql.DriverManager.getConnection(
                        mysql.getJdbcUrl(),
                        mysql.getUsername(),
                        mysql.getPassword()
                    );
                 Statement statement =
                    connection.createStatement()) {

                statement.execute(
                    "CREATE TABLE books (" +
                    "id INT PRIMARY KEY, " +
                    "title VARCHAR(100))"
                );

                statement.execute(
                    "INSERT INTO books VALUES (1, 'Java Basics')"
                );

                try (ResultSet result =
                        statement.executeQuery(
                            "SELECT title FROM books WHERE id = 1"
                        )) {

                    assertTrue(result.next());
                    assertEquals(
                        "Java Basics",
                        result.getString("title")
                    );
                }
            }
        }
    }
}
