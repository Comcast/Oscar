package com.comcast.oscar.sql.queries;

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

public class DictionarySQLQueriesException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5175865963413644345L;
	
	private Throwable cause;

	/**
	 * 
	 * @param message
	 */
	public DictionarySQLQueriesException(String message) {
        super(message);
    }
    
	/**
	 * 
	 * @param cause
	 */
    public DictionarySQLQueriesException (Throwable cause) {
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
