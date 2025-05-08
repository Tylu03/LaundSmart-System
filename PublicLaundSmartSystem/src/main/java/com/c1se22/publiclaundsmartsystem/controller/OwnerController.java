package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.request.OwnerWithdrawInfoRequestDto;
import com.c1se22.publiclaundsmartsystem.service.OwnerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/owners")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_OWNER')")
public class OwnerController {
    private final OwnerService ownerService;

    @PutMapping("/update/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateUserToOwner(@PathVariable String username) {
        ownerService.updateUserToOwner(username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/withdraw-info")
    public ResponseEntity<Boolean> updateWithdrawInfo(@RequestBody @Valid OwnerWithdrawInfoRequestDto requestDto) {
        return ResponseEntity.ok(ownerService.updateWithdrawInfo(requestDto));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Boolean> withdraw(@RequestBody ObjectNode body) {
        return ResponseEntity.ok(ownerService.withdraw(body.get("amount").decimalValue()));
    }

    @PutMapping("/withdraw/confirm/{transactionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> confirmWithdraw(@PathVariable Integer transactionId) {
        return ResponseEntity.ok(ownerService.confirmWithdraw(transactionId));
    }

    @PutMapping("/withdraw/cancel/{transactionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> cancelWithdraw(@PathVariable Integer transactionId, @RequestBody ObjectNode body) {
        return ResponseEntity.ok(ownerService.cancelWithdraw(transactionId, body.get("reason").asText()));
    }

    @GetMapping("/withdraw/amount")
    public ResponseEntity<BigDecimal> getWithdrawAmount(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(ownerService.getAmountCanWithdraw(userDetails.getUsername()));
    }

    @GetMapping("/withdraw/history")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> getWithdrawHistory(Authentication authentication,
                                                     @RequestParam(defaultValue = "desc") String sortDir) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(ownerService.getWithdrawHistory(userDetails.getUsername(), sortDir));
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(ownerService.getTotalRevenue());
    }

    @GetMapping("/revenue/month/{month}")
    public ResponseEntity<BigDecimal> getRevenueByMonth(@PathVariable int month) {
        return ResponseEntity.ok(ownerService.getRevenueByMonth(month));
    }

    @GetMapping("/revenue/year/{year}")
    public ResponseEntity<BigDecimal> getRevenueByYear(@PathVariable int year) {
        return ResponseEntity.ok(ownerService.getRevenueByYear(year));
    }

    @GetMapping("/revenue/month/{month}/year/{year}")
    public ResponseEntity<BigDecimal> getRevenueByMonthAndYear(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(ownerService.getRevenueByMonthAndYear(month, year));
    }

    @GetMapping("/revenue/range")
    public ResponseEntity<BigDecimal> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ownerService.getRevenueByDateRange(startDate, endDate));
    }

    @GetMapping("/usage/month/{month}")
    public ResponseEntity<Integer> getNumberOfUsingByMonth(@PathVariable int month) {
        return ResponseEntity.ok(ownerService.getNumberOfUsingByMonth(month));
    }

    @GetMapping("/usage/year/{year}")
    public ResponseEntity<Integer> getNumberOfUsingByYear(@PathVariable int year) {
        return ResponseEntity.ok(ownerService.getNumberOfUsingByYear(year));
    }
}
