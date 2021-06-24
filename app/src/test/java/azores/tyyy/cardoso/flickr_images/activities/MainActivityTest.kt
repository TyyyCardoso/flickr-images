package azores.tyyy.cardoso.flickr_images.activities

import azores.tyyy.cardoso.flickr_images.utils.Validation
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class MainActivityTest {


    @Test
    fun `Tag with punctuation returns false`() {
        var testVariable = Validation.searchTagValidation("Tiago.")
        assertThat(testVariable).isFalse()
    }
    @Test
    fun `search tag com mais de 10 caracteres`() {
        var testVariable = Validation.searchTagValidation("TiagoCardoso")
        assertThat(testVariable).isFalse()
    }

    @Test
    fun `search tag com menos de 2 caracteres`() {

        var testVariable = Validation.searchTagValidation("T")
        assertThat(testVariable).isFalse()
    }

    @Test
    fun `search tag correta`() {
        var testVariable = Validation.searchTagValidation("Tiago")
        assertThat(testVariable).isTrue()
    }

    @Test
    fun `search tag está vazia`(){
        var testVariable = Validation.searchTagValidation("")
        assertThat(testVariable).isTrue()
    }

}