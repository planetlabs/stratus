/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest.xml;

import lombok.Data;
import org.geoserver.security.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="requestFilterChain")
public class JaxbRequestFilterChain {

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name="filterNames")
	public static class FilterNames {

		@XmlElement(name = "filterName")
		List<String> filterNames;
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name="patterns")
	public static class Patterns {

		@XmlElement(name = "pattern")
		List<String> patterns;
	    
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name="httpMethods")
	public static class HttpMethods {

		@XmlElement(name = "httpMethod")
		Set<HTTPMethod> httpMethods;

	}
	
	public enum Type {UNKNOWN, SERVICE, HTML, LOGOUT}
	
	/**
	 * the position
	 */
	Integer position;
	
	/**
	 * the type
	 */
	Type type;
	
	/**
     * The unique name of the chain
     */
    String name;
    
    /**
     * The ANT patterns for this chain
     */
    Patterns patterns;    
    
    /**
     * The filter names
     */
    FilterNames filterNames;
    
    /**
     * Http methods
     */
    HttpMethods httpMethods;

    /**
     * Chain disabled ?
     */
    Boolean disabled;
    
    /**
     * Is this chain allowed to create an HTTP session ?
     */
    Boolean allowSessionCreation;
    
    /**
     * Does this chain accept SSL requests only 
     */
    Boolean requireSSL;
    
    /**
     * Is this chain matching individual HTTP methods 
     */
    Boolean matchHTTPMethod;

	public JaxbRequestFilterChain(RequestFilterChain filterChain) {
		type = 	filterChain instanceof HtmlLoginFilterChain ? Type.HTML 
				: filterChain instanceof ServiceLoginFilterChain ? Type.SERVICE
				: filterChain instanceof LogoutFilterChain ? Type.LOGOUT
				: Type.UNKNOWN;
		name = filterChain.getName();
		patterns = new Patterns();
		patterns.setPatterns(filterChain.getPatterns());
		filterNames = new FilterNames();
		filterNames.setFilterNames(filterChain.getFilterNames());
		httpMethods = new HttpMethods();
		httpMethods.setHttpMethods(filterChain.getHttpMethods());
		disabled = filterChain.isDisabled();
		allowSessionCreation = filterChain.isAllowSessionCreation();
		requireSSL = filterChain.isRequireSSL();
		matchHTTPMethod = filterChain.isMatchHTTPMethod();
	}	

	public RequestFilterChain save() {
		RequestFilterChain filterChain;
		switch (type) {
		case HTML:
			filterChain = new HtmlLoginFilterChain();
			break;
		case SERVICE:
			filterChain = new ServiceLoginFilterChain();
			break;
		default:
			throw new IllegalStateException("Cannot create request filter chain of type " + type);
		}
		return save(filterChain);
	}
    
	public RequestFilterChain save(RequestFilterChain filterChain) {
		if (filterChain.isConstant()) {
			throw new IllegalStateException("Cannot modify filter chain");
		}
		
		if (type != null) {
			Type expectedType = filterChain instanceof HtmlLoginFilterChain ? Type.HTML 
					: filterChain instanceof ServiceLoginFilterChain ? Type.SERVICE
					: filterChain instanceof LogoutFilterChain ? Type.LOGOUT
					: Type.UNKNOWN;
			if (expectedType != type) {
				throw new IllegalStateException("Specified type doesn't match filter chain.");
			}
		}
		
		if (name != null) {
			filterChain.setName(name);
		}
		
		if (patterns != null) {
			filterChain.setPatterns(patterns.getPatterns());
		}
		
		if (filterNames != null) {
			filterChain.setFilterNames(filterNames.getFilterNames());
		}
		
		if (disabled != null) {
			filterChain.setDisabled(disabled);
		}
		
		if (allowSessionCreation != null) {
			filterChain.setAllowSessionCreation(allowSessionCreation);
		}
		
		if (requireSSL != null) {
			filterChain.setRequireSSL(requireSSL);
		}
		
		if (matchHTTPMethod != null) {
			filterChain.setMatchHTTPMethod(matchHTTPMethod);
		}
		
		return filterChain;
	}

}
