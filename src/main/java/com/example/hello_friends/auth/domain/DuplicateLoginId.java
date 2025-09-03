package com.example.hello_friends.auth.domain;

public class DuplicateLoginId extends RuntimeException {

  private static final String MESSAGE = "이미 등록된 아이디 입니다.";

  public DuplicateLoginId(String message) {
    super(message);
  }

  public DuplicateLoginId() {
    this(MESSAGE);
  }
}

