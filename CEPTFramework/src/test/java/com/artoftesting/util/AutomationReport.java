package com.artoftesting.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class AutomationReport {

	public static void main (String[] args) {
		ExtentSparkReporter report = new ExtentSparkReporter("./CEPT.html");
		ExtentReports extent = new ExtentReports();
		extent.attachReporter(report);
		 extent.flush();		
	}
}
