package com.auca.library.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MembershipType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID membershipTypeId;

    private String membershipName; // "GOLD", "SILVER", "STRIVER"
    private int maxBooks;
    private int price; // Rwf per day

    public MembershipType() {
    }

    public MembershipType(String membershipName, int maxBooks, int price) {
        this.membershipName = membershipName;
        this.maxBooks = maxBooks;
        this.price = price;
    }

    public UUID getMembershipTypeId() {
        return membershipTypeId;
    }

    public void setMembershipTypeId(UUID membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public void setMembershipName(String membershipName) {
        this.membershipName = membershipName;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}