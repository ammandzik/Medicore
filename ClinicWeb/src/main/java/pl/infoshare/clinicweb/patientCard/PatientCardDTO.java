package pl.infoshare.clinicweb.patientCard;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientCardDTO {

    private String patientPesel;
    private String doctorFirstName;
    private String doctorLastName;
    private String patientFirstName;
    private String patientLastName;
    private String symptoms;
    private LocalDateTime dateOfVisit;
    private String noteDoctor;
    private String noteMedicalHistory;
    private String diagnosis;
    private String treatment;
    private Long patientId;
    private Long doctorId;



}
