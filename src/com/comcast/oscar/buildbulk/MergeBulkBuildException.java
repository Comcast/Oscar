package com.comcast.oscar.buildbulk;

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


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */


public class MergeBulkBuildException  extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8145422389479699786L;
	private Throwable cause;

	/**
	 * 
	 * @param message
	 */
	public MergeBulkBuildException(String message) {
        super(message);
    }
    
	/**
	 * 
	 * @param cause
	 */
    public MergeBulkBuildException (Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    /**
     * 
     * @return Throwable
     */
    @Override
	public Throwable getCause() {
        return this.cause;
    }
}
