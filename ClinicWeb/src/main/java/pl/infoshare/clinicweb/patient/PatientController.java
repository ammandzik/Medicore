package pl.infoshare.clinicweb.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.infoshare.clinicweb.doctor.DoctorDto;
import pl.infoshare.clinicweb.doctor.DoctorService;
import pl.infoshare.clinicweb.user.PersonDetails;
import pl.infoshare.clinicweb.user.Utils;
import pl.infoshare.clinicweb.visit.VisitService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class PatientController {

    private final PatientService patientService;

    private final DoctorService doctorService;

    @GetMapping("/patient")
    public String patientForm(Model model) {

        List<DoctorDto> doctors = Utils.convertOptionalToList(doctorService.findAllDoctors());

        model.addAttribute("personDetails", new PersonDetails());
        model.addAttribute("address", new Address());
        model.addAttribute("doctors", doctors);

        return "patient";
    }

    @PostMapping("/patient")
    public String patientFormSubmission(@ModelAttribute Patient patient,
                                        @Valid PersonDetails patientDetails, BindingResult detailsBinding,
                                        @Valid Address patientAddress, BindingResult addressBinding,
                                        @RequestParam("pesel") String pesel,
                                        Model model, RedirectAttributes redirectAttributes) {

        List<DoctorDto> doctors = Utils.convertOptionalToList(doctorService.findAllDoctors());

        model.addAttribute("doctors", doctors);

        if (detailsBinding.hasErrors() || addressBinding.hasErrors() || !Utils.hasPeselCorrectDigits(pesel)) {

            model.addAttribute("peselError", "Wprowadzony numer PESEL jest niepoprawny");

            return "patient";

        } else {

            redirectAttributes.addFlashAttribute("success", "Utworzono nowego pacjenta w bazie.");

            patientService.setPatientAttributes(patient, patientDetails, patientAddress);
            patientService.addPatient(patient);

            return "redirect:/patient";
        }

    }

    @GetMapping("/patients")
    public String viewPatients(Model model) {

        List<PatientDto> patients = Utils.convertOptionalToList(patientService.findAllPatients());

        model.addAttribute("listPatient", patients);

        return "patients";
    }


    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "search";
    }

    @PostMapping("/search")
    public String searchPatient(@PathVariable("id") Long id, Model model, Address address) {

        Optional<PatientDto> patient = patientService.findById(id);
        if (patient != null) {
            model.addAttribute("patient", patient);
            model.addAttribute("address", address);
        } else {
            model.addAttribute("error", "Patient not found");
        }
        return "search";
    }

    @PostMapping("/update-patient")
    public String editPatient(@ModelAttribute("patient") PatientDto patient,
                              Model model, Address address, RedirectAttributes redirectAttributes) {

        model.addAttribute("patient", patient);
        model.addAttribute("address", address);

        patientService.updatePatient(patient, address);
        redirectAttributes.addFlashAttribute("success", "Zaktualizowano dane pacjenta.");
        return "redirect:patients";
    }

    @GetMapping("/update-patient")
    public String fullDetailPatient(@RequestParam(value = "id", required = false)
                                    @ModelAttribute Long id,
                                    Model model) {

        model.addAttribute("patient", patientService.findById(id).get());


        return "update-patient";
    }

    @PostMapping("/search-patient")
    public String searchPatientById(@RequestParam("id") @ModelAttribute Long id,
                                    Model model) {


        if (patientService.findById(id) != null) {
            model.addAttribute("searchForId", id);
        } else {
            model.addAttribute("error", "Nie znaleziono pacjenta o podanym numerze id: " + id);
        }
        return "search-patient";
    }

    @GetMapping("/search-patient")
    public String searchPatientById(Model model) {

        model.addAttribute("patient", new Patient());

        return "search-patient";
    }

    @PostMapping("/delete-patient")
    public String deletePatient(@RequestParam("id") Long id) {

        Optional<PatientDto> patientById = patientService.findById(id);
        if (patientById.isPresent()) {
            patientService.deletePatient(id);
        }
        return "redirect:/patients";
    }

    @GetMapping("/delete-patient")
    public String showDeletePatientForm(@RequestParam("id") Long id, Model model) {

        Optional<PatientDto> patientById = patientService.findById(id);
        model.addAttribute("patient", patientById);

        return "delete-patient";
    }


}
