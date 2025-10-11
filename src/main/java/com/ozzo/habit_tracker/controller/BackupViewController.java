package com.ozzo.habit_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozzo.habit_tracker.backup.BackupData;
import com.ozzo.habit_tracker.backup.BackupImportResult;
import com.ozzo.habit_tracker.service.BackupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class BackupViewController {

    private final BackupService backupService;
    private final ObjectMapper objectMapper;

    public BackupViewController(BackupService backupService, ObjectMapper objectMapper) {
        this.backupService = backupService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/backup")
    public String showBackupPage(Model model) {
        if (!model.containsAttribute("importResult")) {
            model.addAttribute("importResult", null);
        }
        if (!model.containsAttribute("importError")) {
            model.addAttribute("importError", null);
        }
        model.addAttribute("newPage", "backup");
        return "index";
    }

    @PostMapping("/backup/import")
    public String importBackup(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("importError", "Please select a Backup-File.");
            return "redirect:/backup";
        }

        try (InputStream inputStream = file.getInputStream()) {
            BackupData backupData = objectMapper.readValue(inputStream, BackupData.class);
            BackupImportResult result = backupService.importBackup(backupData);
            redirectAttributes.addFlashAttribute("importResult", result);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("importError", "The Backup-File couldn't be read.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("importError", e.getMessage());
        }

        return "redirect:/backup";
    }
}
