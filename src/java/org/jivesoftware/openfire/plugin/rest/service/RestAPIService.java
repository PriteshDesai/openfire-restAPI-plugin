package org.jivesoftware.openfire.plugin.rest.service;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.plugin.rest.RESTServicePlugin;
import org.jivesoftware.openfire.plugin.rest.entity.SystemProperties;
import org.jivesoftware.openfire.plugin.rest.entity.SystemProperty;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pritesh_desai RestAPIService is used to get the restAPI service
 *         related changes
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/system/properties")
public class RestAPIService {

	private RESTServicePlugin plugin;

	private static Logger LOG = LoggerFactory.getLogger(RestAPIService.class);

	@PostConstruct
	public void init() {
		plugin = RESTServicePlugin.getInstance();
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SystemProperties getSystemProperties() {
		LOG.debug("RestAPIService: getSystemProperties(): START");
		SystemProperties systemProperties = plugin.getSystemProperties();
		LOG.debug("RestAPIService: getSystemProperties(): END: systemProperties: " + systemProperties.toString());
		return systemProperties;
	}

	@GET
	@Path("/{propertyKey}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public SystemProperty getSystemProperty(@PathParam("propertyKey") String propertyKey) throws ServiceException {
		LOG.debug("RestAPIService: getSystemProperty(): START: propertyKey: " + propertyKey);
		SystemProperty systemProperties = plugin.getSystemProperty(propertyKey);
		LOG.debug("RestAPIService: getSystemProperty(): END: systemProperties: " + systemProperties.toString());
		return systemProperties;
	}

	@POST
	public Response createSystemProperty(SystemProperty systemProperty) throws ServiceException {
		LOG.debug("RestAPIService: createSystemProperty(): START");
		plugin.createSystemProperty(systemProperty);
		LOG.debug("RestAPIService: createSystemProperty(): END");
		return Response.status(Response.Status.CREATED).build();
	}

	@PUT
	@Path("/{propertyKey}")
	public Response updateUser(@PathParam("propertyKey") String propertyKey, SystemProperty systemProperty)
			throws ServiceException {
		LOG.debug("RestAPIService: updateUser(): START: propertyKey: " + propertyKey + " systemProperty: " + systemProperty.toString());
		plugin.updateSystemProperty(propertyKey, systemProperty);
		LOG.debug("RestAPIService: updateUser(): END");
		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/{propertyKey}")
	public Response deleteUser(@PathParam("propertyKey") String propertyKey) throws ServiceException {
		LOG.debug("RestAPIService: deleteUser(): START: propertyKey: " + propertyKey);
		plugin.deleteSystemProperty(propertyKey);
		LOG.debug("RestAPIService: deleteUser(): END");
		return Response.status(Response.Status.OK).build();
	}
}
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */