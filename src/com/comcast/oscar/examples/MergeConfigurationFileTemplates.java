package com.comcast.oscar.examples;

import java.io.File;

import com.comcast.oscar.buildbulk.MergeBulkBuild;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.test.TestDirectoryStructure;

public class MergeConfigurationFileTemplates {

	public static void main(String[] args) {
		
		MergeBulkBuild mbb = new MergeBulkBuild(ConfigurationFile.DOCSIS_VER_31,
												new File(TestDirectoryStructure.fTestDirPath+File.separator+"output"),
												MergeBulkBuild.TEXTUAL_FILE_OUTPUT,
												"SHARED_SECRET_PASSWORD");
		/* Set Separation token */
		mbb.NOMENCLATURE_SEPERATOR = "_";
		
		/* MUST ADD IN THIS ORDER -> Model_Tier_CPE */
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Model"));
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Tier"));
		mbb.addInputDirectory(new File(TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "CPE"));

		mbb.start();
	}
}
