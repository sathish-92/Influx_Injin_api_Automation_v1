package com_Api;

import org.testng.annotations.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.API.utilities.utilies;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class start_api {
	
	utilies readconfig = new utilies();
	
	static ExtentReports Report;
	static ExtentTest Test;
	String first_film_id;
	String film_session_id;
	String[] seats;
	String order_id;
	Response response;
	
	@BeforeTest
	public static void extent()
	{
		SimpleDateFormat dateformat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date currentDate = new Date();
        String currentDateString = dateformat.format(currentDate);
		Report = new ExtentReports(System.getProperty("user.dir")+"/Report/"+currentDateString+".html");
		Test = Report.startTest("Strike and Reel API Report");
	}
	
	public String basic_auth()
	{	
    RestAssured.baseURI = readconfig.baseurl();
    String validusername=readconfig.Username();
    String validpassword=readconfig.Password();
    
    response = given().contentType("application/json")
            .auth().basic(validusername, validpassword)
            .when()
            .post("/user/v1/token");
    JSONObject jsobj =new JSONObject(response.getBody().asString());
	if((response.getStatusCode()==200)&&(!jsobj.isEmpty()))
    {
	    Test.log(LogStatus.PASS, "Access token passed");
    }
    else
    {
    	Test.log(LogStatus.FAIL, "Access token Failed: "+response.getBody().asString());
    }
    
    JsonPath json_path=new JsonPath(response.asString());
    String access_Token = json_path.getString("accessToken");
    
	return access_Token;
	}
	
	
	@Test(priority = 0)
	public void films()
	{
	String token=basic_auth();
	
	response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films");
	
	String resbod = response.getBody().asString();
	JSONArray JSONResponseBody = new   JSONArray(resbod);
	if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	{
	    Test.log(LogStatus.PASS, "Film endpoint is passed");
	}
	else
	{
		Test.log(LogStatus.FAIL, "Film endpoint is Failed:  "+response.getBody().asString());
	}
	
    String Film_response= response.asString();
	JsonPath json_path=new JsonPath(Film_response);	
    List<String> film_ids = json_path.getList("id");
    first_film_id = film_ids.get(0);
	System.out.println("Film_particular_id: "+first_film_id);
    }
	
	
	public String distinct_showdates()
	{
	String token=basic_auth();
	
	response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/distinctshowdates");
	
	String resbod = response.getBody().asString();
	JSONArray JSONResponseBody = new   JSONArray(resbod);
	if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	{
		Test.log(LogStatus.PASS, "Distinct Showdates is passed");
		
	}
	else
	{
		Test.log(LogStatus.FAIL, "Distinct Showdates is Failed:  " + response.getBody().asString());
	}
	
	    String distinctDate_response= response.asString();
		JsonPath json_path=new JsonPath(distinctDate_response);	
	    List<String> dates = json_path.getList("groupValue");
		String particularDates = dates.get(0);
		System.out.println("Particular date: "+particularDates);
		String Date_only=particularDates.split("T", 0)[0];
		System.out.println("Date_only: "+Date_only);

	return Date_only;
    }
	
	
	@Test(priority = 1)
	public void Showtimes()
	{
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
        String currentDateString = dateformat.format(currentDate);
        
        String token=basic_auth();
		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/cms/v1/sessionsbyexperience?showdate="+currentDateString);
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new   JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
		{
		   Test.log(LogStatus.PASS, "Showtime is passed");
		}
		else
		{
		   Test.log(LogStatus.FAIL, "Showtime is Failed:   "+ response.getBody().asString());
		}

	}
	
	@Test(priority = 2)
	public void Session_id()
	{
		String currentDateString = distinct_showdates();
		String token=basic_auth();

		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/sessionsbyexperience?showdate="+currentDateString);
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new   JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
		{
		Test.log(LogStatus.PASS, "FilmByExperience is passed");
		}
		else
		{
		Test.log(LogStatus.FAIL, "FilmByExperience is Failed:    "+ response.getBody().asString());
		}
		
		String FilmByExperience = response.asString();
		
		film_session_id=JsonPath.with(FilmByExperience).get("sessionsbyexperience[0].experiences[0].sessions[0].sessionid");
		System.out.println("sessionid: "+film_session_id);
	}
	
	@Test(priority = 3)
	public void seat_plan()
	{
		String token=basic_auth();		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/order/v1/seats?sessionid="+film_session_id);
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new   JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
		{		
		    Test.log(LogStatus.PASS, "Seat plan endpoint is passed");
		}
		else
		{
			Test.log(LogStatus.FAIL, "Seat plan endpoint is Failed:  " + response.getBody().asString());
		}
		
		System.out.println("seat_plan: "+response.asString());
		String data= response.asString();
		String ticket_code=JsonPath.with(data).get("tickettypes[0].code[0]");
		System.out.println("ticketcode: "+ticket_code);
		String ticket_type_name=JsonPath.with(data).get("tickettypes[0].description[0]");
		System.out.println("ticket_type_name: "+ticket_type_name);

		float price_val=JsonPath.with(data).get("tickettypes[0].price[0]");
		System.out.println("price_val: "+price_val);
		String lock_code = null;

		int i;
		for(i=0;i<20;i++)
		{
		String Status_code=JsonPath.with(data).get("rowdefs[0].seats[0].status["+i+"]");
		System.out.println("Seat Status: "+Status_code);
		if (Status_code.equalsIgnoreCase("Empty"))
		{
		lock_code=JsonPath.with(data).get("rowdefs[0].seats[0].lockcode["+i+"]");
		System.out.println("lockcode: "+lock_code);	
		String seat_name=JsonPath.with(data).get("rowdefs[0].seats[0].name["+i+"]");
		System.out.println("seat_name: "+seat_name);	
		break;
		}
	  }
		seats=new String[2];
		seats[0] = ticket_code;
		seats[1] = lock_code;
	  }
	
	@Test(priority = 4)
	public void create_order() throws IOException, ParseException
	{
		String token=basic_auth();
		//String body_specification = "{\"sessionid\":\""+film_session_id+"\",\"fullname\":\"sathish kumar\",\"email\":\"sathish.palanisamy@influx.co.in\",\"phonenumber\":\"1231231\",\"tickettypes\":[{\"code\":\""+seats[0]+"\",\"quantity\":1}],\"seats\":[\""+seats[1]+"\"],\"fnb\":[]}";

		JSONObject jsonobject = new JSONObject(readconfig.body_specficaton());	
		jsonobject.remove("sessionid");
		jsonobject.put("sessionid",film_session_id);
		
		JSONArray arr = jsonobject.getJSONArray("tickettypes");
		JSONObject tic = (JSONObject)arr.get(0);
		tic.remove("code");
		tic.put("code", seats[0]);
	
		JSONArray arr1 = jsonobject.getJSONArray("seats");
		arr1.remove(0);
		arr1.put(0, seats[1]);
		
		String bodies = jsonobject.toString();

		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.body(bodies).when().request(Method.POST,"/order/v1/orders");
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new   JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	    {
		Test.log(LogStatus.PASS, "Order creation passed");
	    }
	    else
	    {
		Test.log(LogStatus.FAIL, "Order creation Failed:   "+response.getBody().asString());
	    }
		
	    String create_order = response.asString();
	    System.out.println(create_order);
	    
	    String order_id=JsonPath.with(create_order).get("orders_items[0].id[0]");
		System.out.println("Order_id "+order_id);	
	}
	
	@Test(priority = 5)
	public void cancel_order()
	{
		String token=basic_auth();
		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.DELETE,"/order/v1/orders/"+order_id);
		
		if(response.getStatusCode()==200)
	    {
		    Test.log(LogStatus.PASS, "Order Cancelled");
	    }
	    else
	    {
			Test.log(LogStatus.FAIL, "Order Cancellation Failed:   " +response.getBody().asString());
	    }
		
	}
		
	@AfterTest
	public static void extentend()
	{
		Report.endTest(Test);
		Report.flush();
	}
	
}


