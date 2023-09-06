# Pygmy Marmoset: an assignment submission webapp for CS courses

![Pygmy marmoset photo](PygmyMarmosetWebapp/war/img/pygmyMarmoset-crop.jpg)

Pygmy Marmoset is a very simple assignment submission web application for CS courses.

It is designed as a very feature-reduced version of [Marmoset](http://marmoset.cs.umd.edu/index.shtml).  Pygmy Marmoset has no provision for automated testing or code reviews, so if you need those, use Marmoset!

I had several motivations for creating Pygmy Marmoset:

* *Smaller and more extensible code base*.  The Marmoset code base has a lot of the usual problems associated with being a research prototype.
* *Ease of setup and deployment*.  Marmoset requires an application server to run.  Pygmy Marmoset is deployable as a single jar file, much the same as [CloudCoder](https://cloudcoder.org).
* *Improved UI*.  The feature reduction comes will a significant simplification of the user interface.  Some aspects of the UI are improved: for example, [prism.js](http://prismjs.com/) is used for highlighting submitted source code.
* *Bug fixes*.  In particular, Marmoset has issues allowing students to check their submissions: sometimes source files don't appear in the file listing for the submission.

## Current status

Pygmy Marmoset is ready for production!  We are using it for computer science courses at [York College of Pennsylvania](https://www.ycp.edu) effective Fall 2017.

Note that there are no formal releases because feature development typically happens on branches, and commits to the master branch correspond to actual deployed production versions.  So you should be fine building and running the version on the master branch.

## Trying it out

You will need JDK 17 (or higher?).

### Part 1: Prepare a Database

**1**. Install MariaDB and start the service.  <br>
For example, on Oracle Linux run the following commands:

```bash
sudo dnf install mariadb-server
sudo systemctl enable --now mariadb
```

**2**. Run the MariaDB client as `root`.

```bash
sudo mariadb --user root
```

**3**. From within the MariaDB client, create a MariaDB database. <br>
In this example, the database is called `pygmymarmosetdb`.

```sql
CREATE DATABASE pygmymarmosetdb;
```

**4**. Create a MariaDB user with full privileges on the newly created database.
In this example, the user `ingo` is created with the password `nicekitty123` and given privileges on the `pygmymarmosetdb` database.

```sql
CREATE USER 'ingo'@'localhost' IDENTIFIED BY 'nicekitty123';
GRANT ALL PRIVILEGES ON pygmymarmosetdb.* TO 'ingo'@'localhost';
```

5. Exit the MariaDB client.

```sql
quit;
```

<br>

### Part 2: Build Pygmy Marmoset and Create DB Tables

**1**. Download the Pygmy Marmoset code or clone this git repo.

```bash
git clone https://github.com/jmoscola/PygmyMarmoset.git
```

**2**. In the top-level `PygmyMarmoset` directory (after cloning the repo), create a file called `pygmymarmoset.properties` with the following properties:

* `pm.db.host`: the hostname of the database server
* `pm.db.user`: the MariaDB username the webapp will use to connect to the database
* `pm.db.passwd`: the password associated with the MariaDB username
* `pm.db.name`: the name of the MariaDB database

Here is an example configuration using the values from Part 1 of this guide:

```properties
pm.db.host=localhost
pm.db.user=ingo
pm.db.passwd=nicekitty123
pm.db.name=pygmymarmosetdb
```

This example configuration assumes that the MariaDB server is on the same server as the Pygmy Marmoset webapp (hence `localhost` as the value of `pm.db.host`).

**3**. From the top-level PygmyMarmoset directory, run the command:

```bash
./gradlew uberJar
```

This will install the [Gradle](https://gradle.org/) build tool, download all the required dependencies and build the web application.  After a successful build, the file `build/pygmyMarmosetApp.jar` is the executable webapp jar file.

**4**. From the top-level PygmyMarmoset directory, run the following command to create the required database tables and the initial Pygmy Marmoset user account.

```bash
java -jar ./build/pygmyMarmosetApp.jar createdb
```

Note that the account created during this step is **NOT** the same as the MariaDB account.  The account created during this step is the initial `admin` user for the Pygmy Marmoset webapp.  The username and password created during this step is used to log into the Pygmy Marmoset web interface. 

<br>

### Part 3: Launch and Connect to Pygmy Marmoset

**1**. With Pygmy Marmoset successfully built, you can start the webapp by running the following command:

```bash
java -jar ./build/pygmyMarmosetApp.jar start
```
Note that this command is running the web application from the `build` directory.

**2**. Open a web browser and connect to the local instance of Pygmy Marmoset at [http://localhost:8080/marmoset](http://localhost:8080/marmoset).  At the login page, log into the web application using the `admin` username and password created in `Part 2, Step 4`.  Once logged in, use the `Admin` tab, located at the top, to create additional users and courses.

**3**. To shut down the Pygmy Marmoset server run the following command from the top level of the Pygmy Marmoset source tree:

```bash
java -jar ./build/pygmyMarmosetApp.jar shutdown
```

<br>

### Part 4: Deploying a Production Instance

**1**. On your production server, follow all the steps from `Part 1` of this guide to get MariaDB installed and configured for use with the Pygmy Marmoset web application. Of course, you will also need JDK 17 (or higher?).

**2**. As described in `Part 2` of this guide, download the source code and compile the `pygmyMarmosetApp.jar` file for the web application.  Be sure to create the `pygmymarmoset.properties` file prior to building.  The executable `pygmyMarmosetApp.jar` file is portable and can be compiled on either a development machine or your production server. After compiling, copy the executable `pygmyMarmosetApp.jar` file from the `./build` directory to an appropriate location on your production server (e.g. `/web/marmoset/webapp/`).

**3**. On your production server, run the `pygmyMarmosetApp` `createdb` command as was previously demonstrated in `Part 2, Step 3` of this guide. For example, if your production instance is located in  `/web/marmoset/webapp/`:

```bash
cd /web/marmoset/webapp/
java -jar pygmyMarmosetApp.jar createdb
```

**4**. On your production server, launch the `pygmyMarmosetApp` webapp as was previously demonstrated in `Part 3` of this guide. For example, if your production instance is located in  `/web/marmoset/webapp/`:

```bash
cd /web/marmoset/webapp/
java -jar pygmyMarmosetApp.jar start
```

You should now be able to connect to your production instance of Pygmy Marmoset at http://localhost:8080/marmoset **-OR-** at http://servername:8080/marmoset.

**5**. For convenience, it's nice to ensure that Pygmy Marmoset runs on a system reboot (or restarts in the event of a crash). Run the following command to create a **cron job**:

```bash
crontab -e 
```

In the file that opens, copy and paste the following **cron job** settings. These settings will run a check every five minutes to see if Pygmy Marmoset is running.  If Pygmy Marmoset is not running, the web application will be restarted.

```conf
# Poke Pygmy Marmoset every 5 minutes
*/5 * * * * cd /web/marmoset/webapp && [ ! -e nopoke ] && (java -jar pygmyMarmosetApp.jar poke >> logs/poke.log 2>&1)
```


**6 (a)**. **[OPTIONAL]** By default, the webapp listens for connections on `localhost:8080`. You will almost certainly want to set up Apache 2 or nginx to act as a reverse proxy. This will allow for secure connections via HTTPS.  For example, to install and run Apache 2 on Oracle Linux, run the following commands:

```bash
sudo dnf install httpd
sudo dnf install mod_ssl
sudo setsebool httpd_can_network_connect 1
sudo systemctl enable --now httpd.service
```

**6 (b)**. **[OPTIONAL]** Next, disable the default HTTPS settings. As an `admin` edit the `/etc/httpd/conf.d/ssl.conf` and comment out the default HTTPS settings from `<VirtualHost _default_:443>` ... to ... `</VirtualHost>`.

**6 (c)**. **[OPTIONAL]** As an `admin` create and edit a custom Apache configuration file with settings to handle HTTPS and forwarding from HTTP to HTTPS.  For example, on Oracle Linux:

```bash
sudo vi /etc/httpd/conf.d/example.school.edu.conf
```
Below is an example configuration file.  Note that you'll need to acquire and install the required SSL certificates. Replace the values for `SSLCertificateFile`, `SSLCertificateKeyFile`, and `SSLCertificateChainFile` as appropriate for your SSL configuration.
```conf
##################################################
<VirtualHost *:80>
ServerName example.school.edu

Redirect "/marmoset" "https://example.school.edu/marmoset"

ErrorLog logs/marmoset_http_error_log
TransferLog logs/marmoset_http_access_log
LogLevel warn
</VirtualHost>
##################################################
<VirtualHost *:443>
ServerName example.school.edu

SSLEngine On
SSLProxyEngine On
SSLCertificateFile /etc/pki/tls/certs/example_school_edu_cert.cer
SSLCertificateKeyFile /etc/pki/tls/private/server.key
#SSLCertificateChainFile /etc/pki/tls/certs/chain.cer

# Transparently proxy requests for /marmoset to the Marmoset server
ProxyPass /marmoset http://localhost:8080/marmoset
ProxyPassReverse /marmoset http://localhost:8080/marmoset
<Proxy http://localhost:8080/marmoset>
Order Allow,Deny
Allow from all
</Proxy>

ErrorLog logs/marmoset_http_error_log
TransferLog logs/marmoset_http_access_log
LogLevel warn
</VirtualHost>
##################################################
```

**6 (d)**. **[OPTIONAL]** Restart the Apache server with the following command.

```bash
sudo systemctl restart httpd
```

You should now be able to connect to your production instance of Pygmy Marmoset at https://servername/marmoset.


**7**. To shut down your production instance of the Pygmy Marmoset server run the following command:

```bash
cd /web/marmoset/webapp/
java -jar pygmyMarmosetApp.jar shutdown
```

Note that this does not shutdown Apache.  If you also wish to shutdown Apache, run the following command:

```bash
sudo systemctl stop httpd
```

<br>

### Part 5: Updating a Production Instance

If you would like to deploy a new version of the `pygmyMarmosetApp.jar` file to your production server, following these steps.

**1.** Log into the production server, and disable automatic restarts (as a result of the cron job) of the Pygmy Marmoset server. The existence of the `nopoke` file prevents Pygmy Marmoset from restarting. Be sure to remove it later!

```bash
cd /web/marmoset/webapp/
touch nopoke
```

**2.** Shut down the running instance of Pygmy Marmoset and backup the `.jar` file. 

```bash
java -jar pygmyMarmosetApp.jar shutdown
mv pygmyMarmosetApp.jar pygmyMarmosetApp.jar.bak
```

**3.** Copy your new version of the `pygmyMarmosetApp.jar` file to your server and into the `/web/marmoset/webapp/` directory.

**4.** Restart the Pygmy Marmoset server 
```bash
java -jar pygmyMarmosetApp.jar start
```

**5.** Remove the `nopoke` file
```bash
rm nopoke
```

## License

The code is distributed under the terms of the [AGPL v3](https://www.gnu.org/licenses/agpl-3.0.en.html).  See [LICENSE.txt](LICENSE.txt).
