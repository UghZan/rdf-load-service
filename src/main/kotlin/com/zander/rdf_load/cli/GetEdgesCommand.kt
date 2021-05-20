package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service

@Command(name = "getE", description = "Print edges of graph")
class GetEdgesCommand : CliCommand {
	@Option(name = ["-l", "--limit"], description = "how much edges to show")
	private val limit = 10L


    override fun run() = Service().getEdges(limit)
}
