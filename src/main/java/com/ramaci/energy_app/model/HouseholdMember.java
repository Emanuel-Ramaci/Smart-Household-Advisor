package com.ramaci.energy_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "household_members", uniqueConstraints = @UniqueConstraint(columnNames = { "household_id", "user_id" }))
public class HouseholdMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String membershipType;

    public HouseholdMember() {
    }

    public HouseholdMember(Long id, Household household, User user, String membershipType) {
        this.id = id;
        this.household = household;
        this.user = user;
        this.membershipType = membershipType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

}
