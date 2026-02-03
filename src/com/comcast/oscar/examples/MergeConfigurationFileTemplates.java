package com.comcast.oscar.examples;

import java.io.File;

import com.comcast.oscar.buildbulk.MergeBulkBuild;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.test.TestDirectoryStructure;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */


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
