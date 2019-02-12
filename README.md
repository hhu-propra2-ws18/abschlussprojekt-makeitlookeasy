# ausleiherino24

In order to "realize" database-persistence, create corresponding local docker volumes like so:

docker volume create --name database_volume -d local<br>
docker volume create --name sonardb_volume -d local
			
(docker volume binding is bugged on Windows. Incorrect rights insinde bound volumes.. This is a workaround.)


## Login Details

After starting the Docker Compose file, you can login into the web app
* as user :
	* username : user
	* password : password
* as admin : 
	* username : admin
	* password : password
	

