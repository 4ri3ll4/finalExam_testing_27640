package com.auca.library.domain;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID membershipId;

    private String membershipCode;
    private LocalDate registrationDate;
    private LocalDate expiringTime;

    @Enumerated(EnumType.STRING)
    private Status membershipStatus;

    // "reader_id" in the diagram — the user who owns this membership
    @ManyToOne
    @JoinColumn(name = "reader_id")
    private User reader;

    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipType membershipType;

    public Membership() {
    }

    public UUID getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(UUID membershipId) {
        this.membershipId = membershipId;
    }

    public String getMembershipCode() {
        return membershipCode;
    }

    public void setMembershipCode(String membershipCode) {
        this.membershipCode = membershipCode;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getExpiringTime() {
        return expiringTime;
    }

    public void setExpiringTime(LocalDate expiringTime) {
        this.expiringTime = expiringTime;
    }

    public Status getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(Status membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
}