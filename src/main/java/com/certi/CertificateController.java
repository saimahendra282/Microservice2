package com.certi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "http://localhost:3000")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private JWTManager jwtManager; // Injecting JWTManager to handle JWT extraction

    // Endpoint to retrieve PDF
    @GetMapping("/pdf/{fileId}")
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String fileId) throws IOException {
        GridFsResource resource = certificateService.getPdf(fileId);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // Set the appropriate content type based on file type
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + fileId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    // Endpoint to retrieve image (badge)
    @GetMapping("/badge/{fileId}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileId) throws IOException {
        GridFsResource resource = certificateService.getImage(fileId);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // Set the appropriate content type based on file type
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + fileId + ".png");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_PNG)  // You can adjust this based on your file type (e.g., .jpg, .png)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    // Endpoint to retrieve all certificates
    @GetMapping("/all")
    public ResponseEntity<List<Certificate>> getAllCertificates(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Ensure the Authorization header starts with "Bearer "
            if (!authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Extract token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Validate and extract email from the token
            String email = certificateService.extractEmailFromToken(token); // Call service method for extraction

            // If token is valid, fetch all certificates
            List<Certificate> certificates = certificateService.getAllCertificates();
            return ResponseEntity.ok(certificates);

        } catch (Exception e) {
            // Log the exception message for debugging
            System.err.println("Error: " + e.getMessage());

            // Return error if there's an issue with the token or fetching certificates
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/email")
    public ResponseEntity<List<Certificate>> getCertificatesByEmail(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Ensure the Authorization header starts with "Bearer "
            if (!authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Extract token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Validate and extract email from the token
            String email = certificateService.extractEmailFromToken(token); // Call service method for extraction

            // Fetch certificates associated with the extracted email
            List<Certificate> certificates = certificateService.getCertificatesByEmail(email);
            return ResponseEntity.ok(certificates);

        } catch (Exception e) {
            // Log the exception message for debugging
            System.err.println("Error: " + e.getMessage());

            // Return error if there's an issue with the token or fetching certificates
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    // Endpoint to upload certificate and files (PDF & badge image)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCertificate(
            @RequestHeader("Authorization") String authorizationHeader, // Extract token from header
            @RequestParam("name") String name,
            @RequestParam("trackId") String trackId,
            @RequestParam("trackUrl") String trackUrl,
            @RequestParam("issuedBy") String issuedBy,
            @RequestParam("issuedDate") String issuedDate,
            @RequestParam("expiryDate") String expiryDate,
            @RequestParam("pdfFile") MultipartFile pdfFile,
            @RequestParam("badge") MultipartFile badge) {

        try {
            // Extract the actual token by removing "Bearer " prefix
            String token = authorizationHeader.replace("Bearer ", "");

            // Create certificate object with basic details
            Certificate certificate = new Certificate(name, trackId, trackUrl, issuedBy, issuedDate, expiryDate);

            // Call the service method to save the certificate and upload files
            certificateService.saveCertificate(token, certificate, pdfFile, badge);

            return ResponseEntity.ok("Certificate uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error uploading files: " + e.getMessage());
        }
    }
}
