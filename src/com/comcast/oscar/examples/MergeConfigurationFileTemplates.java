package com.comcast.oscar.examples;

import java.io.File;
import java.io.IOException;

import com.comcast.oscar.buildbulk.MergeBulkBuild;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.utilities.DirectoryStructure;

public class MergeConfigurationFileTemplates {

	public static void main(String[] args) {

		
		MergeBulkBuild mbb = new MergeBulkBuild(ConfigurationFile.DOCSIS_VER_31,"PASSWORD");
		
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Model"));
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Tier"));
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "CPE"));

		mbb.start();
	}
}
