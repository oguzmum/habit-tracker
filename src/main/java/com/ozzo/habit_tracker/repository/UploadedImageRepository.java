package com.ozzo.habit_tracker.repository;

import com.ozzo.habit_tracker.entity.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, Long> {
}
