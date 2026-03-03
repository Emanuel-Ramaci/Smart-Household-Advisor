package com.ramaci.energy_app.runner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ramaci.energy_app.repository.RoleRepository;
import com.ramaci.energy_app.repository.PermissionRepository;
import com.ramaci.energy_app.repository.UserRepository;
import com.ramaci.energy_app.repository.EnergyConsumptionRepository;
import com.ramaci.energy_app.repository.ExpenseRepository;
import com.ramaci.energy_app.repository.HouseholdMemberRepository;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.lambdaworks.crypto.SCryptUtil;
import com.ramaci.energy_app.model.EnergyConsumption;
import com.ramaci.energy_app.model.Expense;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.model.HouseholdMember;
import com.ramaci.energy_app.model.Role;
import com.ramaci.energy_app.model.Permission;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

        private final RoleRepository roleRepository;
        private final PermissionRepository permissionRepository;
        private final HouseholdRepository householdRepository;
        private final UserRepository userRepository;
        private final HouseholdMemberRepository householdMemberRepository;
        private final EnergyConsumptionRepository energyConsumptionRepository;
        private final ExpenseRepository expenseRepository;

        public DataInitializer(RoleRepository roleRepository,
                        PermissionRepository permissionRepository,
                        HouseholdRepository householdRepository,
                        UserRepository userRepository,
                        HouseholdMemberRepository householdMemberRepository,
                        EnergyConsumptionRepository energyConsumptionRepository,
                        ExpenseRepository expenseRepository) {
                this.roleRepository = roleRepository;
                this.permissionRepository = permissionRepository;
                this.householdRepository = householdRepository;
                this.userRepository = userRepository;
                this.householdMemberRepository = householdMemberRepository;
                this.energyConsumptionRepository = energyConsumptionRepository;
                this.expenseRepository = expenseRepository;
        }

        private Permission createPermissionIfNotExists(String name, String resource) {
                return permissionRepository.findByName(name)
                                .orElseGet(() -> permissionRepository.save(new Permission(name, resource)));
        }

        @Override
        public void run(String... args) throws Exception {

                // Creazione dei permessi
                Permission adminCreateUser = createPermissionIfNotExists("ADMIN_CREATE_USER", "USER");
                Permission adminReadAll = createPermissionIfNotExists("ADMIN_READ_ALL_USERS", "USER");
                Permission adminToggle = createPermissionIfNotExists("ADMIN_ACTIVATE_DEACTIVATE_USER", "USER");
                Permission adminDelete = createPermissionIfNotExists("ADMIN_DELETE_USER", "USER");

                Permission userReadOwn = createPermissionIfNotExists("USER_READ_OWN_PROFILE", "USER");
                Permission userUpdateOwn = createPermissionIfNotExists("USER_UPDATE_OWN_PROFILE", "USER");
                Permission userChangePass = createPermissionIfNotExists("USER_CHANGE_OWN_PASSWORD", "USER");
                Permission userDeactivate = createPermissionIfNotExists("USER_DEACTIVATE_OWN_ACCOUNT", "USER");

                Permission heatingSimulation = createPermissionIfNotExists("USER_RUN_HEATING_SIMULATION", "SIMULATION");
                Permission standBySimulation = createPermissionIfNotExists("USER_RUN_STANDBY_SIMULATION", "SIMULATION");
                Permission timeShiftSimulation = createPermissionIfNotExists("USER_RUN_TIMESHIFT_SIMULATION",
                                "SIMULATION");

                Permission userReadSimRes = createPermissionIfNotExists("USER_READ_SIMULATION_RESULTS", "SIMULATION");

                // Creazione dei ruoli
                Role adminRole = roleRepository.findByName("ADMIN")
                                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

                Role userRole = roleRepository.findByName("USER")
                                .orElseGet(() -> roleRepository.save(new Role("USER")));

                // Associazione dei permessi ai ruoli
                adminRole.setPermissions(Set.of(
                                adminCreateUser,
                                adminReadAll,
                                adminToggle,
                                adminDelete,
                                heatingSimulation,
                                standBySimulation,
                                timeShiftSimulation));

                userRole.setPermissions(Set.of(
                                userReadOwn,
                                userUpdateOwn,
                                userChangePass,
                                userDeactivate,
                                heatingSimulation,
                                standBySimulation,
                                timeShiftSimulation,
                                userReadSimRes));

                roleRepository.save(adminRole);
                roleRepository.save(userRole);

                // Household
                List<Household> households = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                        Household h = new Household();
                        h.setName("Household " + i);
                        h.setAddress("Via " + i);
                        h.setCity("Città " + i);
                        h.setPostalCode(10000 + i);
                        h.setSquareMeters(BigDecimal.valueOf(50 + i * 20));
                        h.setEnergyClass(i % 2 == 0 ? "A" : "B");
                        h.setResidentsCount(2 + i % 3);
                        h.setEnergyTariff(BigDecimal.valueOf(0.10 + i * 0.05));
                        h.setCreatedAt(LocalDateTime.now());
                        h.setUpdatedAt(LocalDateTime.now());
                        householdRepository.save(h);
                        households.add(h);
                }

                // Utenti
                List<User> users = new ArrayList<>();

                // 15 utenti con ruolo USER
                for (int i = 1; i <= 15; i++) {
                        User u = new User();
                        u.setEmail("user" + i + "@example.com");
                        u.setFirstName("UserFirst" + i);
                        u.setLastName("UserLast" + i);
                        u.setPassword(SCryptUtil.scrypt("password" + i, 32768, 8, 1));
                        u.setActive(true);
                        u.setCreatedAt(new Date());
                        u.setRoles(Set.of(userRole));
                        userRepository.save(u);
                        users.add(u);
                }

                // 3 utenti con ruolo ADMIN (1 di loro ha anche il ruolo USER)
                for (int i = 1; i <= 3; i++) {
                        User a = new User();
                        a.setEmail("admin" + i + "@example.com");
                        a.setFirstName("AdminFirst" + i);
                        a.setLastName("AdminLast" + i);
                        a.setPassword(SCryptUtil.scrypt("password" + i, 32768, 8, 1));
                        a.setActive(true);
                        a.setCreatedAt(new Date());
                        if (i == 3) {
                                a.setRoles(Set.of(adminRole, userRole));
                        } else {
                                a.setRoles(Set.of(adminRole));
                        }
                        userRepository.save(a);
                        users.add(a);
                }

                // HouseholdMember (16 membri, circa 2-3 per household)
                int userIndex = 0;
                for (Household h : households) {
                        int membersCount = 2 + new Random().nextInt(2); // 2 o 3 membri
                        for (int i = 0; i < membersCount && userIndex < users.size(); i++, userIndex++) {
                                HouseholdMember hm = new HouseholdMember();
                                hm.setHousehold(h);
                                hm.setUser(users.get(userIndex));
                                hm.setMembershipType(i == 0 ? "PROPRIETARIO" : "INQUILINO"); // Il primo membro
                                                                                             // associato ad una casa è
                                                                                             // proprietario
                                householdMemberRepository.save(hm);
                        }
                }

                // EnergyConsumption (circa 3-4 per household)
                for (Household h : households) {
                        int consumptionsCount = 3 + new Random().nextInt(2); // 3 o 4
                        for (int i = 1; i <= consumptionsCount; i++) {
                                EnergyConsumption ec = new EnergyConsumption();
                                ec.setHousehold(h);
                                ec.setCategory("Category " + i);
                                ec.setConsumptionKwh(BigDecimal.valueOf(50 + new Random().nextInt(200)));
                                ec.setReferenceDate(LocalDate.now().minusDays(new Random().nextInt(30)));
                                ec.setCreatedAt(LocalDateTime.now());
                                ec.setUpdatedAt(LocalDateTime.now());
                                energyConsumptionRepository.save(ec);
                        }
                }

                // Expense (circa 1-2 per household)
                for (Household h : households) {
                        int expenseCount = 1 + new Random().nextInt(2); // 1 o 2
                        for (int i = 1; i <= expenseCount; i++) {
                                Expense exp = new Expense();
                                exp.setHousehold(h);
                                exp.setCategory("Expense Cat " + i);
                                exp.setAmount(BigDecimal.valueOf(10 + new Random().nextInt(100)));
                                exp.setReferenceDate(LocalDate.now().minusDays(new Random().nextInt(30)));
                                exp.setDescription("Description " + i);
                                exp.setCreatedAt(LocalDateTime.now());
                                expenseRepository.save(exp);
                        }
                }

                System.out.println("DataInitializer completato: " + households.size() + " household, "
                                + users.size() + " utenti creati.");
        }
}
