package com.synclife.roomreservation.service;

import com.synclife.roomreservation.DTO.RoomDTO;
import com.synclife.roomreservation.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateService {

    private final AppRepository appRepository;


    public void createRoom(long userId, RoomDTO req){
        appRepository.createRoom(req.getRoomName(),
                userId, req.getLocation(), req.getCapacity());
    }
}
