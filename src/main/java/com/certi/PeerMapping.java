package com.certi;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "peer_mappings")
public class PeerMapping {

    @Id
    private String id;
    private String peerEmail;
    private String peerName;
    private String certificateId;
    private String status = "Pending";  // Default value
    private String comment;             // For peer comments on the certificate

    // Fields for renewal
    private boolean renewalRequested = false;  // Default is no request
    private String renewalReason;             // Reason for requesting renewal
    private String renewalStatus = "Pending"; // Renewal status: Pending, Approved, Rejected
    private String renewPdfFile;             // ID of the renewal document in GridFS or a URL
    private String newExpiryDate;            // New expiry date after renewal

    // Constructors
    public PeerMapping(String peerEmail, String peerName, String certificateId) {
        this.peerEmail = peerEmail;
        this.peerName = peerName;
        this.certificateId = certificateId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeerEmail() {
        return peerEmail;
    }

    public void setPeerEmail(String peerEmail) {
        this.peerEmail = peerEmail;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isRenewalRequested() {
        return renewalRequested;
    }

    public void setRenewalRequested(boolean renewalRequested) {
        this.renewalRequested = renewalRequested;
    }

    public String getRenewalReason() {
        return renewalReason;
    }

    public void setRenewalReason(String renewalReason) {
        this.renewalReason = renewalReason;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getRenewPdfFile() {
        return renewPdfFile;
    }

    public void setRenewPdfFile(String renewPdfFile) {
        this.renewPdfFile = renewPdfFile;
    }

    public String getNewExpiryDate() {
        return newExpiryDate;
    }

    public void setNewExpiryDate(String newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }
}
