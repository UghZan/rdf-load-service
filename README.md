RDF graph loading service for LPG-based graph DB.

Quick guide:
1. Launch a graph DB server (JanusGraph-based example can be found at https://github.com/UghZan/sparql-service, example folder)
	Start up command line and from /docker/ directory `docker-compose up --build -d`
	
2. Start up the service using one of three commands:
	-load --file *path to file* - loads up file on the set path (e.g. load --file example/example.ttl)
	-getV --limit *count of elements* - shows a list of *limit* vertices in graph
	-getE --limit *count of elements* - same for edges
	
3. After loading the file, you may launch the sparql-service (at https://github.com/UghZan/sparql-service, MainKt with start command)
and send a HTTP POST request with SPARQL-request in the body to localhost:*port*

Examples for file example.ttl:
1)
prefix log: <http://example.org/ont/transaction-log/>

select ?uri ?date
where {
  ?a v:log:statusCode 200;
     v:uri ?uri ;
	 v:log:processedAt ?date .
}

2)
prefix log: <http://example.org/ont/transaction-log/>
select ?trx ?srv
where {
  ?a e:log:processedBy ?b .
  ?a v:uri ?trx .
  ?b v:uri ?srv .
}

--------------------------------------------------------------------------------------------------------------------------
RU
Инструкция по эксплуатации:
1. Запустить хранилище графов (https://github.com/UghZan/sparql-service, папка example):
	В командной строке `docker-compose up --build -d` из директории example/docker - *создаем образ Janusgraph сервера*

	Подключаемся к консоли Gremlin (в принципе необязательно): `docker exec -it docker_janusgraph_1 ./bin/gremlin.sh`
2. Запустить программу с одним из трех аргументов:
	- load --file *путь к файлу начиная от каталога с проектом* - *загружает файл по указанному пути в хранилище*
	- getV --limit *количество элементов в списке вывода* - *получает список вершин с описанием свойств*
	- getE --limit *количество элементов в списке вывода* - *получает список ребер*
3. Запустить сервис выполнения SPARQL-запросов(MainKt с аргументом -start)
4. Для example.ttl в папке /example/ можно использовать, например, следующий запрос:

prefix log: <http://example.org/ont/transaction-log/>

select ?uri ?date
where {
  ?a v:log:statusCode 200;
     v:uri ?uri ;
	 v:log:processedAt ?date .
}
Пример 2:

prefix log: <http://example.org/ont/transaction-log/>
select ?trx ?srv
where {
  ?a e:log:processedBy ?b .
  ?a v:uri ?trx .
  ?b v:uri ?srv .
}