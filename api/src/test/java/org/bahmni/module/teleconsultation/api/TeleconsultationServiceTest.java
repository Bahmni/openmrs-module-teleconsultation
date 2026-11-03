package org.bahmni.module.teleconsultation.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
		"classpath:TestingApplicationContext.xml" }, inheritLocations = true)
@PrepareForTest({Context.class})
public class TeleconsultationServiceTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	TeleconsultationService teleconsultationService;

	@Before
	public void init() {
		executeDataSet("userRolesAndPrivileges.xml");
	}

	/**
	 * With platform 2.8.x the test sessionFactory (hbm2ddl create-drop) is destroyed in a JVM shutdown hook that
	 * runs after H2's own hook has closed the in-memory database, which logs "Database is already closed".
	 * DB_CLOSE_ON_EXIT=FALSE stops H2 from closing it early. The base class hard-codes the in-memory URL, so the
	 * only way to change it is to override the runtime properties.
	 */
	@Override
	public Properties getRuntimeProperties() {
		Properties properties = super.getRuntimeProperties();
		for (String key : new String[] { "hibernate.connection.url", "connection.url" }) {
			String url = properties.getProperty(key);
			if (url != null && url.startsWith("jdbc:h2:mem:") && !url.contains("DB_CLOSE_ON_EXIT")) {
				properties.setProperty(key, url + ";DB_CLOSE_ON_EXIT=FALSE");
			}
		}
		return properties;
	}

	@Test
	public void shouldPassCreateTeleconsultationLinkIfTheUserHasCreateTeleconsultationPrivileges() {
		Context.authenticate("userWithPrivilege", "P@ssw0rd");
		UUID uuid = UUID.randomUUID();
		String link = teleconsultationService.generateTeleconsultationLink(uuid.toString());
		assertEquals("https://meet.jit.si/" + uuid, link);
	}

	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveSufficientPrivileges() {
		Context.authenticate("userWithoutPrivilege", "P@ssw0rd");
		TeleconsultationService service = Context.getService(TeleconsultationService.class);
		service.generateTeleconsultationLink("uuid");
	}
}
