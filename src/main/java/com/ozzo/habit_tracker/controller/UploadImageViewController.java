package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.dto.HabitImportForm;
import com.ozzo.habit_tracker.entity.UploadedImage;
import com.ozzo.habit_tracker.service.HabitImportService;
import com.ozzo.habit_tracker.service.UploadedImageService;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadImageViewController {

    private static final Logger log = LoggerFactory.getLogger(UploadImageViewController.class);

    private final UploadedImageService uploadedImageService;
    private final HabitImportService habitImportService;

    public UploadImageViewController(UploadedImageService uploadedImageService, HabitImportService habitImportService) {
        this.uploadedImageService = uploadedImageService;
        this.habitImportService = habitImportService;
    }

    @GetMapping("/uploads")
    public String showUploadPage(Model model) {
        populateUploads(model);
        model.addAttribute("newPage", "uploadImage");
        return "index";
    }

    @PostMapping("/uploads")
    public String handleUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            UploadedImage uploadedImage = uploadedImageService.store(file);
            redirectAttributes.addFlashAttribute(
                    "uploadSuccess", "Uploaded " + uploadedImage.getOriginalFilename() + " successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("uploadError", e.getMessage());
        } catch (IOException e) {
            log.error("Failed to store uploaded file", e);
            redirectAttributes.addFlashAttribute("uploadError", "The file could not be saved. Please try again.");
        }

        //this isn't supposed to be the name of the file (uploadImage)
        //but the spring endpoint (uploads) :D
        return "redirect:/uploads";
    }

    @PostMapping("/uploads/analyze")
    public String analyzeUpload(@RequestParam("imageId") Long imageId, @RequestParam("yearMonth") String yearMonth, Model model) {
        populateUploads(model);

        try {
            // defines the table that will be shown in the UI, where the user can take some adjustments before persisting the habit entries
            YearMonth target = YearMonth.parse(yearMonth);
            HabitImportForm form = habitImportService.buildImportForm(imageId, target);
            model.addAttribute("importForm", form);
            model.addAttribute("monthDays", habitImportService.getMonthDays(target));
            model.addAttribute("selectedYearMonth", yearMonth);
        } catch (Exception e) {
            log.error("Failed to analyze uploaded image", e);
            model.addAttribute("analysisError", e.getMessage());
        }

        model.addAttribute("newPage", "uploadImage");
        return "index";
    }

    @PostMapping("/uploads/import")
    public String importDetectedHabits(HabitImportForm form, RedirectAttributes redirectAttributes) {
        try {
            habitImportService.persistImport(form);
            redirectAttributes.addFlashAttribute(
                    "importSuccess",
                    "Imported detections for " + YearMonth.of(form.getYear(), form.getMonth()) + "."
            );
        } catch (Exception e) {
            log.error("Failed to import detected habits", e);
            redirectAttributes.addFlashAttribute("importError", e.getMessage());
        }

        return "redirect:/uploads";
    }

    private void populateUploads(Model model) {
        List<UploadedImage> uploads = uploadedImageService.listUploads();
        model.addAttribute("uploadedImages", uploads);
        model.addAttribute("selectedYearMonth", YearMonth.now().toString());
    }
}
