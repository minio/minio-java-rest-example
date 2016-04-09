package com.minio.miniojavarestexample;

import java.util.ArrayList;
import java.util.List;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.errors.MinioException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParserException;
  
 

public class AlbumDao {

    public List<Album> listAlbums()  throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException {
        List<Album> list = new ArrayList<Album>();
        final String minioBucket = "albums";
        
        //MinioClient minioClient = new MinioClient("s3.amazonaws.com", "AKIAJZG5SMMVPW7IPWNQ", "vrWGtgOcBkWZR+L3VpSb41Q8FpxWgSGBZoO3E8Vf");
		 MinioClient minioClient = new MinioClient("play.minio.io", 9000, "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
		 
         
        
        Iterable<Result<Item>> myObjects = minioClient.listObjects(minioBucket);
        for (Result<Item> result : myObjects) {
          Item item = result.get();
          System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
          Album album = new Album();
          
          album.setUrl("https://play.minio.io:9000/"+ minioBucket + "/" +item.objectName());
          list.add(album);
        } 

    	 
        return list;
    }
 

     
    
  
}
