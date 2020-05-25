package haloDoc.qa.BaseClass;

import haloDoc.qa.constants.Constants;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

public class BaseTest {

    /**
     * Written By : Pranshu Jain
     *
     * This is a base class for API Automation, this class can be used to quickly automate any API's using @test
     * annotation in the child class.
     *
     * This Base class uses Restassured, TestNG libraries.
     *
     */

    public static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    public static RequestSpecification requestSpecification = given();

    @BeforeSuite
    @Parameters({"authType","username","password"})
    public void beforeSuite(@Optional String authType, @Optional String username, @Optional String password) {
        if(authType!=null){
            if(authType.equalsIgnoreCase(Constants.PREEMPTIVE)){
                requestSpecification.auth().preemptive().basic(username,password);
                logger.info("Setting preemptive basic auth");
            } else if(authType.equalsIgnoreCase(Constants.NON_PREEMPTIVE)){
                requestSpecification.auth().basic(username,password);
                logger.info("Setting non preemptive basic auth");
            }
        }else{
            logger.info("No authentication was set");
        }
    }

    @BeforeTest
    @Parameters({"baseURI"})
    public void beforeTest(@Optional String baseURI) {
        if (baseURI != null) {
            requestSpecification.baseUri(baseURI);
        }
    }

    @BeforeMethod
    @Parameters({"basePath"})
    public void beforeMethod(@Optional String basePath) {
        if (basePath != null) {
            requestSpecification.basePath(basePath);
        }
    }
}
