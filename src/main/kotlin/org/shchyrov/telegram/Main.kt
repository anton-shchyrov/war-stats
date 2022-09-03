package org.shchyrov.telegram

import it.tdlight.client.AuthenticationData


fun main(args: Array<String>) {
    val grabber = Grabber()

    // Configure the authentication info
    val authenticationData = AuthenticationData.user(380674777029)

    grabber.start(authenticationData)
    grabber.waitForExit()
}