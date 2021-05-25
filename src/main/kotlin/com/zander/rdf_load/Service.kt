package com.zander.rdf_load

import org.apache.jena.riot.RDFDataMgr
import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph

/* Класс Service
 * Отвечает за загрузку RDF-графов в хранилище
 */

class Service {
    /*Функция Start()
    * Выполняется командой load, загружает файл по пути path в хранилище
    * Параметры:
    * String path - путь до файла
    */
    fun start(path: String) {
        //создание подключения к хранилищу
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")
        //создание источника обходов
        val g = traversal().withRemote("conf/remote-graph.properties")

        //если путь пустой, сообщаем об ошибке
        if (path.isEmpty())
            throw Exception("Path is empty")

        try {
            //замеры времени
            var start = System.currentTimeMillis()
            //загружаем rdf файл
            val rdf = RDFDataMgr.loadModel(path)

            println(
                "File loaded. Statements count: ${
                    rdf.listStatements().toList().size
                } (Took ${System.currentTimeMillis() - start} ms)"
            )

            val names = mutableSetOf<String>() //набор имен узлов

            var verticesCount = 0
            start = System.currentTimeMillis()
            //загружаем субъекты и создаем узлы в графе
            rdf.listSubjects().forEach {
                //запоминаем URI субъектов в свойстве uri
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
                    //подменяем все / на _, _ на __, убираем http:
                    g.V().has("uri", it.subject.uri)
                        .property(
                            it.predicate.uri
                                .removePrefix("http://")
                                .replace("_", "__")
                                .replace("/", "_"), it.`object`.asLiteral().value.toString()
                        )
                        .iterate()// добавляем в граф как свойство
                } else if (it.predicate.localName == "type") {
                    propCount++
                    //подменяем все / на _, _ на __, убираем http:
                    g.V().has("uri", it.subject.uri)
                        .property(
                            it.predicate.uri
                                .removePrefix("http://")
                                .replace("_", "__")
                                .replace("/", "_"), it.`object`.asResource().uri.toString()
                        )
                        .iterate() // добавляем в граф как свойство
                }
                //если объект отношения есть другой объект (ресурс)
                else if (it.`object`.isURIResource) {
                    edgesCount++
                    val uri = it.`object`.asResource().uri

                    if (!names.contains(uri)) //если нет узла с таким URI
                    {
                        //создаем узел, запоминаем URI
                        g.addV(uri).property("uri", uri).iterate()
                        names.add(uri)
                    }
                    val V1 = g.V().has("uri", it.subject.uri)
                    val V2 = g.V().has("uri", uri)
                    V1.addE(
                        it.predicate.uri
                            .removePrefix("http://")
                            .replace("_", "__")
                            .replace("/", "_")
                    )
                        .to(V2)
                        .iterate() //создаем связь между вершинами
                }
            }

            println("All edges(${edgesCount}) and properties(${propCount}) added. (Took ${System.currentTimeMillis() - start} ms)")
            //закрываем соединения
        }
        catch(e: Exception)
        {
            println("Exception caught: ${e.localizedMessage}")
        }
        finally {
            g.close()
            client.close()
            cluster.close()
        }
    }

    /*Функция getVertices()
    * Выполняется командой getV, выводит limit вершин графа с ID и свойствами
    * Параметры:
    * Long limit - количество вершин для вывода
    */

    fun getVertices(limit: Long = -1)
    {
        //создаем соединение с хранилищем и источник обходов
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        try {
            //если limit меньше нуля, выводим все вершины графа
            //иначе только обозначенное количество
            var results = if (limit < 0)
                g.V().elementMap<String>()
            else
                g.V().limit(limit).elementMap<String>()

            //печатаем результаты
            for (m in results) {
                println(m)
            }
        }
        catch(e: Exception)
        {
            println("Exception caught: ${e.localizedMessage}")
        }
        finally {
            g.close()
            client.close()
            cluster.close()
        }
    }

    /*Функция getEdges()
    * Выполняется командой getE, выводит limit ребер графа
    * с ID, свойствами и вершинами на концах
    * Параметры:
    * Long limit - количество ребер для вывода
    */

    fun getEdges(limit: Long = -1)
    {
        //создаем соединение с хранилищем и источник обходов
        val cluster = Cluster.open("conf/remote-objects.yaml")
        val client = cluster.connect<Client>().alias("g")

        val g = traversal().withRemote("conf/remote-graph.properties")

        try {
        //если limit меньше нуля, выводим все ребра графа
        //иначе только обозначенное количество
        var results = if(limit < 0)
            g.E().elementMap<String>()
        else
            g.E().limit(limit).elementMap<String>()

        //печатаем результаты
        for (m in results) {
            println(m)
        }
    }
    catch(e: Exception)
    {
        println("Exception caught: ${e.localizedMessage}")
    }
    finally {
        g.close()
        client.close()
        cluster.close()
    }
    }
}
