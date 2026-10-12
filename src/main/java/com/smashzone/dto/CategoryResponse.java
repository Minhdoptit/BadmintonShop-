package com.smashzone.dto;

public record CategoryResponse(
    Integer id,
    String name,
    String description,
    long productCount
) {}
