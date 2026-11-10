
package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@H2
public class UserDaoIntegrationTest {

    private static DataSource ds;

    @BeforeAll
    static void initDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test");
        ds = dataSource;
    }

    @Test
    void getUserById() {
        UserDao userDao = new UserDao(ds);

        UserId userId = UserId.of(1);

        Optional<User> result =
                userDao.get(Integer.toString(userId.getId()));

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test");
        assertThat(result.get().getId()).isEqualTo(userId);
    }
}
