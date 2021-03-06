package com.scholastic.ereader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;


import java.util.*; 

@Path("/restApp")
public interface BookApi {

@GET
@Path("/hello")
@Produces("text/plain")
public String hello();

	
@GET 
@Produces ("application/json")
@Path("/GetContentMetaData")
public String GetContentMetaData (@QueryParam ("bookid") String id,
											   @QueryParam ("sessionToken") String token
											 /*,@QueryParam ("type") String type */ ); 


@GET 
@Produces ("application/json")
@Path("/GetContentFragments")
public String GetContentFragments (@QueryParam ("bookid") String id,
											   @QueryParam ("sessionToken") String token,
											   @QueryParam ("type") String type, 
											   @QueryParam ("refIds") String refIds);									   	   

@GET
@Produces ("application/json")
@Path("/GetContentUrls")
public String GetContentContentUrls (@QueryParam ("sessionToken") String token,
									 @QueryParam ("type") String type); 


@GET
@Produces ("application/json")
@Path("/GetWordDef")
public String GetWordDef (@QueryParam ("v") String version, @QueryParam ("w") String aWord); 

}



