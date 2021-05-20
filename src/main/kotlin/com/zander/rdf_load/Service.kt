package com.zander.rdf_load

import org.apache.jena.riot.RDFDataMgr
import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph

class Service() {

    fun start(path: String) {

        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        if (path.isEmpty())
            throw Exception("Path is empty")

        var start = System.currentTimeMillis()
        //загружаем rdf файл
        val rdf = RDFDataMgr.loadModel(path)

        println("File loaded. Statements count: ${rdf.listStatements().toList().size} (Took ${System.currentTimeMillis() - start} ms)")

        val names = mutableSetOf<String>() //набор имен узлов

        var verticesCount = 0
        start = System.currentTimeMillis()
        //загружаем субъекты ака узлы
        rdf.listSubjects().forEach {
            g.addV().property("uri", it.uri).iterate()
            names.add(it.uri)
            verticesCount++
        }

        println("All (${verticesCount}) vertices added. (Took ${System.currentTimeMillis() - start} ms)")

        start = System.currentTimeMillis()
        var edgesCount = 0
        var propCount = 0
        //теперь приступаем к подгрузке отношений
        rdf.listStatements().forEach {
            if (it.`object`.isLiteral) // если объект отношения есть литерал
            {
                propCount++
                g.V().has("uri", it.subject.uri).property(it.predicate.uri.removePrefix("http://").replace("_", "__").replace("/", "_"), it.`object`.asLiteral().value.toString()).iterate()// добавляем в граф как свойство
            }
            else if (it.predicate.localName == "type")
            {
                propCount++
                g.V().has("uri", it.subject.uri).property(it.predicate.uri.removePrefix("http://").replace("_", "__").replace("/", "_"), it.`object`.asResource().uri.toString()).iterate() // добавляем в граф как свойство
            }
            else if (it.`object`.isURIResource) //если объект отношения есть другой объект (ресурс)
            {
                edgesCount++
                val uri = it.`object`.asResource().uri

                if (!names.contains(uri)) //если нет узла с таким URI
                {
                    g.addV(uri).property("uri", uri).iterate()
                    names.add(uri)
                }
                val V1 = g.V().has("uri", it.subject.uri)
                val V2 = g.V().has("uri", uri)
                V1.addE(it.predicate.uri.removePrefix("http://").replace("_", "__").replace("/", "_")).to(V2).iterate() //создаем связь
            }
        }

        println("All edges(${edgesCount}) and properties(${propCount}) added. (Took ${System.currentTimeMillis() - start} ms)")

        g.close()
        client.close()
        cluster.close()
    }

    fun getVertices(limit: Long = -1)
    {
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        var results = if(limit < 0) g.V().elementMap<String>() else g.V().limit(limit).elementMap<String>()

        for (m in results) {
            println(m)
        }

        g.close()
        client.close()
        cluster.close()
    }

    fun getEdges(limit: Long = -1)
    {
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        var results = if(limit < 0) g.E().elementMap<String>() else g.E().limit(limit).elementMap<String>()

        for (m in results) {
            println(m)
        }

        g.close()
        client.close()
        cluster.close()
    }
}
