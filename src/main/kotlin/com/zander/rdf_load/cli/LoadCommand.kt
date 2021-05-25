package com.zander.rdf_load.cli

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.OptionType
import com.zander.rdf_load.Service
/*Класс LoadCommand
 *Содержит описание команды load
 */

//аннотация, регистрирующая класс как описание команды
//задаются название команды и описание для справки
@Command(name = "load", description = "Load RDF file")
class LoadCommand : CliCommand {
    //аннотация, задающая filePath как параметр команды
    @Option(name = ["-f", "--file"], description = "path to RDF file")
    private val filePath = ""

    //действия, выполняющиеся при запуске команды
    override fun run() = Service().start(filePath)
}
