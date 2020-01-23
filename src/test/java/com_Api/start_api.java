package com_Api;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import static io.restassured.RestAssured.given;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class start_api {
	
	static ExtentReports Report;
	static ExtentTest Test;
	String first_film_id;
	String film_session_id;
	String[] seats;
	String order_id;
	
	@BeforeClass
	public static void extent()
	{
		Date currentDate = new Date();
		Report = new ExtentReports(System.getProperty("user.dir")+"/Report/"+currentDate+".html");
		Test = Report.startTest("Strike and Reel API Report");

	}
	
	public String basic_auth()
	{	
    RestAssured.baseURI = "https://stg.strikeandreel.com";
    Response response = null;

    String validusername="sathish.palanisamy@influx.co.in";
    String validpassword="Sathish@123";
    
    response = given().contentType("application/json")
            .auth().basic(validusername, validpassword)
            .when()
            .post("/user/v1/token");
    
    JsonPath json_path=new JsonPath(response.asString());
    String access_Token = json_path.getString("accessToken");
	//System.out.println("Access_token: "+access_Token); 
    if(response.getStatusCode()==200)
    {
	    Test.log(LogStatus.PASS, "Access token passed");
    }
    else
    {
    	Test.log(LogStatus.FAIL, "Access token Failed");
    }
	return access_Token;
	}
	
	
	@Test(priority=0)
	public void films()
	{
	String token=basic_auth();
	Response response1 = null;
	response1 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films");
	
    String Film_response= response1.asString();
	JsonPath json_path=new JsonPath(Film_response);	
    List<String> film_ids = json_path.getList("filmid");
    first_film_id = film_ids.get(0);
	System.out.println("Film_particular_id: "+first_film_id);
	
	if(response1.getStatusCode()==200)
	{
	Test.log(LogStatus.PASS, "Film endpoint is passed");
	}
	else
	{
		Test.log(LogStatus.FAIL, "Film endpoint is Failed");
	}
    }
	
	
	public String distinct_showdates()
	{
	String token=basic_auth();
	Response response1 = null;
	
	response1 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/distinctshowdates");

	    String distinctDate_response= response1.asString();
		JsonPath json_path=new JsonPath(distinctDate_response);	
	    List<String> dates = json_path.getList("groupValue");
		String particularDates = dates.get(0);
		System.out.println("Particular date: "+particularDates);
		String Date_only=particularDates.split("T", 0)[0];
		System.out.println("Date_only: "+Date_only);

		
	int val=response1.getStatusCode();
	if(val==200) 
	{
		Test.log(LogStatus.PASS, "Distinct Showdates is passed");
	}
	else
	{
		Test.log(LogStatus.FAIL, "Distinct Showdates is Failed");

	}
	
	return Date_only;

    }
	
	@Test(priority=1)
	public void Showtimes()
	{
        String currentDateString = distinct_showdates();
        String token=basic_auth();
		Response response2 = null;
		
		response2 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/cms/v1/sessionsbyexperience?showdate="+currentDateString);
		
		if(response2.getStatusCode()==200)
		{
		   Test.log(LogStatus.PASS, "Showtime is passed");
		}
		else
		{
		   Test.log(LogStatus.FAIL, "Showtime is Failed");
		}

	}
	
	@Test(priority=2)
	public void Session_id()
	{
		String token=basic_auth();
		//String film_id=films();
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
        String currentDateString = dateformat.format(currentDate);
		Response response2 = null;
		
		response2 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/sessionsbyexperience?showdate="+currentDateString);
		
		String FilmByExperience = response2.asString();
		
		film_session_id=JsonPath.with(FilmByExperience).get("sessionsbyexperience[0].experiences[0].sessions[0].sessionid");
		System.out.println("sessionid: "+film_session_id);
		
		if(response2.getStatusCode()==200)
		{
		Test.log(LogStatus.PASS, "FilmByExperience is passed");
		}
		else
		{
		Test.log(LogStatus.FAIL, "FilmByExperience is Failed");
		}

	}
	
	@Test(priority=3)
	public void seat_plan()
	{
		String token=basic_auth();
		//String session_id_1=Session_id();
		Response response3 = null;
		
		response3 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/order/v1/seats?sessionid="+film_session_id);
		
		System.out.println("seat_plan: "+response3.asString());
		String data= response3.asString();
		String ticket_code=JsonPath.with(data).get("tickettypes[0].code[0]");
		System.out.println("ticketcode: "+ticket_code);
		String ticket_type_name=JsonPath.with(data).get("tickettypes[0].description[0]");
		System.out.println("ticket_type_name: "+ticket_type_name);

		float price_val=JsonPath.with(data).get("tickettypes[0].price[0]");
		System.out.println("price_val: "+price_val);
		String lock_code = null;

		int i;
		for(i=0;i<10;i++)
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
		
		if(response3.getStatusCode()==200) 
		{		
		Test.log(LogStatus.PASS, "Seat plan endpoint is passed");
		}
		else
		{
			Test.log(LogStatus.FAIL, "Seat plan endpoint is Failed");
		}
	  }
	
	@Test(priority=4)
	public void create_order()
	{
		String token=basic_auth();
		//String session_id_1=Session_id();
		//String[] ticket_type=seat_plan();
		String body_specification = "{\"sessionid\":\""+film_session_id+"\","
				+ "\"fullname\":\"sathish kumar\","
				+ "\"email\":\"sathish.palanisamy@influx.co.in\","
				+ "\"phonenumber\":\"1231231\","
				+ "\"tickettypes\":[{\"code\":\""+seats[0]+"\","
				+ "\"quantity\":1}],\"seats\":[\""+seats[1]+"\"],\"fnb\":[]}";
		
		//System.out.println("values:"+body_specification);
		
		Response response4 = null;
		
		response4 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.body(body_specification).when().request(Method.POST,"/order/v1/orders");
		
	    String create_order = response4.asString();
	    System.out.println(create_order);
	    
	    String order_id=JsonPath.with(create_order).get("orders_items[0].id[0]");
		System.out.println("Order_id "+order_id);	
	    
	    
	    if(response4.getStatusCode()==200)
	    {
		Test.log(LogStatus.PASS, "Order creation passed");
	    }
	    else
	    {
		Test.log(LogStatus.FAIL, "Order creation Failed");
	    }

	}
	
	@Test(priority=5)
	public void cancel_order()
	{
		String token=basic_auth();
        Response response5 = null;
		
		response5 = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.DELETE,"/order/v1/orders/"+order_id);
		
		if(response5.getStatusCode()==200)
	    {
		Test.log(LogStatus.PASS, "Order Cancelled");
	    }
	    else
	    {
			Test.log(LogStatus.FAIL, "Order Cancellation Failed");
	    }
		
	}
	
	@AfterClass
	public static void extentend()
	{
		Report.endTest(Test);
		Report.flush();
	}
	
}


