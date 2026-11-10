
package se.yrgo.libraryapp.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.jooby.Context;
import io.jooby.Cookie;
import io.jooby.StatusCode;
import se.yrgo.libraryapp.dao.RoleDao;
import se.yrgo.libraryapp.dao.SessionDao;
import se.yrgo.libraryapp.entities.Role;
import se.yrgo.libraryapp.entities.UserId;
import se.yrgo.libraryapp.entities.forms.LoginData;
import se.yrgo.libraryapp.services.UserService;

public class LoginControllerTest {

    @Test
    void correctLogin() {
        UserService userService = mock(UserService.class);
        RoleDao roleDao = mock(RoleDao.class);
        SessionDao sessionDao = mock(SessionDao.class);
        Context context = mock(Context.class);
        LoginData login = mock(LoginData.class);

        UserId userId = UserId.of(1);
        UUID sessionId = UUID.randomUUID();

        when(login.getUsername()).thenReturn("bosse");
        when(login.getPassword()).thenReturn("password123");

        when(userService.validate("bosse", "password123"))
                .thenReturn(Optional.of(userId));

        when(sessionDao.create(userId)).thenReturn(sessionId);
        when(roleDao.get(userId)).thenReturn(List.of(Role.USER));

        LoginController controller =
                new LoginController(userService, roleDao, sessionDao);

        List<Role> result = controller.login(context, null, login);

        assertThat(result).containsExactly(Role.USER);
        verify(sessionDao).create(userId);
        verify(context).setResponseCookie(any(Cookie.class));
    }

    @Test
    void incorrectLogin() {
        UserService userService = mock(UserService.class);
        RoleDao roleDao = mock(RoleDao.class);
        SessionDao sessionDao = mock(SessionDao.class);
        Context context = mock(Context.class);
        LoginData login = mock(LoginData.class);

        when(login.getUsername()).thenReturn("bosse");
        when(login.getPassword()).thenReturn("wrongpassword");

        when(userService.validate("bosse", "wrongpassword"))
                .thenReturn(Optional.empty());

        LoginController controller =
                new LoginController(userService, roleDao, sessionDao);

        List<Role> result = controller.login(context, null, login);

        assertThat(result).isEmpty();
        verify(context).setResponseCode(StatusCode.UNAUTHORIZED);
        verifyNoInteractions(roleDao);
        verifyNoInteractions(sessionDao);
    }

    @Test
    void validSession() {
        UserService userService = mock(UserService.class);
        RoleDao roleDao = mock(RoleDao.class);
        SessionDao sessionDao = mock(SessionDao.class);

        UserId userId = UserId.of(1);
        UUID sessionId = UUID.randomUUID();

        when(sessionDao.validate(sessionId)).thenReturn(userId);
        when(roleDao.get(userId)).thenReturn(List.of(Role.USER));

        LoginController controller =
                new LoginController(userService, roleDao, sessionDao);

        List<Role> result =
                controller.isLoggedIn(sessionId.toString());

        assertThat(result).containsExactly(Role.USER);
        verify(sessionDao).validate(sessionId);
    }

    @Test
    void missingSession() {
        UserService userService = mock(UserService.class);
        RoleDao roleDao = mock(RoleDao.class);
        SessionDao sessionDao = mock(SessionDao.class);

        LoginController controller =
                new LoginController(userService, roleDao, sessionDao);

        List<Role> result = controller.isLoggedIn(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(roleDao);
        verifyNoInteractions(sessionDao);
    }
}
