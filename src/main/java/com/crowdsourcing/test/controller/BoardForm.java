package com.crowdsourcing.test.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class BoardForm {

    @NotEmpty
    private String contentType;
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    private String content;
}
