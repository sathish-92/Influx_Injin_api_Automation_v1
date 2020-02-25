package com.API.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class utilies {
	
	Properties pro;
	
	public utilies()
	{
		File src = new File("./Config/conf");
		try {
			FileInputStream fis = new FileInputStream(src);
			pro = new Properties();
			pro.load(fis);
		} catch (Exception e) {
			System.out.println("Exception is " + e.getMessage());
		}
		
	}
	
	
	public String baseurl()
	{
		String URL = pro.getProperty("base_url");
		return URL;
	}
	
	public String Username()
	{
		String user_name = pro.getProperty("username");
		return user_name;
	}
	
	public String Password()
	{
		String pass = pro.getProperty("password");
		return pass;
	}
	
	public String generateStringFromResource(String path) throws IOException {

	    return new String(Files.readAllBytes(Paths.get(path)));

	}
	
	public String body_specficaton() throws IOException {

		   String jsonBody = generateStringFromResource("./Config/Body_spec.json");
		   return jsonBody;
	}
	
    
}
