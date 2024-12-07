package com.certi;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Document(collection = "certificates")
public class Certificate {

    @Id
    private String id;
    private String name;
    private String trackId;
    private String trackUrl;
    @Field("IssuedBy")  
    private String IssuedBy;
    private String issuedDate;
    private  String expiryDate;
    private String pdfFile;  // Store the URL or path to the file
    private String badge;    // Store the URL or path to the badge image
    private String email;    // User's email (fetched from user service)
	
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTrackUrl() {
		return trackUrl;
	}
	public void setTrackUrl(String trackUrl) {
		this.trackUrl = trackUrl;
	}
	public String getIssuedBy() {
		return IssuedBy;
	}
	public void setIssuedBy(String issuedBy) {
		IssuedBy = issuedBy;
	}
	public String getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getPdfFile() {
		return pdfFile;
	}
	public void setPdfFile(String pdfFile) {
		this.pdfFile = pdfFile;
	}
	public String getBadge() {
		return badge;
	}
	public void setBadge(String badge) {
		this.badge = badge;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	 public Certificate(String name, String trackId, String trackUrl, String IssuedBy, 
             String issuedDate, String expiryDate) {
this.name = name;
this.trackId = trackId;
this.trackUrl = trackUrl;
this.IssuedBy = IssuedBy;
this.issuedDate = issuedDate;
this.expiryDate = expiryDate;
}

    // Getters and Setters
}
