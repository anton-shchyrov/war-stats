package org.shchyrov.telegram

import it.tdlight.client.APIToken
import it.tdlight.client.AuthenticationData
import it.tdlight.client.SimpleTelegramClient
import it.tdlight.client.TDLibSettings
import it.tdlight.common.Init
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.AuthorizationStateClosed
import it.tdlight.jni.TdApi.AuthorizationStateClosing
import it.tdlight.jni.TdApi.AuthorizationStateLoggingOut
import it.tdlight.jni.TdApi.AuthorizationStateReady
import it.tdlight.jni.TdApi.CheckAuthenticationCode
import it.tdlight.jni.TdApi.CheckDatabaseEncryptionKey
import it.tdlight.jni.TdApi.MessageText
import it.tdlight.jni.TdApi.SetAuthenticationPhoneNumber
import it.tdlight.jni.TdApi.SetTdlibParameters
import it.tdlight.jni.TdApi.TdlibParameters
import it.tdlight.jni.TdApi.UpdateAuthorizationState
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset


class Grabber {

    val client: SimpleTelegramClient

    init {
        // Initialize TDLight native libraries
        Init.start()

        // Obtain the API token
        val apiToken = APIToken.example()

        // Configure the client
        val settings = TDLibSettings.create(apiToken)

        // Configure the session directory
        val sessionPath = Paths.get("example-tdlight-session")
        settings.databaseDirectoryPath = sessionPath.resolve("data")
        settings.downloadedFilesDirectoryPath = sessionPath.resolve("downloads")

        // Create a client
        client = SimpleTelegramClient(settings)

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(UpdateAuthorizationState::class.java, ::onUpdateAuthorizationState)
    }

    fun start(authenticationData: AuthenticationData) = client.start(authenticationData)

    fun waitForExit() = client.waitForExit()

    private fun onUpdateAuthorizationState(update: UpdateAuthorizationState) {
        println("onUpdateAuthorizationState: ${update.authorizationState::class.simpleName}")
        when (update.authorizationState) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                println("Query parameters")
                val parameters = TdlibParameters()
                parameters.databaseDirectory = "tdlib"
                parameters.useMessageDatabase = true
                parameters.useSecretChats = true
                parameters.apiId = 16869927
                parameters.apiHash = "bb665ff7fece88e2bfdf25252ac70238"
                parameters.systemLanguageCode = "en"
                parameters.deviceModel = "Desktop"
                parameters.applicationVersion = "1.0"
                parameters.enableStorageOptimizer = true

                client.send(SetTdlibParameters(parameters), ::authorizationRequestHandler)
            }
            is TdApi.AuthorizationStateWaitEncryptionKey ->
                client.send(CheckDatabaseEncryptionKey(), ::authorizationRequestHandler)
            is TdApi.AuthorizationStateWaitPhoneNumber ->
                client.send(SetAuthenticationPhoneNumber("+380674777029", null), ::authorizationRequestHandler)
            is TdApi.AuthorizationStateWaitCode -> {
                print("Please enter authentication code: ")
                val code = readln()
                client.send(CheckAuthenticationCode(code), ::authorizationRequestHandler)
            }
            is AuthorizationStateReady -> {
                println("Logged in")
//                searchChat()
                getMessages()
            }
            is AuthorizationStateClosing -> println("Closing...")
            is AuthorizationStateClosed -> println("Closed")
            is AuthorizationStateLoggingOut -> println("Logging out...")
            else -> println("Unknown state ${update.authorizationState::class.simpleName}")
        }
    }

    private fun authorizationRequestHandler(res: it.tdlight.client.Result<TdApi.Ok>) {
        if (res.isError) {
            println("authorizationRequestHandler: Receive an error:$res")
        } else {
            println("authorizationRequestHandler: OK")
        }
    }

    fun searchChat() {
        client.send(TdApi.SearchPublicChats("шрайк")) { chats ->
            if (checkError(chats, "searchChat")) {
                println("Chat count: ${chats.get().totalCount}")
                for (chat in chats.get().chatIds) {
                    client.send(TdApi.GetChat(chat)) { info ->
                        if (checkError(info, "GetChat"))
                            println("$chat: ${info.get().title}")
                    }
                }
            }
        }
    }

    fun getMessages() {
        client.send(TdApi.SearchChatMessages(
            -1001149277960,
            "#втрати",
            null,
            0, //3305, // 2985,
            0,
            10,
            null,
            0
        )) { messages ->
            if (checkError(messages, "SearchChatMessages")) {
                val msgArr = messages.get().messages
                println("Message count: ${msgArr.size}")
                for (msg in msgArr) {
                    val content = msg.content
                    println("Message. Class: ${content::class.simpleName}, Date: ${msg.javaDate()}")
                    if (content is MessageText)
                        println(content.text.text)
                    println("-------------")
                }
            }
            client.sendClose()
        }
    }

    companion object {

        private fun <T: TdApi.Object> checkError(test: it.tdlight.client.Result<T>, message: String): Boolean {
            val hasError = test.isError
            if (hasError) println("${message}: ${test.error}")
            return !hasError
        }

        private fun TdApi.Message.javaDate() =
            LocalDateTime.ofEpochSecond(this.date.toLong(), 0, ZoneOffset.UTC)
    }
}

