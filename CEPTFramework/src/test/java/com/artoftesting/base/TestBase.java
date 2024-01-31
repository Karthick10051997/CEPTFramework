package com.artoftesting.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;


/**
* The TestBase class is the base class to fetch environment specific configuration parameters from 
* Jenkins/Maven. Based on the parameters, it performs the browser setup and tear-down functions.
* 
*
*/


public class TestBase {
	
	protected static WebDriver driver;
	public static Properties envConfig;
	WebDriverWait wait;
	public static ExtentReports extentReport ;
	public static ExtentSparkReporter htmlReporter ;
	public static ExtentTest testcase ;
	//To get detail which mentioned in testng annotation
	TestDetail tc ;
	// It is used to store string passed in Screenshot method
	ArrayList<String> Logs = new ArrayList<>();
	// It is used to store path of screenshot passed in Screenshot method
	ArrayList<String> ss_paths = new ArrayList<>();
	// It is used to store path of screenshot passed in Screenshot method
	String SS_Path;

	
	
	
	//Environment value fetched from POM with 'careersIn' and 'production' being the valid values 
	public static final String ENV = System.getProperty("env", "dev");

	//BROWSER value fetched from POM with Chrome being the default value 
	private static final String BROWSER = System.getProperty("browser", "Chrome");
	
		
	//Automation suite setup method to configure and instantiate a particular browser
	@BeforeSuite
    public void suiteSetup() throws Exception {
		
		//Browser configuration - can add more browsers and remote driver here
		if (BROWSER.equals("Firefox")) {
			WebDriverManager.firefoxdriver().setup(); //can also use set property method for browser executables
			driver = new FirefoxDriver();
         }
		else if (BROWSER.equals("Chrome")) {
			String s = System.getProperty("user.dir") +"driver\\chromedriver.exe";
			System.out.println(s);
			System.setProperty("webdriver.gecko.driver", s);
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--remote-allow-orgins=*");
			 driver = new ChromeDriver(options);
			            
         }
		 else if (BROWSER.equals("IE")) {
			 WebDriverManager.iedriver().setup();
             driver = new InternetExplorerDriver();
         }
		 else {
             throw new RuntimeException("Browser type unsupported");
         }
		
		//Setting implicit wait
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		driver.manage().window().maximize();
		
		//Setting WebDriverWait with max timeout value of 20 seconds
		wait = new WebDriverWait(driver, 20);

		//Environment specific properties file loading
		
		InputStream configFile = new FileInputStream(System.getProperty("user.dir") + 
				"\\src\\test\\java\\com\\artoftesting\\config\\" + ENV +  ".properties");		 
		envConfig = new Properties();
		envConfig.load(configFile);
		// It is used to create a testng extended report
		extentReport = new ExtentReports();
		// It is used to create a html file in mentioned below location 
		htmlReporter = new  ExtentSparkReporter("./CEPT.html");
		extentReport.attachReporter(htmlReporter);
		
		// It is used to delete the log folder
		 String folderPath = "C:\\Users\\acer\\git\\booking\\log";

	        // Create a File object for the folder
	        File folder = new File(folderPath);

	        // Check if the folder exists
	        if (folder.exists()) {
	            // Delete the folder and its contents
	            deleteFolder(folder);
	            System.out.println("Folder deleted successfully.");
	        } else {
	            System.out.println("Folder does not exist.");
	        }
	
	}
   
    
    
  

	
	@BeforeMethod()
    public void loadBaseUrl(Method method) {
        driver.get(envConfig.getProperty("baseUrl"));
        
    	tc = method.getAnnotation(TestDetail.class);
    	
    	
    		testcase= extentReport.createTest(tc.testCaseName());
    	
        testcase.assignAuthor(tc.author());
        testcase.assignCategory(tc.category());
        testcase.info("Testcase ID -"+tc.testCaseID());
        testcase.info("Module Name -"+tc.module());
      
    }
 

	@AfterMethod
	public void screenshotAndDeleteCookies(ITestResult testResult ,Method method) throws IOException {
		
		String path;
		
		
		
		
//		switch(testResult.getStatus())
//		{
//		
//		case ITestResult.SUCCESS:
//			for(int i=0;i<Logs.size();i++)
//			{	
//				testcase.pass(Logs.get(i),MediaEntityBuilder.createScreenCaptureFromPath(ss_paths.get(i)).build());
//			
//			}
//			
//			break;
//		case ITestResult.FAILURE:
//			for(int i=0;i<Logs.size();i++)
//			{
//			testcase.fail(Logs.get(i),MediaEntityBuilder.createScreenCaptureFromPath(ss_paths.get(i)).build());
//			
//			}break;
//		}
//	  
		for(int i=0;i<Logs.size();i++)
		{	
			testcase.pass(Logs.get(i),MediaEntityBuilder.createScreenCaptureFromPath(ss_paths.get(i)).build());
		
		}
		if(testResult.getStatus() == ITestResult.FAILURE){
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("errorScreenshots\\" + testResult.getName() + "-" 
					+ Arrays.toString(testResult.getParameters()) +  ".png"));	
			path ="errorScreenshots\\" + testResult.getName() + "-" 
					+ Arrays.toString(testResult.getParameters()) +  ".png";
			
			testcase.fail("fail",MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			}
		 
        Logs.clear();
        ss_paths.clear();
        
	}
	
	/**
	 * @
	 * It is used store screenshot in array 
	 * @param Log message need to be shown in report with screenshots
	 * @param Number of second to be delayed . It can be zero,if delays is not needed
	 * 
	 */
 
     public void Screenshot(String log,int Seconds) throws InterruptedException 
   {
    	 if(Seconds!=0)
    	 {
    		 Thread.sleep(Seconds*1000);
    	 }
    	
    	 File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(scrFile, new File("logs\\" +log
						 +".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			SS_Path ="logs\\"+ log +".png";
			
	Logs.add(log);
	ss_paths.add(SS_Path);
   
   }
     
    
    @AfterSuite
    public void suiteTearDown() {
    	extentReport.flush();
    	driver.quit();
    }
    
    
    /**
     * 
     * It is method to delete the folder
     * @param  Folder location need to be deleted
     */
    
    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            // List all files and subdirectories in the folder
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Recursively delete files and subdirectories
                    deleteFolder(file);
                }
            }
        }

        // Delete the folder itself
        folder.delete();
    }

}
