Сервис загрузки RDF-графов в хранилище на основе LPG.

Инструкция по эксплуатации:
1. Запустить хранилище графов (дальше вырезка из Readme тимура, файлы там же):
	В командной строке `docker-compose up --build -d` из директории example/docker - *создаем образ Janusgraph сервера*

	Подключаемся к консоли Gremlin (в принципе необязательно): `docker exec -it docker_janusgraph_1 ./bin/gremlin.sh`
2. Запустить программу с одним из трех аргументов:
	- load --port *порт подключения, по умолчанию 1338* --path *путь к файлу начиная от каталога с проектом* - *загружает файл по указанному пути в хранилище*
	- getV - *получает список вершин с описанием свойств*
	- getE - *получает список ребер*
3. Запустить сервис Тимура (MainKt с аргументом -start)
4. Для example.ttl в папке /example/ можно использовать, например, следующий запрос:

select ?uri ?date
where {
  ?a v:statusCode 200;
     v:uri ?uri ;
	 v:processedAt ?date .
}