package com.zander.rdf_load

import org.apache.jena.riot.RDFDataMgr
import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph

class Endpoint(private val port: Int) {

    fun start(path: String) {

        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = EmptyGraph.instance().traversal().withRemote("conf/remote-graph.properties")

        if (path.isEmpty())
            throw Exception("Path is empty")

        //загружаем rdf файл
        val rdf = RDFDataMgr.loadModel(path)

        val names = mutableSetOf<String>() //набор имен узлов

        //загружаем субъекты ака узлы
        rdf.listSubjects().forEach {
            g.addV().property("uri", it.uri).iterate()
            names.add(it.uri)
            //println("added new vertex ${it.uri}")
        }

        println("All vertices added.")

        //теперь приступаем к подгрузке отношений
        rdf.listStatements().forEach {
            if (it.`object`.isLiteral) // если объект отношения есть литерал
            {
                //!!!Тестовые изменения!!!
                //Поменял URI свойства на локальное имя, для теста работы сервиса запросов
                g.V().has("uri", it.subject.uri).property(it.predicate.localName, it.`object`.asLiteral().value.toString()).iterate() // добавляем в граф как свойство
            }
            else if (it.predicate.localName == "type")
            {
                //!!!Тестовые изменения!!!
                //Поменял URI свойства на локальное имя, для теста работы сервиса запросов
                g.V().has("uri", it.subject.uri).property(it.predicate.localName, it.`object`.asResource().uri).iterate() // добавляем в граф как свойство
            }
            else if (it.`object`.isURIResource) //если объект отношения есть другой объект (ресурс)
            {
                val uri = it.`object`.asResource().uri

                if (!names.contains(uri)) //если нет узла с таким URI
                {
                    g.addV().property("uri", uri).iterate()
                    names.add(uri)
                }
                val V1 = g.V().has("uri", it.subject.uri)
                val V2 = g.V().has("uri", uri)
                V1.addE(it.predicate.uri).to(V2).iterate() //создаем связь
            }
        }

        println("All edges and properties added.")

        println("Submitted.")

        g.close()
        client.close()
        cluster.close()
    }

    fun getVertices()
    {
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        val results = g.V().limit(10).valueMap<String>().toList()

        for (m in results) {
            println(m)
        }
    }

    fun getEdges()
    {
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        val results = g.E().limit(10).valueMap<String>().toList()

        for (m in results) {
            println(m)
        }
    }
}
