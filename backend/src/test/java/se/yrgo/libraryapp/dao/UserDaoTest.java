
package se.yrgo.libraryapp.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

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

    @Test
    void getExistingLoginInfo() throws SQLException {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("password_hash"))
                .thenReturn("testhash");

        UserDao dao = new UserDao(ds);

        var result = dao.getLoginInfo("bosse");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId().getId()).isEqualTo(1);
        assertThat(result.get().getPasswordHash())
                .isEqualTo("testhash");
    }

    @Test
    void getNonExistingLoginInfo() throws SQLException {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDao dao = new UserDao(ds);

        assertThat(dao.getLoginInfo("unknown")).isEmpty();
    }

    @Test
    void getExistingUser() throws SQLException {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("user")).thenReturn("bosse");
        when(rs.getString("realname"))
                .thenReturn("Bosse Andersson");

        UserDao dao = new UserDao(ds);

        Optional<User> result = dao.get("1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(UserId.of(1));
        assertThat(result.get().getName()).isEqualTo("bosse");
        
    }

    @Test
    void getNonExistingUser() throws SQLException {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDao dao = new UserDao(ds);

        assertThat(dao.get("99999")).isEmpty();
    }
}
