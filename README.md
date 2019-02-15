# ausleiherino24

## Login Details
After starting the Docker Compose file, you can login into the web app
* as user :
	* username : user
	* password : password
* as admin : 
	* username : admin
	* password : password
	
## database
The folder pgsql-01/persistence is initially owned by root. To fix this do `sudo
chown -R 1000:1000 ./pgsql-01/persistence` from within project root.

If one wants to fully reset the database or do cleanup, then delete folder ./pgsql-01/persistence  by doing `sudo rm -rf *` **from within** ./pgsql-01/persistence.
	

