# Java Photo API Service [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/minio/minio?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![minio_JAVA1](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA1.jpg?raw=true)


This example will guide you through the code to build a really simple Java based RESTful service with the [Minio Server](https://docs.minio.io/docs/minio-quickstart-guide) and the[ Minio Java Client SDK](https://docs.minio.io/docs/java-client-quickstart-guide). 

REST based apps are often written to service mobile and web clients. PhotoAPI Service we create in this example will service the [Android Photo App](https://docs.minio.io/docs/android-photo-app)  and [Swift Photo App](https://docs.minio.io/docs/swift-photo-app) examples.

The full code is available at:  [https://github.com/minio/minio-java-rest-example](https://github.com/minio/minio-java-rest-example), and is released under Apache 2.0 License.

##  1. Dependencies

We will use Eclipse IDE to build this example and include Jersey, JSON and asm packages.

  * Eclipse
  * Jersey Bundle
  * Jersey Server
  * Jersey Core
  * JSON and asm


## 2. SetUp  

* Step 1 -  Launch Eclipse -> New Project -> Create a Dynamic Web Project. 
 
Name your project PhotoAPIService

![minio_JAVA2](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA2.jpg?raw=true)


* Step 2 - Convert the project to a Maven Project as shown below

![minio_JAVA3](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA3.jpg?raw=true)



* Step 3 -  Create a new pom.xml in the next screen.  

This pom.xml will have all the configuration details that Maven needs, to build the project.

![minio_JAVA4](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA4.jpg?raw=true)

* Step 4 -  Include the minio library and other dependencies in the pom.xml file as shown below. 

 Here's the full pom.xml generated after adding all the above dependencies successfully. 

![minio_JAVA5](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA5.jpg?raw=true)


```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>PhotoAPIService</groupId>
  <artifactId>PhotoAPIService</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>war</packaging>
  <build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
      <plugin>
        <artifactId>maven-war-plugin</artifactId>
        <version>2.4</version>
        <configuration>
          <warSourceDirectory>WebContent</warSourceDirectory>
          <failOnMissingWebXml>false</failOnMissingWebXml>
        </configuration>
      </plugin>
    </plugins>
  </build>
   <dependencies>
		<dependency>
			<groupId>asm</groupId>
			<artifactId>asm</artifactId>
			<version>3.3.1</version>
		</dependency>
		<dependency>
			<groupId>com.sun.jersey</groupId>
			<artifactId>jersey-bundle</artifactId>
			<version>1.19</version>
		</dependency>
		<dependency>
			<groupId>org.json</groupId>
			<artifactId>json</artifactId>
			<version>20140107</version>
		</dependency>
		<dependency>
			<groupId>com.sun.jersey</groupId>
			<artifactId>jersey-server</artifactId>
			<version>1.19</version>
		</dependency>
		<dependency>
			<groupId>com.sun.jersey</groupId>
			<artifactId>jersey-core</artifactId>
			<version>1.19</version>
		</dependency>
		<dependency>
			<groupId>io.minio</groupId>
			<artifactId>minio</artifactId>
			<version>1.0.1</version>
		</dependency>
  </dependencies>
</project>

```

* Step 5 - Set Up web.xml 

web.xml also known as the deployment descriptor, resides under \WebContent\WEB-INF\  directory. If you don't see one, you may generate a new web.xml by selecting PhotoAPIService -> Right Click -> Java EE Tools -> Generate Deployment Descriptor Stub.

![minio_JAVA6](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA6.jpg?raw=true)


Modify the web.xml to include the servlet-name and url-pattern as shown below.

```xml

<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://xmlns.jcp.org/xml/ns/javaee" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" version="3.1">
  <display-name>PhotoAPIService</display-name>
  <welcome-file-list>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
    <welcome-file>index.jsp</welcome-file>
    <welcome-file>default.html</welcome-file>
    <welcome-file>default.htm</welcome-file>
    <welcome-file>default.jsp</welcome-file>
  </welcome-file-list>
  <servlet>
		<servlet-name>Jersey Web Application</servlet-name>
		<servlet-class>com.sun.jersey.spi.container.servlet.ServletContainer</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>Jersey Web Application</servlet-name>
		<url-pattern>/minio/*</url-pattern>
	</servlet-mapping>
  
</web-app>

```

## 3. Create a Service - PhotoService.java


Create PhotoService.java where we add a list api method. Calling the list api on photoservice returns  a json of image urls from the albumDao object.


```java

package com.minio.photoapiservice;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.xmlpull.v1.XmlPullParserException;

import io.minio.errors.MinioException;

@Path("/photoservice")
public class PhotoService {
    // Initialize new album service.
    AlbumDao albumDao = new AlbumDao();

    // Define GET method and resource.
    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Album> listAlbums() throws InvalidKeyException,
            NoSuchAlgorithmException, IOException,
            XmlPullParserException, MinioException {

        // Return list of albums.
        return albumDao.listAlbums();
    }
}

```

## 4. Data Management -  AlbumDao.java

For simplicity we don't have a database in this example. listAlbums() simply connects with the Minio Server and returns a List of Album Objects using the [listObjects](https://docs.minio.io/docs/java-client-api-reference#listObjects) API. The individual album objects are populated with presigned URLs which are set to expire in a day. 

Every time a calling client consumes the list API service, we generate new presigned URLs which will expire in 1 day. This is a best practice and we recommend using presigned URLs wherever applicable. Expiring presigned URLs are especially useful in share use cases.
 
Learn more about this [API ](https://docs.minio.io/docs/java-api-reference#presignedGetObject )

```java

 package com.minio.photoapiservice;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.errors.MinioException;

public class AlbumDao {
    public List<Album> listAlbums() throws NoSuchAlgorithmException,
            IOException, InvalidKeyException, XmlPullParserException, MinioException {

        List<Album> list = new ArrayList<Album>();
        final String minioBucket = "albums";

        // Initialize minio client object.
        MinioClient minioClient = new MinioClient("play.minio.io", 9000,
                                                  "Q3AM3UQ867SPQQA43P2F",
                                                  "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

        // List all objects.
        Iterable<Result<Item>> myObjects = minioClient.listObjects(minioBucket);

        // Iterate over each elements and set album url.
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());

            // Generate a presigned URL which expires in a day
            url = minioClient.presignedGetObject(minioBucket, item.objectName(), 60 * 60 * 24);
             
            // Create a new Album Object
            Album album = new Album();
            
            // Set the presigned URL in the album object
            album.setUrl(url);
            
            // Add the album object to the list holding Album objects
            list.add(album);
            
        }

        // Return list of albums.
        return list;
    }
}


```

## 5. Create the Root Element -  Album.java

The root element holds the underlying Album data. url is a member variable in the Album class.

```java

package com.minio.photoapiservice;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Album")
public class Album {
    private String url;
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

```

## 6. Build 

Select the Project and do a Maven Clean and then do a Maven Install which automatically builds the project. 

```sh

Project -> Run -> Maven Clean
Project -> Run -> Maven Install

```

![minio_JAVA7](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA7.jpg?raw=true)

After Maven install, you should see "BUILD SUCCESS" as shown below in the console. Once you see this, we are ready to deploy the application on Tomcat.

![minio_JAVA8](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA8.jpg?raw=true)


## 7. Run

* Step 1 - Configure Tomcat

    * Click on Servers -> New
    *  Pick the Tomcat v8.0 Server and then click Next (as shown below)

![minio_JAVA9](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA9.jpg?raw=true)

* Step 2 - Add your Project to the Server

     * Click on Server-> Add / Remove Projects.
     * Select this project on the left and click on Add.

![minio_JAVA10](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA10.jpg?raw=true)

* Step 3 - Run on the Server

    * Press the Run Button on Eclipse and Choose Run on Server. This will open a browser window inside eclipse with http://localhost:8080/PhotoAPIService/

    * Add  minio/photoservice/list to the end of the above URL to see the json output. The full URL would be http://localhost:8080/PhotoAPIService/minio/photoservice/list

    * You may also directly call our play hosted URL of this example. Please visit the URL http://play.minio.io:8080/PhotoAPIService-0.0.1-SNAPSHOT/minio/photoservice/list on a browser or any HTTP client such as Postman.

![minio_JAVA11](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA11.jpg?raw=true)


## 8. Explore Further.

- [Using `minio-java` library with Minio Server](https://docs.minio.io/docs/java-client-quickstart-guide) 
- [Minio Java Client SDK API Reference](https://docs.minio.io/docs/java-client-api-reference)
 
