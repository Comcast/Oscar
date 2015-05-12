package com.comcast.oscar.buildbulk;

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
