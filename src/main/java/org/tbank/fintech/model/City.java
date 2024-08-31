package org.tbank.fintech.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class City {
    private String slug;
    private Coordinates coords;
}
