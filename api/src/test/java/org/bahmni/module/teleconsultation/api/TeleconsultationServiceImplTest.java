package org.bahmni.module.teleconsultation.api;

import org.bahmni.module.teleconsultation.api.impl.TeleconsultationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.PrivilegeConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class TeleconsultationServiceImplTest {

    @Mock
    private AdministrationService administrationService;

    @InjectMocks
    TeleconsultationServiceImpl teleconsultationService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
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
    public void shouldAddAndRemoveGetGlobalPropertiesProxyPrivilegeAroundGlobalPropertyRead() {
        when(administrationService.getGlobalProperty("bahmni.appointment.teleConsultation.serverUrlPattern")).thenAnswer(
                invocation -> {
                    // verifyStatic only checks invocation counts, not order. Verifying from inside the stub
                    // proves the privilege is held before the read happens, and not released yet.
                    PowerMockito.verifyStatic(Context.class);
                    Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
                    PowerMockito.verifyStatic(Context.class, Mockito.never());
                    Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
                    return "https://test.server/{0}";
                });
        teleconsultationService.generateTeleconsultationLink(UUID.randomUUID().toString());

        PowerMockito.verifyStatic(Context.class);
        Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
        PowerMockito.verifyStatic(Context.class);
        Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
    }

    @Test
    public void shouldRemoveProxyPrivilegeEvenWhenGlobalPropertyReadFails() {
        when(administrationService.getGlobalProperty("bahmni.appointment.teleConsultation.serverUrlPattern")).thenThrow(
                new IllegalStateException("boom"));
        try {
            teleconsultationService.generateTeleconsultationLink(UUID.randomUUID().toString());
            fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // the privilege must still be released
        }

        PowerMockito.verifyStatic(Context.class);
        Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
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
