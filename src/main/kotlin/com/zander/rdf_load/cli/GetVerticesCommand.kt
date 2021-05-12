package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service

@Command(name = "getV", description = "Print vertices of graph")
class GetVerticesCommand : CliCommand {
    @Option(type = OptionType.COMMAND, name = ["-p", "--port"], description = "port of the endpoint")
    private val port = 1338

    override fun run() = Service(port).getVertices()
}
