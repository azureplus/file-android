package com.mercandalli.sdk.files.api.online

data class FileOnlineAuthentication(
        val login: String,
        val passwordSha1: String
) {

    fun createToken() = FileOnlineTokenCreator.createToken(login, passwordSha1)

    fun createTokens() = FileOnlineTokenCreator.createTokens(login, passwordSha1, 8)

    companion object {

        fun isLogged(token: String, fileOnlineAuthentications: List<FileOnlineAuthentication>): Boolean {
            for (fileOnlineAuthentication in fileOnlineAuthentications) {
                val logged = isLoggedWithTokens(token, fileOnlineAuthentication)
                if (logged) {
                    return true
                }
            }
            return false
        }

        @Suppress("unused")
        private fun isLoggedWithToken(token: String, fileOnlineAuthentication: FileOnlineAuthentication) =
                fileOnlineAuthentication.createToken() == token

        private fun isLoggedWithTokens(token: String, fileOnlineAuthentication: FileOnlineAuthentication) =
                fileOnlineAuthentication.createTokens().contains(token)
    }
}