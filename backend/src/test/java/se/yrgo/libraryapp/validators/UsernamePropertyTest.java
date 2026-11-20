
package se.yrgo.libraryapp.validators;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsernamePropertyTest {

    @Property(tries = 100)
    void validUsernamesAreAccepted(
            @ForAll @AlphaChars @StringLength(min = 4, max = 20)
            String username) {

        assertTrue(Username.validate(username));
    }

    @Property(tries = 100)
    void shortUsernamesAreRejected(
            @ForAll @AlphaChars @StringLength(min = 0, max = 3)
            String username) {

        assertFalse(Username.validate(username));
    }
}
