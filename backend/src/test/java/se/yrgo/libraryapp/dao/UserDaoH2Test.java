
package se.yrgo.libraryapp.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

public class UserDaoH2Test {

    @Test
    void registerUserWithH2() throws Exception {

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        // Create the tables needed for the test
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                "CREATE TABLE user (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user VARCHAR(70), " +
                "password_hash VARCHAR(200), " +
                "realname VARCHAR(100))"
            );

            stmt.execute(
                "CREATE TABLE role (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "role VARCHAR(15) UNIQUE)"
            );

            stmt.execute(
                "CREATE TABLE user_role (" +
                "user_id INT, " +
                "role_id INT, " +
                "PRIMARY KEY (user_id, role_id), " +
                "FOREIGN KEY (user_id) REFERENCES user(id), " +
                "FOREIGN KEY (role_id) REFERENCES role(id))"
            );

            stmt.execute(
                "INSERT INTO role VALUES " +
                "(1, 'admin'), (2, 'user')"
            );
        }

        UserDao dao = new UserDao(ds);

        boolean result = dao.register(
            "bosse",
            "Bosse Andersson",
            "hashedPassword123"
        );

        assertThat(result).isTrue();

        // Check the saved user and role
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT u.user, u.realname, " +
                 "u.password_hash, ur.role_id " +
                 "FROM user u " +
                 "JOIN user_role ur ON u.id = ur.user_id " +
                 "WHERE u.user = 'bosse'"
             )) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("user"))
                .isEqualTo("bosse");
            assertThat(rs.getString("realname"))
                .isEqualTo("Bosse Andersson");
            assertThat(rs.getString("password_hash"))
                .isEqualTo("hashedPassword123");
            assertThat(rs.getInt("role_id"))
                .isEqualTo(2);
        }
    }
}
