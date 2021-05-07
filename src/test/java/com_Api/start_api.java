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
import java.text.DecimalFormat;
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
	int price_val;
	int tax_val;
	int quantity_value_spec;
	String payement_intiate_id;
	String user_update_res;
	@BeforeTest
	public static void extent()
	{
		SimpleDateFormat dateformat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date currentDate = new Date();
        String currentDateString = dateformat.format(currentDate);
		Report = new ExtentReports(System.getProperty("user.dir")+"/Report/"+currentDateString+".html");
		Test = Report.startTest("CMX INJIN API REPORT");
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
	    Test.log(LogStatus.PASS, "Access token is generated");
    }
    else
    {
    	Test.log(LogStatus.FAIL, "Access token Failed: "+response.getBody().asString());
    }
    
    JsonPath json_path=new JsonPath(response.asString());
    String access_Token = json_path.getString("accessToken");
    
	return access_Token;
	}
	
	
	
	public String films()
	{
	String token=basic_auth();
	
	response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0").queryParam("nowshowing", "1")
			.when().request(Method.GET,"/cms/v1/films");
	
	String resbod = response.getBody().asString();
	JSONArray JSONResponseBody = new   JSONArray(resbod);
	if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	{
	    Test.log(LogStatus.PASS, "Film endpoint is executed and verified Successfully");
	}
	else
	{
		Test.log(LogStatus.FAIL, "Film endpoint is Failed:  "+response.getBody().asString());
	}
	
    String Film_response= response.asString();
	JsonPath json_path=new JsonPath(Film_response);	
    List<String> film_ids = json_path.getList("id");
    int siz = film_ids.size();
    System.out.println(siz);
    String Sheduled_Date_only=null;
    int i;
    for(i=0;i<siz;i++)
    {
		String movie_name=JsonPath.with(Film_response).get("title["+i+"]");
    	System.out.println("Film["+i+"]: "+movie_name);
    	first_film_id = film_ids.get(i);
   
	String token1=basic_auth();
	
	response = given().contentType("application/json").headers("Authorization","Bearer "+token1,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/sessions");
	
	String resbod1 = response.getBody().asString();
	System.out.println("err");
	JSONArray JSONResponseBody1 = new   JSONArray(resbod1);
	if((response.getStatusCode()==200)&&(!(JSONResponseBody1.isEmpty()))&&(!(resbod1.isEmpty())))
	{
		String ShedDate_response= response.asString();
		JsonPath json_path1=new JsonPath(ShedDate_response);	
	    List<String> dates = json_path1.getList("showtime");
		String particularsheduledDates = dates.get(0);
		System.out.println("Particularsheduled date: "+particularsheduledDates);
		Sheduled_Date_only=particularsheduledDates.split("T", 0)[0];
		System.out.println("Sheduled Date_only: "+Sheduled_Date_only);
		
		Test.log(LogStatus.PASS, "Sheduled sessions endpoint is executed and verified Successfully");
		break;
	}
	
    }  
	return Sheduled_Date_only;
    }
	
	/*public String distinct_showdates()
	{
	String token=basic_auth();
	
	response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
			.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/distinctshowdates");
	
	String resbod = response.getBody().asString();
	JSONArray JSONResponseBody = new   JSONArray(resbod);
	if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	{
		Test.log(LogStatus.PASS, "Distinct Showdates endpoint is executed and verified Successfully");
		
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
	*/
	
//	@Test(priority = 1)
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
		   Test.log(LogStatus.PASS, "Showtime endpoint is executed and verified Successfully");
		}
		else
		{
		   Test.log(LogStatus.FAIL, "Showtime is Failed:   "+ response.getBody().asString());
		}

	}
	
	@Test(priority = 0)
	public void Session_id()
	{
		String currentDateString = films();
		String token=basic_auth();

		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/cms/v1/films/"+first_film_id+"/sessionsbyexperience?showdate="+currentDateString);
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
		{
        String FilmByExperience = response.asString();
		
		film_session_id=JsonPath.with(FilmByExperience).get("sessionsbyexperience[0].experiences[0].sessions[0].id");
		System.out.println("sessionid: "+film_session_id);
		
		Test.log(LogStatus.PASS, "FilmByExperience endpoint is executed and verified Successfully");
		}
		else
		{
		Test.log(LogStatus.FAIL, "FilmByExperience is Failed:    "+ response.getBody().asString());
		}
		
	}
	
	@Test(priority = 1)
	public void seat_plan()
	{
		String token=basic_auth();	
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/order/v1/seats?sessionid="+film_session_id);
		
		String resbod = response.getBody().asString();
		JSONArray JSONResponseBody = new JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
		{		
			String data= response.asString();
			String ticket_code=JsonPath.with(data).get("tickettypes[0].code[0]");
			System.out.println("ticketcode: "+ticket_code);
			String ticket_type_name=JsonPath.with(data).get("tickettypes[0].description[0]");
			System.out.println("ticket_type_name: "+ticket_type_name);

			price_val=JsonPath.with(data).get("tickettypes[0].price[0]");
			System.out.println("price_val: "+price_val);
			tax_val=JsonPath.with(data).get("tickettypes[0].tax[0]");
			System.out.println("Tax: "+tax_val);
			String lock_code = null;
			List<String> seatsize=JsonPath.with(data).get("rowdefs[0].seats");
			int ss = seatsize.size();		

			outerloop:
			for(int i=0;i<ss;i++)
			{	
			List<String> statussize=JsonPath.with(data).get("rowdefs[0].seats["+i+"].status");
			int sss = statussize.size();
			
			for(int j=0;j<sss;j++) {
			String Status_code=JsonPath.with(data).get("rowdefs[0].seats["+i+"].status["+j+"]");
			
			if (Status_code.equalsIgnoreCase("Empty"))
			{
			lock_code=JsonPath.with(data).get("rowdefs[0].seats["+i+"].lockcode["+j+"]");
			System.out.println("lockcode: "+lock_code);	
			String seat_name=JsonPath.with(data).get("rowdefs[0].seats["+i+"].name["+j+"]");
			System.out.println("seat_name: "+seat_name);	
			break outerloop;
			}
			}
		  }
			seats=new String[2];
			seats[0] = ticket_code;
			seats[1] = lock_code;
			
			
			
		    Test.log(LogStatus.PASS, "Seat plan endpoint endpoint is executed and verified Successfully");
		    
		}
		else
		{
			Test.log(LogStatus.FAIL, "Seat plan endpoint is Failed:  " + response.getBody().asString());
		}
		
		
	  }
	
	@Test(priority = 2)
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
		
		quantity_value_spec= tic.getInt("quantity");
		System.out.println("quantity_value:"+quantity_value_spec);
		
		JSONArray arr1 = jsonobject.getJSONArray("seats");
		arr1.remove(0);
		arr1.put(0, seats[1]);
		
		String bodies = jsonobject.toString();

		System.out.println(bodies);
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.body(bodies).when().request(Method.POST,"/order/v1/orders");
		
		String resbod = response.getBody().asString();
		System.out.println(resbod);
		JSONArray JSONResponseBody = new   JSONArray(resbod);
		if((response.getStatusCode()==200)&&(!(JSONResponseBody.isEmpty()))&&(!(resbod.isEmpty())))
	    {
		    order_id=JsonPath.with(resbod).get("id[0]");
			System.out.println("Order_id "+order_id);
			
		Test.log(LogStatus.PASS, "Order creation endpoint is executed and verified Successfully");
	    }
	    else
	    {
		Test.log(LogStatus.FAIL, "Order creation Failed:   "+response.getBody().asString());
	    }
		
		
		List<String> sizeofgrouping=JsonPath.with(resbod).get("order_grouping");
		int n = sizeofgrouping.size();
		float pric_val=0;
		int quant_val=0;
		for(int i=0;i<n;i++)
		{
			
			 pric_val= JsonPath.with(resbod).get("order_grouping["+i+"].pricebeforetax["+i+"]");
			 System.out.println(pric_val);
			 quant_val = JsonPath.with(resbod).get("order_grouping["+i+"].quantity["+i+"]");
			 System.out.println(quant_val);
		}
		float tax_dec=tax_val+100;
		float prc_quan = price_val*quantity_value_spec;
		float subtotal = (prc_quan/tax_dec)*100;
		DecimalFormat df_obj = new DecimalFormat("#.##");
	    float rounding_price_before_tax=Float.parseFloat(df_obj.format(subtotal));
		System.out.println("Subtotal:"+rounding_price_before_tax);
		float tax_amt= price_val-rounding_price_before_tax;
		float rounding_tax=Float.parseFloat(df_obj.format(tax_amt));
		System.out.println("Tax amount:"+rounding_tax);
		
		
		
	} 
	
	@Test(priority = 3)
	public void payment_types()
	{
		String token=basic_auth();
		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
				.when().request(Method.GET,"/checkout/v1/payment-types?orderid="+order_id);
		
		
			System.out.println("Paymennt:"+response.getBody().asString());
			String payment_types_id = response.asString();
			payement_intiate_id=JsonPath.with(payment_types_id).get("id[0]");
			System.out.println(payement_intiate_id);
			
			//JSONArray JSONResponseBody = new   JSONArray(response);
			if((response.getStatusCode()==200)&&(!(payment_types_id.isEmpty())))
			{
			    Test.log(LogStatus.PASS, "Payment type endpoint is executed and verified Successfully");
			}
			else
			{
				Test.log(LogStatus.FAIL, "Payment type is Failed:  "+response.getBody().asString());
			}
	}
	
	@Test(priority = 4)
	public void Initiate_payment()throws IOException, ParseException
	{
		String token=basic_auth();
		
		JSONObject jsonobject = new JSONObject(readconfig.intitate_pay_body());
		jsonobject.remove("orderid");
		jsonobject.put("orderid",order_id);
		
		JSONArray arr = jsonobject.getJSONArray("paymentid");
		JSONObject paym = (JSONObject)arr.get(0);
		paym.remove("id");
		paym.put("id", payement_intiate_id);
		
		String resposing = jsonobject.toString();
		
		response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0").body(resposing)
				.when().request(Method.POST,"/checkout/v1/checkout");
		
		String resbod= response.asString();
		System.out.println(resbod);
		
		if((response.getStatusCode()==200)&&(!(resbod.isEmpty())))
		{
		    Test.log(LogStatus.PASS, "Initiate_payment iendpoint is executed and verified Successfully");
		}
		else
		{
			Test.log(LogStatus.FAIL, "Initiate_payment is Failed:  "+response.getBody().asString());
		}
	}
	
	 @Test(priority = 5)
		public void cancel_order()
		{
			String token=basic_auth();
			
			response = given().contentType("application/json").headers("Authorization","Bearer "+token,"appplatform","WEBSITE","appversion","1.0.0")
					.when().request(Method.DELETE,"/order/v1/orders/"+order_id);
			
			if(response.getStatusCode()==200)
		    {
			    Test.log(LogStatus.PASS, "Order Cancelled endpoint is executed and verified Successfully");
		    }
		    else
		    {
				Test.log(LogStatus.FAIL, "Order Cancellation Failed:   " +response.getBody().asString());
		    }
			
		}
	 
//	 //@Test(priority = 6)	 
//	 public void user_journey() throws IOException, ParseException
//	 {
//		 User_jounery_api ap = new  User_jounery_api();
//		 ap.update_user_api(user_update_res);
//		 if((user_update_res.getStatusCode()==200)&&(!(user_update_res.isEmpty())))
//		 {
//			 
//		 }
//	 }
	
	@AfterTest
	public static void extentend()
	{
		Report.endTest(Test);
		Report.flush();
	}
	
}


