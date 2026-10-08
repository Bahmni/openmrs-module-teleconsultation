package org.bahmni.module.teleconsultation.api;

import org.bahmni.module.teleconsultation.api.impl.TeleconsultationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TeleconsultationServiceImplTest {

    @Mock
    private AdministrationService administrationService;

    @InjectMocks
    TeleconsultationServiceImpl teleconsultationService;

    private AutoCloseable mocks;

    private MockedStatic<Context> contextMock;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        contextMock = Mockito.mockStatic(Context.class);
        contextMock.when(Context::getAdministrationService).thenReturn(administrationService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        contextMock.close();
        mocks.close();
    }

    @Test
    public void shouldGenerateTCLinkForAppointment() {
        when(administrationService.getGlobalProperty("bahmni.appointment.teleConsultation.serverUrlPattern")).thenReturn(
                "https://test.server/{0}");
        UUID uuid = UUID.randomUUID();
        String link = teleconsultationService.generateTeleconsultationLink(uuid.toString());
        assertEquals("https://test.server/" + uuid, link);
    }

    @Test
    public void shouldGenerateTCLinkForAppointmentWithDefaultServerWhenServerUrlIsNull() {
        when(administrationService.getGlobalProperty("bahmni.appointment.teleConsultation.serverUrlPattern")).thenReturn(
                null);
        UUID uuid = UUID.randomUUID();
        String link = teleconsultationService.generateTeleconsultationLink(uuid.toString());
        assertEquals("https://meet.jit.si/" + uuid, link);
    }

    @Test
    public void shouldGenerateTCLinkForAppointmentWithDefaultServerWhenServerUrlIsEmpty() {
        when(administrationService.getGlobalProperty("bahmni.appointment.teleConsultation.serverUrlPattern")).thenReturn(
                "");
        UUID uuid = UUID.randomUUID();
        String link = teleconsultationService.generateTeleconsultationLink(uuid.toString());
        assertEquals("https://meet.jit.si/" + uuid, link);
    }
}
