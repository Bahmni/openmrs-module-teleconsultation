package org.bahmni.module.teleconsultation.web.controller;

import org.bahmni.module.teleconsultation.api.TeleconsultationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class TeleconsultationControllerTest {
	
	@Mock
	private TeleconsultationService teleconsultationService;
	
	@InjectMocks
	private TeleconsultationController teleconsultationController;
	
	private AutoCloseable mocks;
	
	@BeforeEach
	public void setUp() throws Exception {
		mocks = openMocks(this);
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
	}
	
	@Test
	public void shouldGenerateTeleconsultationLink() throws Exception {
		String patientUuid = "patientUuid";
		ResponseEntity<String> adhocTeleconsultationResponse = teleconsultationController
		        .generateTeleconsultationLink(patientUuid);
		verify(teleconsultationService, times(1)).generateTeleconsultationLink(eq(patientUuid));
	}
}
