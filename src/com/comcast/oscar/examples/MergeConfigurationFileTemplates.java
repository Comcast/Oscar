package com.comcast.oscar.examples;

import java.io.File;
import java.io.IOException;

import com.comcast.oscar.utilities.DirectoryStructure;

public class MergeConfigurationFileTemplates {

	public static void main(String[] args) {

		File[] files = DirectoryStructure.fMibsTextDir().listFiles();
		
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
		}
		

	}
}
