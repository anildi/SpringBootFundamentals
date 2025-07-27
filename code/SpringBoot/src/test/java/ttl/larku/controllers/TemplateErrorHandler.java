package ttl.larku.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Template ErrorHandler.  This one lets all errors through so they
 * can be handled by the Client
 *
 * @author whynot
 */

@Component
public class TemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return false;
        //return (httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {
        //Do nothing
	    	/*
	        if (httpResponse.getStatusCode()
	          .series() == HttpStatus.Series.SERVER_ERROR) {
	        } else if (httpResponse.getStatusCode()
	          .series() == HttpStatus.Series.CLIENT_ERROR) {
	            switch(httpResponse.getStatusCode()) {
	            	case NOT_FOUND:  
	            		break;
	            }
	        }
	        */
    }
}
