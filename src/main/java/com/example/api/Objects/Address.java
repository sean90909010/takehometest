package com.example.api.objects;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Address {

        @NotBlank(message = "Address line 1 cannot be blank")
        private String line1;
        private String line2;
        private String line3;
        
        @NotBlank(message = "Town cannot be blank")
        private String town;
        
        @NotBlank(message = "County cannot be blank")
        private String county;
        
        @NotBlank(message = "Postcode cannot be blank")
        private String postcode;
    
}
