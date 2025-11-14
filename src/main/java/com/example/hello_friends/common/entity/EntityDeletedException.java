package com.example.hello_friends.common.entity;

import com.example.hello_friends.common.response.MotherException;
import org.springframework.http.HttpStatus;

public class EntityDeletedException extends MotherException {
  private static final String MESSAGE = "이미 삭제된 엔티티 입니다.";
  public EntityDeletedException() {
    super(MESSAGE, HttpStatus.BAD_REQUEST);
  }
}

