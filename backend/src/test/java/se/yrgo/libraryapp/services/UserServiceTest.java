
package se.yrgo.libraryapp.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.UserId;

public class UserServiceTest {

    @Test
    @SuppressWarnings("deprecation")
    void correctLogin() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();

        UserId id = UserId.of(1);
        LoginInfo info = new LoginInfo(id, "password123");

        when(dao.getLoginInfo("bosse"))
                .thenReturn(Optional.of(info));

        UserService service = new UserService(dao, encoder);

        assertThat(service.validate("bosse", "password123"))
                .contains(id);
    }

    @Test
    @SuppressWarnings("deprecation")
    void incorrectPassword() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();

        UserId id = UserId.of(1);
        LoginInfo info = new LoginInfo(id, "password123");

        when(dao.getLoginInfo("bosse"))
                .thenReturn(Optional.of(info));

        UserService service = new UserService(dao, encoder);

        assertThat(service.validate("bosse", "wrongpassword"))
                .isEmpty();
    }

    @Test
    @SuppressWarnings("deprecation")
    void userDoesNotExist() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();

        when(dao.getLoginInfo("unknown"))
                .thenReturn(Optional.empty());

        UserService service = new UserService(dao, encoder);

        assertThat(service.validate("unknown", "password123"))
                .isEmpty();
    }

    @Test
    void registerValidUser() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(encoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(dao.register(
                "bosse123",
                "Bosse Andersson",
                "hashedPassword"))
                .thenReturn(true);

        UserService service = new UserService(dao, encoder);

        assertThat(service.register(
                "bosse123",
                "Bosse Andersson",
                "password123")).isTrue();

        verify(encoder).encode("password123");
        verify(dao).register(
                "bosse123",
                "Bosse Andersson",
                "hashedPassword");
    }

    @Test
    void registerInvalidUsername() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        UserService service = new UserService(dao, encoder);

        assertThat(service.register(
                "abc",
                "Bosse Andersson",
                "password123")).isFalse();

        verifyNoInteractions(dao, encoder);
    }

    @Test
    void registerInvalidRealName() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        UserService service = new UserService(dao, encoder);

        assertThat(service.register(
                "bosse123",
                "Bosse shiet",
                "password123")).isFalse();

        verifyNoInteractions(dao, encoder);
    }

    @Test
    void checkUsernameAvailability() {
        UserDao dao = mock(UserDao.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(dao.isNameAvailable("bosse123"))
                .thenReturn(true);

        UserService service = new UserService(dao, encoder);

        assertThat(service.isNameAvailable("bosse123"))
                .isTrue();

        verify(dao).isNameAvailable("bosse123");
    }
}
