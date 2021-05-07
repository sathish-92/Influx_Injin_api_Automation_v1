package com_Api;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.API.utilities.utilies;

import io.restassured.response.Response;

public class User_jounery_api {
	
	utilies readconfig = new utilies();
	Response response;
	
	String validusername=readconfig.cineapp_username();
    String validpassword=readconfig.cineapp_Password();
	
	public String update_user_api(String res)throws IOException, ParseException
	{
		JSONObject jsonobject = new JSONObject(readconfig.update_user_body());
     	String email_id_val=jsonobject.getString("username");
	    System.out.println(email_id_val);
	   
	    response = given().contentType("application/json")
	            .auth().basic(validusername, validpassword)
	            .when()
	            .put("/user/v1/userdetails?username="+email_id_val);
		
		String resbod= response.asString();
		System.out.println(resbod);
		return resbod;

	}

}
