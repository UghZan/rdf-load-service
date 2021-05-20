Сервис загрузки RDF-графов в хранилище на основе LPG.

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