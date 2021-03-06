package com.zander.rdf_load.cli

import com.github.rvesse.airline.Cli
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException
import com.github.rvesse.airline.parser.errors.ParseCommandMissingException
import com.github.rvesse.airline.parser.errors.ParseException
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException

fun main(vararg args: String) {
    val cli = getCli()

    try {
        cli.parse(*args).run()
    } catch (e: ParseCommandMissingException) {
        main("help")
    } catch (e: ParseException) {
        when (e) {
            is ParseArgumentsUnexpectedException,
            is ParseOptionMissingException -> {
                System.err.println("Error: " + e.message)
                val commandName = args[0]
                System.err.println("Run `rdf-load-service help $commandName` to see help for the command `$commandName`\n")
            }
            else -> {
                System.err.println("Error: " + e.message)
                System.err.println("Run `rdf-load-service help` to see help")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getCli(): Cli<CliCommand> =
    Cli.builder<CliCommand>("rdf-load-service")
        .withCommands(
            HelpCommand::class.java,
            LoadCommand::class.java,
            GetVerticesCommand::class.java,
            GetEdgesCommand::class.java
        )
        .build()
