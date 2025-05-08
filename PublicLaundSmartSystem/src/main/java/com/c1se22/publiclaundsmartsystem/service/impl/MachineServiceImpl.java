package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.*;
import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.enums.ReservationStatus;
import com.c1se22.publiclaundsmartsystem.enums.UsageHistoryStatus;
import com.c1se22.publiclaundsmartsystem.event.handler.ReservationEventHandler;
import com.c1se22.publiclaundsmartsystem.event.handler.WashingCompleteEventHandler;
import com.c1se22.publiclaundsmartsystem.event.handler.WashingNearCompleteEventHandler;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.payload.internal.FirebaseMachine;
import com.c1se22.publiclaundsmartsystem.enums.MachineStatus;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.request.MachineCreateDto;
import com.c1se22.publiclaundsmartsystem.payload.response.MachineAndTimeDto;
import com.c1se22.publiclaundsmartsystem.payload.request.MachineDto;
import com.c1se22.publiclaundsmartsystem.repository.*;
import com.c1se22.publiclaundsmartsystem.service.MachineService;
import com.c1se22.publiclaundsmartsystem.service.NotificationService;
import com.c1se22.publiclaundsmartsystem.service.OwnerService;
import com.google.firebase.database.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MachineServiceImpl implements MachineService{
    @Value("${app.machine.secret}")
    String machineSecret;
    MachineRepository machineRepository;
    LocationRepository locationRepository;
    UsageHistoryRepository usageHistoryRepository;
    ReservationRepository reservationRepository;
    UserRepository userRepository;
    NotificationService notificationService;
    RoleRepository roleRepository;
    OwnerService ownerService;
    FirebaseDatabase firebaseDatabase;
    WashingCompleteEventHandler washingCompleteEventHandler;
    WashingNearCompleteEventHandler washingNearCompleteEventHandler;
    ReservationEventHandler reservationEventHandler;

    public MachineServiceImpl(MachineRepository machineRepository,
                              LocationRepository locationRepository,
                              UsageHistoryRepository usageHistoryRepository,
                              ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              OwnerService ownerService,
                              FirebaseDatabase firebaseDatabase,
                              NotificationService notificationService,
                              WashingCompleteEventHandler washingCompleteEventHandler,
                              WashingNearCompleteEventHandler washingNearCompleteEventHandler,
                              ReservationEventHandler reservationEventHandler) {
        this.machineRepository = machineRepository;
        this.locationRepository = locationRepository;
        this.usageHistoryRepository = usageHistoryRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.roleRepository = roleRepository;
        this.ownerService = ownerService;
        this.firebaseDatabase = firebaseDatabase;
        this.notificationService = notificationService;
        this.washingCompleteEventHandler = washingCompleteEventHandler;
        this.washingNearCompleteEventHandler = washingNearCompleteEventHandler;
        this.reservationEventHandler = reservationEventHandler;
    }

    @Override
    public List<MachineDto> getAllMachines() {
        return machineRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<MachineDto> getMachines(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase(
                Sort.Direction.ASC.name()) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return machineRepository.findAll(pageable).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public MachineDto getMachineById(Integer id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id.toString()));
        return mapToDto(machine);
    }

    @Override
    @Loggable
    @Transactional(rollbackFor = Exception.class)
    public MachineDto addMachine(MachineCreateDto machineDto) {
        Location location;
        if (machineDto.getLocationId() != null){
            location = locationRepository.findById(machineDto.getLocationId()).orElseThrow(() ->
                    new ResourceNotFoundException("Location", "id", machineDto.getLocationId().toString()));
        } else{
            location = Location.builder()
                    .name(machineDto.getLocationName())
                    .address(machineDto.getLocationAddress())
                    .city(machineDto.getLocationCity())
                    .district(machineDto.getLocationDistrict())
                    .ward(machineDto.getLocationWard())
                    .lng(0.0)
                    .lat(0.0)
                    .build();
            location = locationRepository.save(location);
        }
        if (machineRepository.existsBySecretId(machineDto.getSecretId()))
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.RESOURCE_EXISTS, "SecretId already exists");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(machineSecret, machineDto.getHashKey()))
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_HASH);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsernameOrEmail(authentication.getName(), authentication.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", authentication.getName()));
        Role role = roleRepository.findByName("ROLE_OWNER").orElseThrow(() ->
                new ResourceNotFoundException("Role", "name", "ROLE_OWNER"));
        if (!user.getRoles().contains(role)){
            ownerService.updateUserToOwner(user.getUsername());
        }
        Machine machine = Machine.builder()
                .secretId(machineDto.getSecretId())
                .name(machineDto.getName())
                .model(machineDto.getModel())
                .capacity(machineDto.getCapacity())
                .status(MachineStatus.AVAILABLE)
                .location(location)
                .lastMaintenanceDate(LocalDate.now())
                .installationDate(LocalDate.now())
                .user(user)
                .build();
        Machine newMachine = machineRepository.save(machine);
        FirebaseMachine firebaseMachine = FirebaseMachine.builder()
                .id(machine.getSecretId())
                .status(String.valueOf(machine.getStatus()))
                .duration(0)
                .build();
        firebaseDatabase.getReference("WashingMachineList").child(firebaseMachine.getId()).setValueAsync(firebaseMachine);
        log.info("Machine {} has been added", newMachine.getId());
        return mapToDto(newMachine);
    }

    @Override
    @Loggable
    public MachineDto updateMachine(Integer id, MachineDto machineDto) {
        Machine machine = machineRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id.toString()));
        Location location = locationRepository.findById(machineDto.getLocationId()).orElseThrow(() ->
                new ResourceNotFoundException("Location", "id", machineDto.getLocationId().toString()));
        machine.setName(machineDto.getName());
        machine.setModel(machineDto.getModel());
        machine.setCapacity(machineDto.getCapacity());
        machine.setLocation(location);
        return mapToDto(machineRepository.save(machine));
    }

    @Override
    @Loggable
    public void deleteMachine(Integer id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id.toString()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsernameOrEmail(authentication.getName(), authentication.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", authentication.getName()));
        if (!machine.getUser().getId().equals(user.getId()))
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
        UsageHistory usageHistories = usageHistoryRepository.findByMachineIdAndStatus(id, UsageHistoryStatus.IN_PROGRESS);
        if (usageHistories != null)
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.MACHINE_IN_USE);
        Reservation reservation = reservationRepository.findByMachineIdAndStatus(id, ReservationStatus.PENDING);
        if (reservation != null)
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.MACHINE_NOT_AVAILABLE);
        machine.setStatus(MachineStatus.DISABLED);
        firebaseDatabase.getReference("WashingMachineList").child(machine.getSecretId())
                .setValueAsync(MachineStatus.DISABLED.name());
        log.info("Machine {} has been disabled", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public MachineDto updateMachineStatus(Integer id, String status) {
        Machine machine = machineRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id.toString()));
        MachineStatus machineStatus = MachineStatus.valueOf(status.toUpperCase());
        machine.setStatus(machineStatus);
        Machine updatedMachine = machineRepository.save(machine);
        firebaseDatabase.getReference("WashingMachineList").child(machine.getSecretId()).child("status")
                .setValueAsync(status);
        log.info("Machine {} status has been updated to {}", id, status);
        return mapToDto(updatedMachine);
    }

    @Override
    public MachineDto getMachineAreBeingReservedByUser(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", username));
        return machineRepository.findMachineAreBeingReservedByUser(user.getId()).map(this::mapToDto).orElse(null);
    }

    @Override
    public List<MachineAndTimeDto> getMachinesAreBeingUsedByUser(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", username));
        List<Machine> machines = machineRepository.findMachinesAreBeingUsedByUser(user.getId());
        List<Integer> machineIds = machines.stream().map(Machine::getId).toList();
        List<UsageHistory> usageHistories = usageHistoryRepository.findByCurrentUsedMachineIdsAndUserId(machineIds, user.getId());
        return usageHistories.stream().map(usageHistory -> mapToMachineAndTimeDto(usageHistory.getMachine(), usageHistory)).collect(Collectors.toList());
    }

    @Override
    public List<MachineDto> getMachinesByOwnerId(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id.toString()));
        return machineRepository.findMachinesByOwnerId(user.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<MachineDto> getMachinesForCurrentOwner(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", username));
        return machineRepository.findMachinesByOwnerId(user.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public void updateMachineErrorStatus(String id) {
        log.info("Updating machine {} status to ERROR", id);
        Machine machine = machineRepository.findBySecretId(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id));
        MachineStatus machineStatus = MachineStatus.ERROR;
        machine.setStatus(machineStatus);
        Machine updatedMachine = machineRepository.save(machine);
        firebaseDatabase.getReference("WashingMachineList").child(updatedMachine.getSecretId())
                .child("status").setValueAsync(machineStatus.name());
        User owner = userRepository.findById(updatedMachine.getUser().getId()).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", updatedMachine.getUser().getId().toString()));
        notificationService.sendNotification(owner.getId(), "Máy " + updatedMachine.getName() + " đã ngừng hoạt động, vui lòng kiểm tra.");
        Reservation reservation = reservationRepository.findByMachineIdAndStatus(updatedMachine.getId(), ReservationStatus.PENDING);
        if (reservation != null) {
            reservation.setStatus(ReservationStatus.ARCHIVED);
            reservationRepository.save(reservation);
            log.info("Reservation {} status has been updated to CANCELLED", reservation.getId());
            reservationEventHandler.cancelTask("reservation-" + reservation.getId());
            User user = userRepository.findById(reservation.getUser().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("User", "id", reservation.getUser().getId().toString()));
            notificationService.sendNotification(user.getId(),
                    "Máy " + updatedMachine.getName() + " đã ngừng hoạt động, xin lỗi vì sự bất tiện này. Đơn đặt hàng của bạn đã bị hủy.");
            log.info("Machine {} status has been updated to ERROR", id);
            return;
        }
        UsageHistory usageHistory = usageHistoryRepository.findByMachineIdAndStatus(updatedMachine.getId(),
                UsageHistoryStatus.IN_PROGRESS);
        if (usageHistory != null) {
            usageHistory.setStatus(UsageHistoryStatus.FAILED);
            usageHistoryRepository.save(usageHistory);
            log.info("UsageHistory {} status has been updated to FAILED", usageHistory.getUsageId());
            washingCompleteEventHandler.cancelTask("complete-" + usageHistory.getUsageId());
            washingNearCompleteEventHandler.cancelTask("near-complete-" + usageHistory.getUsageId());
            log.info("Scheduled tasks for usageHistory {} have been cancelled", usageHistory.getUsageId());
            User user = userRepository.findById(usageHistory.getUser().getId()).orElseThrow(() ->
                    new ResourceNotFoundException("User", "id", usageHistory.getUser().getId().toString()));
            user.setBalance(user.getBalance().add(usageHistory.getCost()));
            userRepository.save(user);
            log.info("User {} balance has been updated to {}", user.getId(), user.getBalance());
            notificationService.sendNotification(user.getId(),
                    "Máy " + updatedMachine.getName() + " đã ngừng hoạt động, xin lỗi vì sự bất tiện này. Số tiền " +
                            usageHistory.getCost().toPlainString() + " đã được hoàn lại. Hãy tới trạm để lấy quần áo của bạn.");
        }
        log.info("Machine {} status has been updated to ERROR", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public void updateMachineActiveStatus(String id) {
        log.info("Updating machine {} status to ACTIVE", id);
        Machine machine = machineRepository.findBySecretId(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id));
        MachineStatus machineStatus = MachineStatus.AVAILABLE;
        machine.setStatus(machineStatus);
        Machine updatedMachine = machineRepository.save(machine);
        firebaseDatabase.getReference("WashingMachineList").child(updatedMachine.getSecretId())
                .child("status").setValueAsync(machineStatus.name());
        firebaseDatabase.getReference("WashingMachineList").child(updatedMachine.getSecretId())
                .child("RealtimeStatus").setValueAsync("OFF");
        log.info("Machine {} status has been updated to ACTIVE", id);
    }

    @Override
    public boolean checkMachineHashKey(String hashKey) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(machineSecret, hashKey))
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_HASH);
        return true;
    }

    @Override
    public boolean checkMachineRTStatus(Integer id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Machine", "id", id.toString()));
            
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference ref = firebaseDatabase.getReference("WashingMachineList").child(machine.getSecretId());
        
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String rtStatus = dataSnapshot.getValue(String.class);
                if ("READY".equals(rtStatus)) {
                    future.complete(true);
                    ref.child("RealtimeStatus").removeEventListener(this);
                } else if (rtStatus != null && !"OFF".equals(rtStatus)) {
                    // Don't complete the future yet if we get a non-READY status
                    log.debug("Received status: {} for machine {}", rtStatus, id);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
                log.error("Error checking machine RT status: {}", error.getMessage());
            }
        };
        
        // Add listener before setting status
        ref.child("RealtimeStatus").addValueEventListener(listener);
        ref.child("status").setValueAsync("CHECK");
        
        try {
            // Wait up to 5 seconds for READY response
            boolean isWorking = future.get(5, TimeUnit.SECONDS);
            
            if (isWorking) {
                ref.child("status").setValueAsync(machine.getStatus().toString());
                ref.child("RealtimeStatus").setValueAsync("OFF");
                log.info("Machine {} is working properly", id);
            } else {
                machine.setStatus(MachineStatus.ERROR);
                machineRepository.save(machine);
                ref.child("status").setValueAsync("ERROR");
                log.warn("Machine {} did not respond with READY status within timeout", id);
            }
            return isWorking;
            
        } catch (Exception e) {
            // Cleanup and handle timeout/error
            ref.child("RealtimeStatus").removeEventListener(listener);
            log.error("Error while checking machine status: {}", e.getMessage());
            machine.setStatus(MachineStatus.ERROR);
            machineRepository.save(machine);
            ref.child("status").setValueAsync("ERROR");
            return false;
        }
    }

    private MachineDto mapToDto(Machine machine) {
        MachineDto machineDto = MachineDto.builder()
                .id(machine.getId())
                .secretId(machine.getSecretId())
                .name(machine.getName())
                .model(machine.getModel())
                .capacity(machine.getCapacity())
                .status(String.valueOf(machine.getStatus()))
                .build();
        if (machine.getLocation() != null) {
            machineDto.setLocationId(machine.getLocation().getId());
            machineDto.setLocationName(machine.getLocation().getName());
            machineDto.setLocationAddress(machine.getLocation().getAddress());
            machineDto.setLocationCity(machine.getLocation().getCity());
            machineDto.setLocationDistrict(machine.getLocation().getDistrict());
            machineDto.setLocationWard(machine.getLocation().getWard());
            machineDto.setLocationLng(machine.getLocation().getLng());
            machineDto.setLocationLat(machine.getLocation().getLat());
        }
        return machineDto;
    }

    private MachineAndTimeDto mapToMachineAndTimeDto(Machine machine, UsageHistory usageHistory) {
        MachineAndTimeDto machineAndTimeDto = MachineAndTimeDto.builder()
                .id(machine.getId())
                .secretId(machine.getSecretId())
                .name(machine.getName())
                .model(machine.getModel())
                .capacity(machine.getCapacity())
                .status(String.valueOf(machine.getStatus()))
                .startTime(usageHistory.getStartTime())
                .endTime(usageHistory.getEndTime())
                .build();
        if (machine.getLocation() != null) {
            machineAndTimeDto.setLocationId(machine.getLocation().getId());
            machineAndTimeDto.setLocationName(machine.getLocation().getName());
            machineAndTimeDto.setLocationAddress(machine.getLocation().getAddress());
            machineAndTimeDto.setLocationCity(machine.getLocation().getCity());
            machineAndTimeDto.setLocationDistrict(machine.getLocation().getDistrict());
            machineAndTimeDto.setLocationWard(machine.getLocation().getWard());
            machineAndTimeDto.setLocationLng(machine.getLocation().getLng());
            machineAndTimeDto.setLocationLat(machine.getLocation().getLat());
        }
        return machineAndTimeDto;
    }
}
