package com.certi;

public class PeerMappingWithCertificateDetails {

    private PeerMapping peerMapping;
    private Certificate certificate;

    public PeerMappingWithCertificateDetails(PeerMapping peerMapping, Certificate certificate) {
        this.peerMapping = peerMapping;
        this.certificate = certificate;
    }

    public PeerMapping getPeerMapping() {
        return peerMapping;
    }

    public void setPeerMapping(PeerMapping peerMapping) {
        this.peerMapping = peerMapping;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
