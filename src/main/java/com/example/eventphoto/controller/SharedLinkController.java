package com.example.eventphoto.controller;

import com.example.eventphoto.dto.*;
import com.example.eventphoto.model.Customer;
import com.example.eventphoto.security.JwtPrincipal;
import com.example.eventphoto.service.CustomerService;
import com.example.eventphoto.service.ShareLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared")
@RequiredArgsConstructor
public class SharedLinkController {

    private final ShareLinkService shareLinkService;
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SharedLinkResponse>> create(@Valid @RequestBody SharedLinkCreateRequest request) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerService.findById(principal.getCustomerId());
        SharedLinkResponse response = shareLinkService.create(request, principal.getCustomerId(), customer);
        return ResponseEntity.ok(ApiResponse.success("Share link created", response));
    }

    @GetMapping("/public/{shareCode}")
    public ResponseEntity<ApiResponse<SharedLinkResponse>> getPublic(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password) {
        SharedLinkResponse response = shareLinkService.getByShareCodePublic(shareCode, password);
        return ResponseEntity.ok(ApiResponse.success("OK", response));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<SharedLinkResponse>>> listMine() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SharedLinkResponse> list = shareLinkService.listByCustomer(principal.getCustomerId());
        return ResponseEntity.ok(ApiResponse.success("OK", list));
    }
}
