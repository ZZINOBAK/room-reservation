package com.synclife.roomreservation.service;

import com.synclife.roomreservation.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CancleService {

    private final AppRepository appRepository;

}
