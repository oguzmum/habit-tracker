package com.ozzo.habit_tracker.controller;

import com.ozzo.habit_tracker.entity.UploadedImage;
import com.ozzo.habit_tracker.service.UploadedImageService;
import java.io.IOException;
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

    public UploadImageViewController(UploadedImageService uploadedImageService) {
        this.uploadedImageService = uploadedImageService;
    }

    @GetMapping("/uploads")
    public String showUploadPage(Model model) {
        List<UploadedImage> uploads = uploadedImageService.listUploads();
        model.addAttribute("uploadedImages", uploads);
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
}
