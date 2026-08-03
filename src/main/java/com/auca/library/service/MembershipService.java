package com.auca.library.service;

import java.time.LocalDate;
import java.util.UUID;

import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Status;
import com.auca.library.domain.User;

public class MembershipService {

    private final MembershipDao membershipDao;
    private final MembershipTypeDao membershipTypeDao;
    private final UserDao userDao;

    public MembershipService(MembershipDao membershipDao, MembershipTypeDao membershipTypeDao, UserDao userDao) {
        this.membershipDao = membershipDao;
        this.membershipTypeDao = membershipTypeDao;
        this.userDao = userDao;
    }

    public Membership registerMembership(UUID userId, UUID membershipTypeId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("No user found for id: " + userId);
        }

        MembershipType type = membershipTypeDao.findById(membershipTypeId);
        if (type == null) {
            throw new IllegalArgumentException("No membership type found for id: " + membershipTypeId);
        }

        // Business rule: a user cannot register a second membership while
        // one is already PENDING or APPROVED.
        Membership existingActive = membershipDao.findActiveMembershipByReaderId(userId);
        if (existingActive != null) {
            throw new IllegalStateException("User already has an active membership");
        }

        Membership membership = new Membership();
        membership.setMembershipCode("MEM-" + System.currentTimeMillis()); // simple unique-ish code
        membership.setRegistrationDate(LocalDate.now());
        membership.setExpiringTime(LocalDate.now().plusYears(1)); // arbitrary 1-year validity — assignment doesn't specify
        membership.setMembershipStatus(Status.PENDING); // new registrations start PENDING, per test name
        membership.setReader(user);
        membership.setMembershipType(type);

        return membershipDao.save(membership);
    }
}