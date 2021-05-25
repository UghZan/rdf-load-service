package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service

/*Класс GetVerticesCommand
 *Содержит описание команды getV
 */

//аннотация, регистрирующая класс как описание команды
//задаются название команды и описание для справки
@Command(name = "getV", description = "Print vertices of graph")
class GetVerticesCommand : CliCommand {

	//аннотация, устанавливающая limit как параметр команды
	@Option(name = ["-l", "--limit"], description = "how much vertices to show")
	private val limit = 10L

	//действия, выполняющиеся при выполнении команды
    override fun run() = Service().getVertices(limit)
}
