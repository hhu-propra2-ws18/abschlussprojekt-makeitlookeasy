# ausleiherino24

## Login Details
After starting the Docker Compose file, you can login into the web app
* as user :
	* username : user
	* password : password
* as admin : 
	* username : admin
	* password : password

## gradle-tasks
<u>bootRun</u> now automatically starts jenkins on 9000 and postgres on 5432. Folder structure is generated automatically and the app will be started first once postgres is ready. The app will be running on 8080 as usual.

<u>manualinit</u> can trigger the folder structure creation manually if needed.

<u>cleanUp</u> tries to delete all folders created, though it may be possible that it fails with a permission error, then one can run the clean.sh with elevation like so: ` ./scripts/clean.sh`.

<u>dockerReboot</u>, <u>dockerClean</u>, <u>dockerReset</u> do cleanup tasks in different intensitys.

## docker
Permissions of the database persistence folder (pgsql-01) are now synchronized with the invoking user, so that it should be visible and accessible from within the IDE.

Container/service and compose-logs are now automatically written to ./build/logs/

## jenkins
Does the same as travis, just more and better. It can be reached on localhost:8080. Persistent local storage is located
at ~/jenkins_home.

## javadocs
The task _javadocs_ generates its output to ./build/docs/javadoc/de. 

## code-style
All Sourcefiles are now formatted according to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Checkstyle is configured accordingly.

Importing the styleguide settings to the intelliJ formatter: +
`File -> Settings -> Editor -> Code Style -> Java -> Import Scheme -> IntelliJ Idea code style XML`
Then use ./config/checkstyle/intellij-java-google-style.xml

Then use CTRL+ALT+L to automatically format the currently opened file.

To get visual feedback regarding styleguide violations on-the-fly check "Treat Checkstyle errors as warnings." and configure the checkstyle intellij plugin like so: <br>
`File -> Settings -> Other Settings -> Checkstyle -> + -> Use local checkstyle-file and set a name -> Select ./config/checkstyle/checkstyle-google.xml.`

## Code Analysis
### sonarcloud
The app is now available online on sonarcloud [here](https://sonarcloud.io/organizations/ausleiherino24/).

Running the gradle-task _sonarqube_ will trigger a new analysis run, but the analysis is also automatically triggered on every new commit by travisCI.

The plugin _SonarLint_ marks sonarqubes findings locally in the IDE.

### PMD
Apart from the report that is generated when running the task <u>check</u> to ./build/reports/pmd automatically through gradle one can do a local run with the intellij PMD plugin. Once installed it can be configured acc. to Jens rules like so: <br>
`Settings -> Other Settings -> PMD -> + and then select ./config/pmd/ruleset.xml.` <br>
To run the analysis: Tools -> Run PMD -> custom rles -> ruleset.

