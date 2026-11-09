package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class RealNameTest {

    @Test
    void validRealName() {
        assertThat(RealName.validate("Anna Andersson"))
                .isTrue();
    }

    @Test
    void invalidWordAtEnd() {
        assertThat(RealName.validate("Anna shiet"))
                .isFalse();
    }

    @Test
    void invalidWordAtBeginning() {
        assertThat(RealName.validate("shiet Anna"))
                .isFalse();
    }

    @Test
    void firstWordFromBadWordsList() {
        assertThat(RealName.validate("Anna balderdash"))
                .isFalse();
    }
}
