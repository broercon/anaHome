package org.anaHome

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader

@SpringBootApplication
class Application

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    val name = "Kotlin"
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    println("Hello, " + name + "!")

    for (i in 1..5) {
        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
        // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
        println("i = $i")
    }

    startDockerCompose()
    runApplication<Application>(*args)
}


fun startDockerCompose() {
    try {
        // Step 1: Run docker-compose up to start the services
        println("Starting Docker Compose...")
        val processBuilder = ProcessBuilder("docker-compose", "up", "-d")

        // Capture the output of the docker-compose command
        val output = StringBuilder()
        val reader = BufferedReader(InputStreamReader(processBuilder.start().inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }

        // Print the output of the docker-compose process
        println(output.toString())
        println("Docker Compose started successfully.")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
