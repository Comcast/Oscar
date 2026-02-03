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


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */

public class PacketCableSqlQueryException extends Exception {

	private static final long serialVersionUID = -2747516882743788218L;

	private Throwable cause;

	/**
	 * Constructor for PacketCableSqlQueryException.
	 * @param message String
	 */
	public PacketCableSqlQueryException(String message) {
        super(message);
    }
    
    /**
     * Constructor for PacketCableSqlQueryException.
     * @param cause Throwable
     */
    public PacketCableSqlQueryException (Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    /**
     * Method getCause.
     * @return Throwable
     */
    @Override
	public Throwable getCause() {
        return this.cause;
    }
	
}
