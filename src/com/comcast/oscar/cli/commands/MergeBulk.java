package com.comcast.oscar.cli.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.buildbulk.MergeBulkBuild;
import com.comcast.oscar.utilities.DirectoryStructure;

/**
 * 
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 * 
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

 */

public class MergeBulk {
	
	public static final String ERROR = "No input directory specified.";
	
	private final String[] args;
	private File fOutputDir = new File(DirectoryStructure.sBasePath());
	private boolean boolIsBinary = true;
	private List<File> list = new ArrayList<File>();
	private MergeBulkBuild mbb;
	
	public MergeBulk(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command Maximum CPE
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("input dirs> <o=<output dir>> <b{inary}/t{ext}");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("mergebulkbuild");
        OptionBuilder.withDescription("Merge multiple text files from directories into one binary. " +
        		"EX: -mbb inputDirectoryModel inputDirectoryTier inputDirectoryCPE o=outputDirectory text. " +
        		"Output directory and b{inary}/t{ext} are optional.");
		return OptionBuilder.create("mbb");
	}
	
	/**
	 * Check for any OID arguments
	 * @return boolean
	 */
	public boolean hasInputDir() {
		boolean boolInputDir = false;

		for (String string : this.args) {
			if (string.contains("=")) {
				String[] array = string.split("=");
				
				if (array[0].equalsIgnoreCase("o") || array[0].equalsIgnoreCase("O")) {
					this.fOutputDir = new File(array[1] + File.separator);
				}
			} 
			else if (string.equalsIgnoreCase("b")) {
				this.boolIsBinary = true;
			} 
			else if (string.equalsIgnoreCase("t")) {
				this.boolIsBinary = false;
			}
			else {
				this.list.add(new File(string));
				boolInputDir = true;
			}
		}
		
		return boolInputDir;
	}
	
	/**
	 * Merge input files into output file
	 * @param configurationFileType
	 * @param key
	 */
	public void mergeFiles(int configurationFileType, String key) {
		this.mbb = new MergeBulkBuild(configurationFileType,
			this.fOutputDir,
			this.boolIsBinary,
			key);
		
		this.mbb.NOMENCLATURE_SEPERATOR = "_";
		
		for (File file : this.list) {
			this.mbb.addInputDirectory(file);
		}
		
		this.mbb.start();
	}
	
	/**
	 * Return output directory
	 * @return
	 */
	public File getOutputDir() {
		return this.fOutputDir;
	}
	
	/**
	 * Return input files
	 * @return
	 */
	public List<File> getInputDir() {
		return this.list;
	}

}
