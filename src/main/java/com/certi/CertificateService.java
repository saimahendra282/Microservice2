package com.certi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.StringUtils;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private JWTManager jwtManager;  // Injecting JWTManager to handle JWT extraction

    @Autowired
    private GridFsTemplate gridFsTemplate; // GridFsTemplate for storing files

    // Method to extract email from JWT token
    public String extractEmailFromToken(String token) {
        Map<String, String> claims = jwtManager.validateToken(token); // Validate and extract claims
        if (claims.get("code").equals("200")) {
            return claims.get("email"); // Return extracted email from JWT claims
        } else {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    // Save the certificate along with PDF and Badge files
    public void saveCertificate(String token, Certificate certificate, MultipartFile pdfFile, MultipartFile badge) throws IOException {
        String email = extractEmailFromToken(token);
        certificate.setEmail(email);  // Set email on certificate
        
        String pdfUrl = null;
        String badgeUrl = null;

        // Save PDF to GridFS if file exists
        if (pdfFile != null && !pdfFile.isEmpty()) {
            pdfUrl = saveFileToGridFS(pdfFile);
        }

        // Save Badge to GridFS if file exists
        if (badge != null && !badge.isEmpty()) {
            badgeUrl = saveFileToGridFS(badge);
        }

        certificate.setPdfFile(pdfUrl);  // Set PDF file URL (GridFS ID)
        certificate.setBadge(badgeUrl);  // Set Badge file URL (GridFS ID)

        certificateRepository.save(certificate);  // Save certificate to the database
    }

    // Fetch all certificates
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    // Fetch a certificate by ID
    public Certificate getCertificateById(String id) {
        return certificateRepository.findById(id).orElse(null);
    }
    public List<Certificate> getCertificatesByEmail(String email) {
        return certificateRepository.findByEmail(email); // Assuming you have a method in your repository to fetch certificates by email
    }

    // Fetch the PDF file from GridFS
    public GridFsResource getPdf(String fileId) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(fileId)));
            GridFSFile gridFSFile = gridFsTemplate.findOne(query);
            if (gridFSFile == null) {
                return null; // File not found
            }
            InputStream fileStream = gridFsTemplate.getResource(gridFSFile).getInputStream();
            return new GridFsResource(gridFSFile, fileStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fetch the image file from GridFS
    public GridFsResource getImage(String fileId) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(fileId)));
            GridFSFile gridFSFile = gridFsTemplate.findOne(query);
            if (gridFSFile == null) {
                return null; // File not found
            }
            InputStream fileStream = gridFsTemplate.getResource(gridFSFile).getInputStream();
            return new GridFsResource(gridFSFile, fileStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Helper method to save a file to GridFS and return the file ID
    private String saveFileToGridFS(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), filename);  // Store file in GridFS
        return fileId.toString();  // Return file ID as String (can be used as URL or path)
    }
}
