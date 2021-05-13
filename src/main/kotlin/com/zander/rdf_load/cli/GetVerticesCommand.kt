package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service

@Command(name = "getV", description = "Print vertices of graph")
class GetVerticesCommand : CliCommand {
    @Option(name = ["-p", "--port"], description = "port of the endpoint")
    private val port = 1338
	@Option(name = ["-l", "--limit"], description = "how much vertices to show")
	private val limit = 10

    override fun run() = Service(port).getVertices(limit)
}
