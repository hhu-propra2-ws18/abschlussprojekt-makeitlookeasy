# ausleiherino24

In order to "realize" database-persistence, create corresponding local docker volumes like so:

docker volume create --name database_volume -d local
docker volume create --name sonardb_volume -d local
			
(docker volume binding is bugged on Windows. Incorrect rights insinde bound volumes.. This is a workaround.)
