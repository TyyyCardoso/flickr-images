package azores.tyyy.cardoso.flickr_images.utils

object Validation {

    /**
     *
     * Não pode ter pontuação
     * Não pode ter mais de 10 caracteres
     * Não pode ter menos de 2 caracteres
     * Pode estar vazia
     *
     */

    fun searchTagValidation(text : String) : Boolean{
        return when{
            text.isEmpty() -> true
            text.contains(".") -> false
            text.length < 2 -> false
            text.length > 10 -> false
            else -> true
        }
    }
}