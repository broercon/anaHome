package org.broercon.anahome

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader

@SpringBootApplication
class Application

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    startDockerCompose()
    runApplication<Application>(*args)
}


private fun startDockerCompose () {
    val logger = KotlinLogging.logger {}

    try {
        // Step 1: Run docker-compose up to start the services
        logger.info {"Starting Docker Compose..."}
        val processBuilder = ProcessBuilder("docker-compose", "up", "-d")
        val reader = BufferedReader(InputStreamReader(processBuilder.start().inputStream))

        if (logger.isDebugEnabled) {
            // Capture the output of the docker-compose command
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null)
                output.append(line).append("\n")
            // Print the output of the docker-compose process
            logger.info {output.toString()}
        }

        logger.info {"Docker Compose started successfully."}

    } catch (e: Exception) {
        e.printStackTrace()
    }
}