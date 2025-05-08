package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.response.UsageHistoryDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserUsageDto;
import com.c1se22.publiclaundsmartsystem.service.UsageHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usage-histories")
@AllArgsConstructor
public class UsageHistoryController {
    UsageHistoryService usageHistoryService;
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<UsageHistoryDto>> getAllUsageHistories() {
        return ResponseEntity.ok(usageHistoryService.getAllUsageHistories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageHistoryDto> getUsageHistoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(usageHistoryService.getUsageHistoryById(id));
    }

//    @PatchMapping("/{id}/complete")
//    public ResponseEntity<Boolean> completeUsageHistory(@PathVariable Integer id) {
//        usageHistoryService.completeUsageHistory(id);
//        return ResponseEntity.ok(true);
//    }

    @GetMapping("/between")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<UsageHistoryDto>> getUsageHistoriesBetween(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getUsageHistoriesBetween(startDate, endDate));
    }

    @GetMapping("/count/washing-type")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<Map<String, Long>> getUsageCountByWashingType(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getUsageCountByWashingType(startDate, endDate));
    }

    @GetMapping("/revenue/washing-type")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueByWashingType(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getRevenueByWashingType(startDate, endDate));
    }

    @GetMapping("/top-users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<UserUsageDto>> getTopUsers(@RequestParam String start, @RequestParam String end, @RequestParam int limit) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getTopUsers(startDate, endDate, limit));
    }

    @GetMapping("/user-usage-count")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<Map<String, Long>> getUserUsageCount(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getUserUsageCount(startDate, endDate));
    }

    @GetMapping("/total-revenue")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<BigDecimal> getTotalRevenue(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getTotalRevenue(startDate, endDate));
    }

    @GetMapping("/total-usage-count")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<Long> getTotalUsageCount(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(usageHistoryService.getTotalUsageCount(startDate, endDate));
    }

    @GetMapping("/user")
    public ResponseEntity<List<UsageHistoryDto>> getUsageHistoriesByUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(usageHistoryService.getUsageHistoriesByUsername(userDetails.getUsername()));
    }
}
