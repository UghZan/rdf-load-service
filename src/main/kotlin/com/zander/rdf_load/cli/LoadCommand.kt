package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service

@Command(name = "load", description = "Load RDF file")
class LoadCommand : CliCommand {

    @Option(type = OptionType.COMMAND, name = ["-f", "--file"], description = "path to RDF file")
    private val filePath = ""

    override fun run() = Service().start(filePath)
}
