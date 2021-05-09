package com_Api;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.API.utilities.utilies;

import io.restassured.http.Method;
import io.restassured.response.Response;

public class User_jounery_api {
	
	utilies readconfig = new utilies();
	Response response;
	String[] statuses;
	
	String validusername=readconfig.cineapp_username();
    String validpassword=readconfig.cineapp_Password();
    
	
	public int update_user_api()throws IOException, ParseException
	{
		
		JSONObject jsonobject = new JSONObject(readconfig.update_user_body());
     	String email_id_val=jsonobject.getString("username");
	    System.out.println(email_id_val);
	    String up_get_values = jsonobject.toString();
	    
	    response = given().contentType("application/json")
	            .auth().basic(validusername, validpassword).body(up_get_values)
	            .when()
	            .put("/user/v1/userdetails?username="+email_id_val);
		
		String resbod= response.asString();
		System.out.println(resbod);
		int codes = response.getStatusCode();
		System.out.println(codes);
		
		return codes;

	}
	
	public int get_user_details()throws IOException, ParseException
	{
		JSONObject jsonobject = new JSONObject(readconfig.update_user_body());
     	String email_id_val1=jsonobject.getString("username");
	    System.out.println(email_id_val1);
	    
	    response = given().contentType("application/json")
	            .auth().basic(validusername, validpassword)
	            .when().get("/user/v1/userdetails?username="+email_id_val1);
		
		String resbod1= response.asString();
		System.out.println(resbod1);
		int codes1 = response.getStatusCode();
		System.out.println(codes1);
		
		return codes1;

	}

}
