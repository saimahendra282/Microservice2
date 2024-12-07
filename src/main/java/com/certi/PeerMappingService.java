package com.certi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PeerMappingService {

    @Autowired
    private PeerMappingRepository peerMappingRepository;

    @Autowired
    private JWTManager jwtManager;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;  // GridFsTemplate to interact with GridFS

    public String extractEmailFromToken(String token) {
        Map<String, String> claims = jwtManager.validateToken(token);
        if ("200".equals(claims.get("code"))) {
            return claims.get("email");
        } else {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    public PeerMapping createPeerMapping(String peerEmail, String peerName, String certificateId) {
        if (peerEmail == null || peerName == null || certificateId == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        PeerMapping peerMapping = new PeerMapping(peerEmail, peerName, certificateId);
        return peerMappingRepository.save(peerMapping);
    }

    public List<PeerMapping> getAllPeerMappings() {
        List<PeerMapping> peerMappings = peerMappingRepository.findAll();

        for (PeerMapping peerMapping : peerMappings) {
            Optional<Certificate> certificate = certificateRepository.findById(peerMapping.getCertificateId());
            certificate.ifPresent(cert -> peerMapping.setCertificateId(cert.getId()));
        }

        return peerMappings;
    }

    public List<PeerMapping> getMappingsByEmail(String email) {
        List<PeerMapping> peerMappings = peerMappingRepository.findByPeerEmail(email);
        for (PeerMapping peerMapping : peerMappings) {
            Optional<Certificate> certificate = certificateRepository.findById(peerMapping.getCertificateId());
            certificate.ifPresent(cert -> peerMapping.setCertificateId(cert.getId()));
        }
        return peerMappings;
    }

    // Method to approve or reject renewal request (only status, no comment)
    public PeerMapping updateRenewalStatus(String mappingId, String renewalStatus) {
        Optional<PeerMapping> optionalMapping = peerMappingRepository.findById(mappingId);
        if (optionalMapping.isPresent()) {
            PeerMapping peerMapping = optionalMapping.get();
            peerMapping.setRenewalStatus(renewalStatus);  // Update renewal status (e.g., Approved or Rejected)
            
            // Only update the renewal status, not the new expiry date or PDF
            return peerMappingRepository.save(peerMapping);
        } else {
            throw new RuntimeException("PeerMapping not found");
        }
    }

    public PeerMapping updateStatus(String mappingId, String status, String comment) {
        Optional<PeerMapping> optionalMapping = peerMappingRepository.findById(mappingId);
        if (optionalMapping.isPresent()) {
            PeerMapping peerMapping = optionalMapping.get();
            peerMapping.setStatus(status);
            peerMapping.setComment(comment);
            return peerMappingRepository.save(peerMapping);
        } else {
            throw new RuntimeException("Mapping not found");
        }
    }

    // New method to handle renewal request (with newExpiryDate and renewal details)
    public PeerMapping handleRenewalRequest(String mappingId, MultipartFile renewalPdf, String renewalReason, String newExpiryDate) throws IOException {
        Optional<PeerMapping> optionalMapping = peerMappingRepository.findById(mappingId);
        if (optionalMapping.isPresent()) {
            PeerMapping peerMapping = optionalMapping.get();

            // Handle PDF upload and store in GridFS
            String renewalPdfId = saveFileToGridFS(renewalPdf);

            // Update renewal request details
            peerMapping.setRenewalRequested(true);  // Mark the renewal as requested
            peerMapping.setRenewalReason(renewalReason);  // Save the reason for renewal
            peerMapping.setRenewPdfFile(renewalPdfId);  // Save the PDF file ID in the model
            peerMapping.setNewExpiryDate(newExpiryDate);  // Set the new expiry date
            peerMapping.setRenewalStatus("Pending");  // Default renewal status as "Pending"

            // Save the updated PeerMapping
            return peerMappingRepository.save(peerMapping);
        } else {
            throw new RuntimeException("PeerMapping not found");
        }
    }

    // Helper method to save file to GridFS and return the file ID
    private String saveFileToGridFS(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        org.bson.types.ObjectId fileId = gridFsTemplate.store(file.getInputStream(), filename);  // Store the file in GridFS
        return fileId.toString();  // Return the GridFS file ID as a string
    }
}
