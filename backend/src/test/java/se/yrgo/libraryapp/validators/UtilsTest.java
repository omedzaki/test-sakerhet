
package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    @Test
    void removesNumbersAndSpecialCharacters() {
        String result = Utils.onlyLettersAndWhitespace("Anna123!");
        assertThat(result).isEqualTo("anna");
    }

    @Test
    void keepsSpacesAndConvertsToLowercase() {
        String result = Utils.onlyLettersAndWhitespace("Anna Andersson");
        assertThat(result).isEqualTo("anna andersson");
    }

    @Test
    void convertsLeetSpeakToLetters() {
        String result = Utils.cleanAndUnLeet("B0SS3");
        assertThat(result).isEqualTo("bosse");
    }

    @Test
    void cleansLeetSpeakAndSpecialCharacters() {
        String result = Utils.cleanAndUnLeet("H3LL0, W0RLD!");
        assertThat(result).isEqualTo("hello world");
    }

    @Test
    void handlesEmptyStrings() {
        assertThat(Utils.onlyLettersAndWhitespace("")).isEmpty();
        assertThat(Utils.cleanAndUnLeet("")).isEmpty();
    }
}
