package com.example.api;

import lombok.Builder;

@Builder
public class Address {

        private String line1;
        private String line2;
        private String line3;
        private String town;
        private String county;
        private String postcode;

        public Address(String line1, String line2, String line3, String town, String county, String postcode) {
            setLine1(line1);
            setLine2(line2);
            setLine3(line3);
            setTown(town);
            setCounty(county);
            setPostcode(postcode);
        }

        public String getLine1() {
            return line1;
        }
        public void setLine1(String line1) {
            this.line1 = line1;
        }
        public String getLine2() {
            return line2;
        }
        public void setLine2(String line2) {

            if(line2 == null){
                line2 = "";
            }
            this.line2 = line2;
        }
        public String getLine3() {
            return line3;
        }
        public void setLine3(String line3) {

            if(line3 == null){
                line3 = "";
            }
            this.line3 = line3;
        }
        public String getTown() {
            return town;
        }
        public void setTown(String town) {
            this.town = town;
        }
        public String getCounty() {
            return county;
        }
        public void setCounty(String county) {
            this.county = county;
        }
        public String getPostcode() {
            return postcode;
        }
        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }
    
}
