package com.certi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/peer-mappings")
@CrossOrigin(origins = "http://localhost:3000") // React frontend URL
public class PeerMappingController {

    @Autowired
    private PeerMappingService peerMappingService;

    // Endpoint to get all peer mappings along with certificate details
    @GetMapping("/all")
    public ResponseEntity<List<PeerMapping>> getAllPeerMappings() {
        List<PeerMapping> peerMappings = peerMappingService.getAllPeerMappings();
        return ResponseEntity.ok(peerMappings);
    }

    // Endpoint to create or update peer mapping (simplified)
    @PostMapping("/add")
    public ResponseEntity<String> addPeerMapping(@RequestBody PeerMapping peerMapping) {
        try {
            // Validate the required fields before passing to the service
            if (peerMapping.getPeerEmail() == null || peerMapping.getPeerName() == null || peerMapping.getCertificateId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields.");
            }

            // Create or update peer mapping
            peerMappingService.createPeerMapping(
                peerMapping.getPeerEmail(),
                peerMapping.getPeerName(),
                peerMapping.getCertificateId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Peer mapping saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving peer mapping: " + e.getMessage());
        }
    }

    // Endpoint to get a peer mapping by peer email along with certificate details
    @GetMapping("/mapped/self")
    public ResponseEntity<List<PeerMapping>> getMappingsForLoggedInPeer(@RequestHeader("Authorization") String token) {
        // Extract email using the service
        String email = peerMappingService.extractEmailFromToken(token.substring(7)); // Remove 'Bearer ' prefix
        List<PeerMapping> peerMappings = peerMappingService.getMappingsByEmail(email);

        return ResponseEntity.ok(peerMappings);
    }


    // Endpoint to update mapping status (Verified or Rejected)
    @PostMapping("/update-status/{mappingId}")
    public ResponseEntity<String> updateMappingStatus(
        @PathVariable String mappingId,
        @RequestBody Map<String, String> requestBody
    ) {
        String status = requestBody.get("status");
        String comment = requestBody.get("comment");

        if (status == null || (!status.equals("Verified") && !status.equals("Rejected"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
        }

        try {
            peerMappingService.updateStatus(mappingId, status, comment);
            return ResponseEntity.ok("Mapping status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating status: " + e.getMessage());
        }
    }
    // Endpoint to handle certificate renewal request (upload renewal PDF, reason, and new expiry date)
    @PostMapping("/renew/{mappingId}")
    public ResponseEntity<String> handleRenewalRequest(
        @PathVariable String mappingId,
        @RequestParam("renewalPdf") MultipartFile renewalPdf,
        @RequestParam("renewalReason") String renewalReason,
        @RequestParam("newExpiryDate") String newExpiryDate
    ) {
        try {
            // Call the service to handle the renewal request
            peerMappingService.handleRenewalRequest(mappingId, renewalPdf, renewalReason, newExpiryDate);
            return ResponseEntity.status(HttpStatus.OK).body("Renewal request processed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing renewal request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing renewal request: " + e.getMessage());
        }
    }

    // Endpoint to verify or reject the renewal request
    @PutMapping("/verify-renewal/{mappingId}")
    public ResponseEntity<String> verifyOrRejectRenewal(
        @PathVariable String mappingId,
        @RequestBody Map<String, String> requestBody
    ) {
        String status = requestBody.get("status"); // "Verified" or "Rejected"
        String comment = requestBody.get("comment"); // Optional comment explaining the decision

        if (status == null || (!status.equals("Verified") && !status.equals("Rejected"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
        }

        try {
            peerMappingService.updateRenewalStatus(mappingId, status);  // Call service method to update status
            peerMappingService.updateStatus(mappingId, status, comment); // Optionally update the status in the PeerMapping
            return ResponseEntity.ok("Renewal request " + status.toLowerCase() + " successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating renewal request: " + e.getMessage());
        }
    }
}
