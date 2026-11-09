
package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class UsernameTest {

    @Test
    void correctUsername() {
        assertTrue(Username.validate("bosse"));
    }

    @Test
    void incorrectUsername() {
        assertFalse(Username.validate("name with space"));
    }

    @Test
    void usernameWithNumbers() {
        assertTrue(Username.validate("bosse123"));
    }

    @Test
    void usernameTooShort() {
        assertFalse(Username.validate("abc"));
    }

    @Test
    void usernameMinimumLength() {
        assertTrue(Username.validate("abcd"));
    }

    @Test
    void usernameWithSpecialCharacters() {
        assertFalse(Username.validate("bosse!"));
    }

    @Test
    void usernameWithAssertJ() {
        assertThat(Username.validate("Bosse_9")).isTrue();
    }
    @Test
void usernameEmpty() {
    assertFalse(Username.validate(""));
}
}
