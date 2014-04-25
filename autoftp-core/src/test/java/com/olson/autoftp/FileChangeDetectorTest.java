package com.olson.autoftp;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileChangeDetectorTest {

	@Test
	public void test() throws IOException, InterruptedException
	{
		Calendar calLastCheck = GregorianCalendar.getInstance();
		
		pause();
		
		File baseDir = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest");
		File file2 = null;
		File file4 = null;
		File file6 = null;
		if (!baseDir.exists())
		{
			baseDir.mkdirs();
			File file1 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\testFile1.txt");
			file1.createNewFile();
			file2 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\testFile2.txt");
			file2.createNewFile();
			File innerDir = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir");
			if (!innerDir.exists())
			{
				innerDir.mkdirs();
				File file3 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\testFile3.txt");
				file3.createNewFile();
				file4 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\testFile4.txt");
				file4.createNewFile();
				File innerInnerDir = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir");
				if (!innerInnerDir.exists())
				{
					innerInnerDir.mkdirs();
					File file5 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile5.txt");
					file5.createNewFile();
					file6 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile6.txt");
					file6.createNewFile();
				}
			}
		}
		
		FileChangeDetector changeDetector = new FileChangeDetector();
		changeDetector.setRootDirectory("C:\\Users\\Zack\\Desktop\\FileChangeTest");
		Collection<String> collChangedFiles = changeDetector.findChangesAfter(calLastCheck);
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\testFile1.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\testFile2.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\testFile3.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\testFile4.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile5.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile6.txt"));

		calLastCheck = GregorianCalendar.getInstance();
		pause();
		
		FileUtils.touch(file2);
		FileUtils.touch(file4);
		FileUtils.touch(file6);
		File file7 = new File("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile7.txt");
		file7.createNewFile();
		
		collChangedFiles = changeDetector.findChangesAfter(calLastCheck);
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\testFile2.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\testFile4.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile6.txt"));
		assertTrue(collChangedFiles.contains("C:\\Users\\Zack\\Desktop\\FileChangeTest\\innerDir\\innerInnerDir\\testFile7.txt"));
		
		
		
		FileUtils.deleteDirectory(baseDir);
	}
	
	private void pause() throws InterruptedException
	{
		Thread.sleep(1000);
	}

}
