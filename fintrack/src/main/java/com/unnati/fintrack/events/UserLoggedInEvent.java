package com.unnati.fintrack.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoggedInEvent {

    private Long userId;
    private String name;
    private String email;
}