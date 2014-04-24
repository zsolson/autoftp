package com.olson.autoftp;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.junit.Test;

public class FileChangeDetectorTest {

	@Test
	public void test() 
	{
		FileChangeDetector changeDetector = new FileChangeDetector();
		changeDetector.setRootDirectory("C:\\Users\\Zack\\Desktop\\FIleChangeTest");
		Calendar calLastCheck = GregorianCalendar.getInstance();
		calLastCheck.add(Calendar.DAY_OF_YEAR, -1);
		Collection<String> collChangedFiles = changeDetector.findChangesAfter(calLastCheck);
	}

}
