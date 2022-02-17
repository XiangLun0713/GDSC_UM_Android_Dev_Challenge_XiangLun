package me.xianglun.idiary.model;

import java.util.List;

public class DiaryModel {
    private String date, time, title, diaryMainText, idaryId;
    List<String> imageURL, texts;

    public DiaryModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiaryMainText() {
        return diaryMainText;
    }

    public void setDiaryMainText(String diaryMainText) {
        this.diaryMainText = diaryMainText;
    }

    public String getIdaryId() {
        return idaryId;
    }

    public void setIdaryId(String idaryId) {
        this.idaryId = idaryId;
    }

    public List<String> getImageURL() {
        return imageURL;
    }

    public void setImageURL(List<String> imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
