/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest;

import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.RequestFilterChain;
import org.geoserver.security.config.PasswordPolicyConfig;
import org.geoserver.security.config.SecurityAuthProviderConfig;
import org.geoserver.security.config.SecurityFilterConfig;
import org.geoserver.security.config.SecurityManagerConfig;
import org.geoserver.security.password.MasterPasswordProviderConfig;
import org.geoserver.security.validation.SecurityConfigException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import stratus.rest.xml.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController(value = "securityRestController")
@RequestMapping(value = "/rest/security")
public class SecurityConfigRestController {

	protected GeoServerSecurityManager securityManager;

	public SecurityConfigRestController(GeoServerSecurityManager securityManager) {
		this.securityManager = securityManager;
	}

    @ExceptionHandler(IllegalStateException.class)
    public void illegalArgumentError(IllegalStateException exception, HttpServletResponse response) throws IOException {
    	response.sendError(400, exception.getMessage());
    }
    
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public void indexOutOfBounds(IndexOutOfBoundsException exception, HttpServletResponse response) throws IOException {
    	response.sendError(400, exception.getMessage());
    }
    
    @ExceptionHandler(IOException.class)
    public void ioError(IOException exception, HttpServletResponse response) throws IOException {
    	response.sendError(500, exception.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public void somethingNotFound(IllegalArgumentException exception, HttpServletResponse response) throws IOException {
    	response.sendError(404, exception.getMessage());
    }

	@GetMapping(value = "", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public JaxbSecurityConfig get() throws IOException {
        return new JaxbSecurityConfig(securityManager);
    }
	
	@PutMapping(value = "")    
	public @ResponseStatus(HttpStatus.OK) void put(@RequestBody JaxbSecurityConfig config) throws Exception {
		config.updateConfig(securityManager);		
    }
	
	@PostMapping(value = "/filterChains")    
	public @ResponseStatus(HttpStatus.CREATED) void postFilterChain(@RequestBody JaxbRequestFilterChain chain) throws Exception {
		SecurityManagerConfig config = securityManager.getSecurityConfig();
		if (chain.getPosition() == null) {
			config.getFilterChain().getRequestChains().add(chain.save());
		} else {
			config.getFilterChain().getRequestChains().add(chain.getPosition(), chain.save());
		}
		securityManager.saveSecurityConfig(config);
    }
	
	@GetMapping(value = "/filterChains", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})    
	public @ResponseStatus(HttpStatus.CREATED)
    JaxbRequestFilterChainList getFilterChains() throws Exception {
		List<JaxbRequestFilterChain> list = new ArrayList<>();
		for (RequestFilterChain gsChain : securityManager.getSecurityConfig().getFilterChain().getRequestChains()) {			
			list.add(new JaxbRequestFilterChain(gsChain));
		}
		return new JaxbRequestFilterChainList(list);
    }
	
	@PutMapping(value = "/filterChains/{name}")    
	public @ResponseStatus(HttpStatus.OK) void putFilterChain(@PathVariable String name, 
			@RequestBody JaxbRequestFilterChain chain) throws Exception {
		SecurityManagerConfig config = securityManager.getSecurityConfig();
		RequestFilterChain gsChain = config.getFilterChain().getRequestChainByName(name);
		if (gsChain == null) {
			throw new IllegalArgumentException("filterchain doesn't exist");
		}
		gsChain = chain.save(gsChain);
		int currentPosition = config.getFilterChain().getRequestChains().indexOf(gsChain);
		
		if (chain.getPosition() == null || chain.getPosition() == currentPosition) {
			config.getFilterChain().getRequestChains().set(currentPosition, chain.save());
		} else {
			config.getFilterChain().getRequestChains().remove(currentPosition);
			config.getFilterChain().getRequestChains().add(
					currentPosition < chain.getPosition() ? chain.getPosition() - 1 : chain.getPosition(), 
							chain.save());
		}
		securityManager.saveSecurityConfig(config);
    }
	
	@DeleteMapping(value = "/filterChains/{name}")    
	public @ResponseStatus(HttpStatus.OK) void deleteFilterChain(@PathVariable String name) throws Exception {
		SecurityManagerConfig config = securityManager.getSecurityConfig();
		RequestFilterChain gsChain = config.getFilterChain().getRequestChainByName(name);
		if (gsChain == null) {
			throw new IllegalArgumentException("filterchain doesn't exist");
		}
		int currentPosition = config.getFilterChain().getRequestChains().indexOf(gsChain);
		config.getFilterChain().getRequestChains().remove(currentPosition);
		securityManager.saveSecurityConfig(config);
    }
	
	@GetMapping(value = "/filterChains/{name}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})    
	public @ResponseStatus(HttpStatus.OK) JaxbRequestFilterChain getFilterChain(@PathVariable String name) throws Exception {
		SecurityManagerConfig config = securityManager.getSecurityConfig();
		RequestFilterChain gsChain = config.getFilterChain().getRequestChainByName(name);
		if (gsChain == null) {
			throw new IllegalArgumentException("filterchain doesn't exist");
		}
		JaxbRequestFilterChain chain = new JaxbRequestFilterChain(gsChain);
		chain.setPosition(config.getFilterChain().getRequestChains().indexOf(gsChain));		
		return chain;
    }
	
	@GetMapping(value = "/authFilters")    
	public @ResponseStatus(HttpStatus.OK)
    JaxbAuthFilterList getAuthFilters() throws Exception {
		return new JaxbAuthFilterList(securityManager.listFilters());
    }
	
	@GetMapping(value = "/authFilters/{name}", produces = {MediaType.APPLICATION_XML_VALUE})    
	public @ResponseStatus(HttpStatus.OK) byte[] getAuthFilter(@PathVariable String name) throws IOException {
		SecurityFilterConfig filterConfig = securityManager.loadFilterConfig(name);
		if (filterConfig == null) {
			throw new IllegalArgumentException("authentication filter not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			persister.save(filterConfig, out);
			return out.toByteArray();
		}
    }

	@PostMapping(value = "/authFilters")    
	public @ResponseStatus(HttpStatus.CREATED) void postAuthFilter(@RequestBody byte[] data) throws IOException, SecurityConfigException {
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			SecurityFilterConfig filterConfig = persister.load(in, SecurityFilterConfig.class);
			if (filterConfig.getId() != null) {
				throw new IllegalStateException("provided an ID for new authentication filter.");
			}
			securityManager.saveFilter(filterConfig);
		}
    }
	
	@PutMapping(value = "/authFilters/{name}")    
	public @ResponseStatus(HttpStatus.OK) void putAuthFilter(@PathVariable String name, 
			@RequestBody byte[] data) throws IOException, SecurityConfigException {
		SecurityFilterConfig oldFilterConfig = securityManager.loadFilterConfig(name);
		if (oldFilterConfig == null) {
			throw new IllegalArgumentException("authentication filter not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			SecurityFilterConfig filterConfig = persister.load(in, SecurityFilterConfig.class);
			if (filterConfig.getId() != null && !filterConfig.getId().equals(oldFilterConfig.getId())) {
				throw new IllegalStateException("provided a non-matching ID for authentication filter.");
			}
			if (filterConfig.getName() != null && !filterConfig.getName().equals(oldFilterConfig.getName())) {
				throw new IllegalStateException("provided a non-matching name for authentication filter.");
			}
			if (filterConfig.getName() == null) {
				filterConfig.setName(name);
			}
			filterConfig.setId(oldFilterConfig.getId());
			securityManager.saveFilter(filterConfig);
		}
    }
	
	@DeleteMapping(value = "/authFilters/{name}")    
	public @ResponseStatus(HttpStatus.OK) void deleteAuthFilter(@PathVariable String name) throws IOException, SecurityConfigException {
		SecurityFilterConfig oldFilterConfig = securityManager.loadFilterConfig(name);
		if (oldFilterConfig == null) {
			throw new IllegalArgumentException("authentication filter not found");
		}
		
		securityManager.removeFilter(oldFilterConfig);
    }
	
	
	@GetMapping(value = "/authProviders")    
	public @ResponseStatus(HttpStatus.OK)
    JaxbAuthProviderList getAuthProviders() throws Exception {
		return new JaxbAuthProviderList(securityManager.listAuthenticationProviders());
    }
	
	@GetMapping(value = "/authProviders/{name}", produces = {MediaType.APPLICATION_XML_VALUE})    
	public @ResponseStatus(HttpStatus.OK) byte[] getAuthProvider(@PathVariable String name) throws IOException {
		SecurityAuthProviderConfig providerConfig = securityManager.loadAuthenticationProviderConfig(name);
		if (providerConfig == null) {
			throw new IllegalArgumentException("authentication provider not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			persister.save(providerConfig, out);
			return out.toByteArray();
		}
    }
	
	@PostMapping(value = "/authProviders")    
	public @ResponseStatus(HttpStatus.CREATED) void postAuthProvider(@RequestBody byte[] data) throws IOException, SecurityConfigException {
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			SecurityAuthProviderConfig providerConfig = persister.load(in, SecurityAuthProviderConfig.class);
			if (providerConfig.getId() != null) {
				throw new IllegalStateException("provided an ID for new authentication provider.");
			}
			securityManager.saveAuthenticationProvider(providerConfig);
		}
    }
	
	@PutMapping(value = "/authProviders/{name}")    
	public @ResponseStatus(HttpStatus.OK) void putAuthProvider(@PathVariable String name, 
			@RequestBody byte[] data) throws IOException, SecurityConfigException {
		SecurityAuthProviderConfig oldProviderConfig = securityManager.loadAuthenticationProviderConfig(name);
		if (oldProviderConfig == null) {
			throw new IllegalArgumentException("authentication provider not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			SecurityAuthProviderConfig providerConfig = persister.load(in, SecurityAuthProviderConfig.class);
			if (providerConfig.getId() != null && !providerConfig.getId().equals(oldProviderConfig.getId())) {
				throw new IllegalStateException("provided a non-matching ID for authentication provider.");
			}
			if (providerConfig.getName() != null && !providerConfig.getName().equals(oldProviderConfig.getName())) {
				throw new IllegalStateException("provided a non-matching name for authentication provider.");
			}
			if (providerConfig.getName() == null) {
				providerConfig.setName(name);
			}
			providerConfig.setId(oldProviderConfig.getId());
			securityManager.saveAuthenticationProvider(providerConfig);
		}
    }
	
	@DeleteMapping(value = "/authProviders/{name}")    
	public @ResponseStatus(HttpStatus.OK) void deleteAuthProvider(@PathVariable String name) throws IOException, SecurityConfigException {
		SecurityAuthProviderConfig oldProviderConfig = securityManager.loadAuthenticationProviderConfig(name);
		if (oldProviderConfig == null) {
			throw new IllegalArgumentException("authentication provider not found");
		}
		
		securityManager.removeAuthenticationProvider(oldProviderConfig);
    }
	
	@GetMapping(value = "/masterPasswordProviders")    
	public @ResponseStatus(HttpStatus.OK)
    JaxbPasswordProviderList getPasswordProviders() throws Exception {
		return new JaxbPasswordProviderList(securityManager.listMasterPasswordProviders());
    }
	
	@GetMapping(value = "/masterPasswordProviders/{name}", produces = {MediaType.APPLICATION_XML_VALUE})    
	public @ResponseStatus(HttpStatus.OK) byte[] getPasswordProvider(@PathVariable String name) throws IOException {
		MasterPasswordProviderConfig providerConfig = securityManager.loadMasterPassswordProviderConfig(name);
		if (providerConfig == null) {
			throw new IllegalArgumentException("master password provider not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			persister.save(providerConfig, out);
			return out.toByteArray();
		}
    }
	
	@PostMapping(value = "/masterPasswordProviders")    
	public @ResponseStatus(HttpStatus.CREATED) void postPasswordProvider(@RequestBody byte[] data) throws IOException, SecurityConfigException {
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			MasterPasswordProviderConfig providerConfig = persister.load(in, MasterPasswordProviderConfig.class);
			if (providerConfig.getId() != null) {
				throw new IllegalStateException("provided an ID for new master password provider.");
			}
			securityManager.saveMasterPasswordProviderConfig(providerConfig);
		}
    }
	
	@PutMapping(value = "/masterPasswordProviders/{name}")    
	public @ResponseStatus(HttpStatus.OK) void putPasswordProvider(@PathVariable String name, 
			@RequestBody byte[] data) throws IOException, SecurityConfigException {
		MasterPasswordProviderConfig oldProviderConfig = securityManager.loadMasterPassswordProviderConfig(name);
		if (oldProviderConfig == null) {
			throw new IllegalArgumentException("master password provider not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			MasterPasswordProviderConfig providerConfig = persister.load(in, MasterPasswordProviderConfig.class);
			if (providerConfig.getId() != null && !providerConfig.getId().equals(oldProviderConfig.getId())) {
				throw new IllegalStateException("provided a non-matching ID for master password provider.");
			}
			if (providerConfig.getName() != null && !providerConfig.getName().equals(oldProviderConfig.getName())) {
				throw new IllegalStateException("provided a non-matching name for master password provider.");
			}
			if (providerConfig.getName() == null) {
				providerConfig.setName(name);
			}
			providerConfig.setId(oldProviderConfig.getId());
			securityManager.saveMasterPasswordProviderConfig(providerConfig);
		}
    }
	
	@DeleteMapping(value = "/masterPasswordProviders/{name}")    
	public @ResponseStatus(HttpStatus.OK) void deletePasswordProvider(@PathVariable String name) throws IOException, SecurityConfigException {
		MasterPasswordProviderConfig oldProviderConfig = securityManager.loadMasterPassswordProviderConfig(name);
		if (oldProviderConfig == null) {
			throw new IllegalArgumentException("master password provider not found");
		}
		
		securityManager.removeMasterPasswordProvder(oldProviderConfig);
    }	

	@GetMapping(value = "/passwordPolicies")    
	public @ResponseStatus(HttpStatus.OK)
    JaxbPasswordPolicyList getPasswordPolicies() throws Exception {
		return new JaxbPasswordPolicyList(securityManager.listPasswordValidators());
    }
	
	@GetMapping(value = "/passwordPolicies/{name}", produces = {MediaType.APPLICATION_XML_VALUE})    
	public @ResponseStatus(HttpStatus.OK) byte[] getPasswordPolicy(@PathVariable String name) throws IOException {
		PasswordPolicyConfig policyConfig = securityManager.loadPasswordPolicyConfig(name);
		if (policyConfig == null) {
			throw new IllegalArgumentException("password policy not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			persister.save(policyConfig, out);
			return out.toByteArray();
		}
    }
	
	@PostMapping(value = "/passwordPolicies")    
	public @ResponseStatus(HttpStatus.CREATED) void postPasswordPolicy(@RequestBody byte[] data) throws IOException, SecurityConfigException {
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			PasswordPolicyConfig policyConfig = persister.load(in, PasswordPolicyConfig.class);
			if (policyConfig.getId() != null) {
				throw new IllegalStateException("provided an ID for new password policy.");
			}
			securityManager.savePasswordPolicy(policyConfig);
		}
    }
	
	@PutMapping(value = "/passwordPolicies/{name}")    
	public @ResponseStatus(HttpStatus.OK) void putPasswordPolicy(@PathVariable String name, 
			@RequestBody byte[] data) throws IOException, SecurityConfigException {
		PasswordPolicyConfig oldPolicyConfig = securityManager.loadPasswordPolicyConfig(name);
		if (oldPolicyConfig == null) {
			throw new IllegalArgumentException("master password provider not found");
		}
		
		XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			PasswordPolicyConfig policyConfig = persister.load(in, PasswordPolicyConfig.class);
			if (policyConfig.getId() != null && !policyConfig.getId().equals(oldPolicyConfig.getId())) {
				throw new IllegalStateException("provided a non-matching ID for password policuy.");
			}
			if (policyConfig.getName() != null && !policyConfig.getName().equals(oldPolicyConfig.getName())) {
				throw new IllegalStateException("provided a non-matching name for password policy.");
			}
			if (policyConfig.getName() == null) {
				policyConfig.setName(name);
			}
			policyConfig.setId(oldPolicyConfig.getId());
			securityManager.savePasswordPolicy(policyConfig);
		}
    }
	
	@DeleteMapping(value = "/passwordPolicies/{name}")    
	public @ResponseStatus(HttpStatus.OK) void deletePasswordPolicy(@PathVariable String name) throws IOException, SecurityConfigException {
		PasswordPolicyConfig oldPolicyConfig = securityManager.loadPasswordPolicyConfig(name);
		if (oldPolicyConfig == null) {
			throw new IllegalArgumentException("master password provider not found");
		}
		
		securityManager.removePasswordValidator(oldPolicyConfig);
    }

}
