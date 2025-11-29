package com.ozzo.habit_tracker.dto;

import java.util.ArrayList;
import java.util.List;

public class HabitImportForm {

    private Long uploadedImageId;
    private String uploadedImageName;
    private int year;
    private int month;
    private List<HabitImportRow> rows = new ArrayList<>();

    public Long getUploadedImageId() {
        return uploadedImageId;
    }

    public void setUploadedImageId(Long uploadedImageId) {
        this.uploadedImageId = uploadedImageId;
    }

    public String getUploadedImageName() {
        return uploadedImageName;
    }

    public void setUploadedImageName(String uploadedImageName) {
        this.uploadedImageName = uploadedImageName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<HabitImportRow> getRows() {
        return rows;
    }

    public void setRows(List<HabitImportRow> rows) {
        this.rows = rows;
    }
}
