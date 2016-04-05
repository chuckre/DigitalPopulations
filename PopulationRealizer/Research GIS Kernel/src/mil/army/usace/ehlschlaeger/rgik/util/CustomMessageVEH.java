package mil.army.usace.ehlschlaeger.rgik.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.DefaultValidationEventHandler;


/**
 * ValidationEventHandler for loading XML file. Called when XML parser finds a
 * problem, whereupon it prints the problem then crashes. I don't like the way
 * the default parser reports errors; this provides more useful output.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class CustomMessageVEH extends DefaultValidationEventHandler {
    String mainPath;
    
    public CustomMessageVEH() {
        mainPath = null;
    }
    
    public CustomMessageVEH(String mainFile) {
        this.mainPath = mainFile;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        String detail = null;
        if(event.getMessage() != null) {
            detail = event.getMessage();
            if(event.getLinkedException() != null) {
                String m2 = ObjectUtil.getMessage(event.getLinkedException());
                if(! detail.equals(m2))
                    detail = String.format("%s (%s)", detail, m2);
                }
        }
        else {
            if(event.getLinkedException() != null)
                detail = ObjectUtil.getMessage(event.getLinkedException());
        }

        String location = null;
        if(event.getLocator() != null) {
            String file;
            if(event.getLocator().getURL() != null)
                file = event.getLocator().getURL().toString();
            else
                file = mainPath;
            
            if(file == null)
                location = String.format("line %d, column %d",
                    event.getLocator().getLineNumber(), event.getLocator().getColumnNumber());
            else
                location = String.format("line %d, column %d in file %s",
                    event.getLocator().getLineNumber(), event.getLocator().getColumnNumber(),
                    file);
        }

        String msg = "XML error";
        if(location != null)
            msg = String.format("%s at %s", msg, location);
        if(detail != null)
            msg = String.format("%s: %s", msg, detail);
        
        boolean retval;
        switch(event.getSeverity()) {
            case ValidationEvent.ERROR:
            case ValidationEvent.FATAL_ERROR:
                retval = false;
                break;
            default:
                retval = true;
        }
        
        System.out.println(msg);
        
        return retval;
    }
}
