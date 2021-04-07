package com.zander.rdf_load

import org.apache.jena.riot.RDFDataMgr
import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal

class Endpoint(private val port: Int, private val path: String) {

    fun start() {

        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val trav = traversal().withRemote("conf/remote-graph.properties")

        if (path.isEmpty())
            throw Exception("Path is empty")

        //загружаем rdf файл
        val rdf = RDFDataMgr.loadModel(path)

        val names = mutableSetOf<String>() //набор имен узлов

        //загружаем субъекты ака узлы
        rdf.listSubjects().forEach {
            val V = trav.addV().`as`(it.uri)
            names.add(it.uri)
            client.submit(V)
            //println("added new vertex ${it.uri}")
        }

        println("All vertices added.")

        //теперь приступаем к подгрузке отношений
        rdf.listStatements().forEach {
            if (it.`object`.isLiteral) // если объект отношения есть литерал
            {
                val V = trav.V().`as`(it.subject.uri)
                V.property(it.predicate.uri, it.`object`.asLiteral().value.toString()) // добавляем в граф как свойство
                client.submit(V)
                //println("added new property")
            } else if (it.`object`.isURIResource) //если объект отношения есть другой объект (ресурс)
            {
                if (names.contains(it.`object`.asResource().uri)) { //и имеется узел с таким URI
                    val V1 = trav.V().`as`(it.subject.uri)
                    val V2 = trav.V().`as`(it.`object`.asResource().uri)
                    V1.addE(it.predicate.uri).to(V2) //создаем связь

                    client.submit(V1)
                    //println("added new edge")
                } else //иначе
                {
                    val V = trav.V().`as`(it.subject.uri) //добавляем как свойство
                    V.property(it.predicate.uri, it.`object`.asResource().uri)

                    client.submit(V)
                    //println("added new property")
                }
            }
        }

        println("All edges and properties added.")

        val results = trav.V()

        for (m in results) {
            System.out.println(m)
        }

        //trav.iterate()

        //println("Iteration completed.")

        //client.submit(trav.)

        println("Submitted.")

        trav.close()
        client.close()
        cluster.close()
    }
}
