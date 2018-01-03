# Java Photo API Service [![Slack](https://slack.minio.io/slack?type=svg)](https://slack.minio.io)

![minio_JAVA1](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA1.jpg?raw=true)


本示例将会指导你如何使用[Minio Server](https://docs.minio.io/docs/minio-quickstart-guide)和[Minio Java Client SDK](https://docs.minio.io/docs/java-client-quickstart-guide)构建一个非常简单的Java RESTful服务。


基于REST的应用通常是给移动端和web客户端提供服务。我们在本示例中创建的PhotoAPI Service将会给[Android Photo App](https://docs.minio.io/docs/android-photo-app)和[Swift Photo App](https://docs.minio.io/docs/swift-photo-app)提供服务。


你可以通过[这里](https://github.com/minio/minio-java-rest-example)获取完整的代码，代码是以Apache 2.0 License发布的。

##  1. 依赖

我们将使用Ecilpese IDE进行开发，同时需要用到Jersey，JSON和asm这几个包。

  * Eclipse
  * Jersey Bundle
  * Jersey Server
  * Jersey Core
  * JSON and asm


## 2. 设置  

* 步骤1 - 创建你的album存储桶

	```sh
	mc mb play/album

	mc cp ~/Downloads/Pic-1.jpg play/album/
	mc cp ~/Downloads/Pic-2.jpg play/album/
	mc cp ~/Downloads/Pic-3.jpg play/album/
	```
* 步骤2 - 使用`mc policy`命令可以将存储桶设为可公开读写。更多关于`mc policy`命令的细节，请参考[这里](https://docs.minio.io/docs/minio-client-complete-guide#policy)。

	```sh
	mc policy public play/album
	```

![minio-album.png](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-album.png?raw=true)

* 步骤3 - 启动Eclipse -> New Project -> Create a Dynamic Web Project。
 
将你的工程命名为PhotoAPIService

![minio-server-TomCatv8.5.png](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-server-TomCatv8.5.png?raw=true)


* 步骤4 - 将你的项目设为Maven项目

![minio_JAVA3](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA3.jpg?raw=true)


* 步骤5 - 创建一个新的pom.xml。  

这个pom.xml需要有Maven需要的所有配置信息。

![minio_JAVA4](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA4.jpg?raw=true)

* 步骤6 - 按下面所示在pom.xml中引入minio和其它需要的依赖。 

 下面是全部配置好的pom.xml。

![minio-dependencies3.0.6](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-dependencies3.0.6.png?raw=true)


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
			<version>3.0.6</version>
		</dependency>
  </dependencies>
</project>
```

* 步骤7 - 设置web.xml 

web.xml在\WebContent\WEB-INF\目录下。如果你那没有，你可以通过PhotoAPIService -> 右键单击 -> Java EE Tools -> Generate Deployment Descriptor Stub生成一个。

![minio_JAVA6](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA6.jpg?raw=true)


修改web.xml，按下面所示添加servlet配置。

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

## 3. 创建一个Service - PhotoService.java

创建一个PhotoService.java文件，添加listAlbums方法，调用这个方法返回图片url的json数组。


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

## 4. 操作数据 - AlbumDao.java

为了简单起见，在本示例中我们没有用到数据库。listAlbums()是连接到Minio Server并调用[listObjects](https://docs.minio.io/docs/java-client-api-reference#listObjects)方法，并包装成Album对象的List。album对象里含有过期时间为一天的presigned URL。

每次调用这个list接口，我们都会生成一个新的过期时间为一天的presigned URL。presigned URL很适合用于分享的应用场景，我们推荐你在合适的应用场景下使用。
 
更多关于presignedGetObject的细节，请看[这里]( https://docs.minio.io/docs/java-client-api-reference#presignedGetObject)。

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
             
            // Create a new Album Object
            Album album = new Album();
            
            // Set the presigned URL in the album object
            album.setUrl(minioClient.presignedGetObject(minioBucket, item.objectName(), 60 * 60 * 24));
            
            // Add the album object to the list holding Album objects
            list.add(album);
            
        }

        // Return list of albums.
        return list;
    }
}
```

## 5. 创建Root Element - Album.java

Root element用于持有Album的数据，url是它的一个属性。

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

## 6. 构建 

选中项目，运行mvn clean install进行构建。 

```sh
Project -> Run -> Maven Clean
Project -> Run -> Maven Install
```

![minio_JAVA7](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA7.jpg?raw=true)

maven install成功后，你应该可以在控制台中看见"BUILD SUCCESS"的信息，这时候我们就可以进行部署了。

![minio_JAVA8](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA8.jpg?raw=true)


## 7. 运行

* 步骤1 - 配置Tomcat

    * 点击Servers -> New
    * 选中Tomcat v8.5.16 Server将点击Next

![minio-server-TomCatv8.5](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-server-TomCatv8.5.png?raw=true)

* 步骤2 - 将你的项目加入到Server中

     * 点击Server-> Add / Remove Projects。
     * 选中项目并点击Add。

![minio_JAVA10](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA10.jpg?raw=true)

* 步骤3 - 运行Server

    * 点击Run按钮，并选择Run on Server。这将会在eclipse中打开一个浏览器，访问http://localhost:8080/PhotoAPIService/ 。

    * 在URL后面加上minio/photoservice/list，来查看json结果。完整的URL是http://localhost:8080/PhotoAPIService/minio/photoservice/list 。

    * 你也可以直接访问我们在[play server](http://play.minio.io:8080/PhotoAPIService-0.0.1-SNAPSHOT/minio/photoservice/list)上的示例。

![minio_JAVA11](https://github.com/minio/minio-java-rest-example/blob/master/docs/screenshots/minio-JAVA11.jpg?raw=true)


## 8. 了解更多。

- [Using `minio-java` library with Minio Server](https://docs.minio.io/docs/java-client-quickstart-guide) 
- [Minio Java Client SDK API Reference](https://docs.minio.io/docs/java-client-api-reference)
 
