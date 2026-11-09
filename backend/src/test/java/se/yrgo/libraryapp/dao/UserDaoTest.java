
package se.yrgo.libraryapp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDaoTest {

    @Test
    void registerReturnsFalseWhenDatabaseFails() throws SQLException {
        DataSource ds = mock(DataSource.class);

        when(ds.getConnection())
                .thenThrow(new SQLException("Database unavailable"));

        UserDao dao = new UserDao(ds);

        boolean result = dao.register(
                "bosse",
                "Bosse Andersson",
                "password123"
        );

        assertThat(result).isFalse();

        verify(ds).getConnection();
    }

    @Test
    void registerRollsBackWhenInsertFails() throws SQLException {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);

        when(stmt.executeUpdate(
                anyString(),
                eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("Insert failed"));

        UserDao dao = new UserDao(ds);

        boolean result = dao.register(
                "bosse",
                "Bosse Andersson",
                "password123"
        );

        assertThat(result).isFalse();
        verify(conn).rollback();
    }
}
