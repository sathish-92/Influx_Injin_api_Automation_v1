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
	
	public String access_token_body() throws IOException {

		   String jsonBody = generateStringFromResource("./Config/access_token.json");
		   return jsonBody;
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
	
	public String intitate_pay_body() throws IOException {

		   String jsonBody = generateStringFromResource("./Config/Initiate_pay_body.json");
		   return jsonBody;
	}
	
	public String update_user_body() throws IOException {

		   String jsonBody = generateStringFromResource("./Config/Update_user_payload.json");
		   return jsonBody;
	}
    
	public String cineapp_username()
	{
		String user_name1 = pro.getProperty("cine_app_username");
		return user_name1;
	}
	
	public String cineapp_Password()
	{
		String pass1 = pro.getProperty("cine_app_password");
		return pass1;
	}
	
}
