package com.minio.miniojavarestexample;


import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import io.minio.errors.MinioException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.xmlpull.v1.XmlPullParserException;
 


@Path("/photoservice")
public class PhotoService {
	
	AlbumDao albumDao = new AlbumDao();
	
	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_JSON})
	public List<Album> listAlbums() throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException {
		 
			return albumDao.listAlbums();
		 
	}
	
	 
 	
	 
}