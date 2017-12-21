package com.appirio.service.terms.manager;

/**
 * The DocusignHandlerException is thrown by the DocusignHandler fo the handling errors.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class DocusignHandlerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The isTemporary field
     */
    private final boolean isTemporary; 
    
    /**
     * Create DocusignHandlerException
     *
     * @param message the message to use
     * @param isTemporary the isTemporary to use
     */
    public DocusignHandlerException(String message, boolean isTemporary) {
        super(message);
        this.isTemporary = isTemporary;
    }
    
    /**
     * Create DocusignHandlerException
     *
     * @param message the message to use
     * @param exp the exp to use
     * @param isTemporary the isTemporary to use
     */
    public DocusignHandlerException(String message, Exception exp, boolean isTemporary) {
        super(message, exp);
        this.isTemporary = isTemporary;
    }

    /**
     * Get isTemporary.
     * @return the isTemporary. 
     */
    public boolean isTemporary() {
        return this.isTemporary;
    }
}
