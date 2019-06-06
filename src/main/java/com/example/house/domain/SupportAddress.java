package com.example.house.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class SupportAddress {
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid",strategy="uuid")
    private String id;

    private String belongTo;

    private String enName;

    private String level;

    private String longitude;

    private String latitude;

    private String cnName;
    public enum Level{
        CITY("city"),
        REGION("region");

        private String value;

        Level(String value){
            this.value = value;
        }

        public static Level of(String value){
            for (Level level : Level.values()) {
                if (level.value.equals(value)){
                    return level;
                }
            }
            throw new IllegalArgumentException();
        }

        public String getValue() {
            return value;
        }
    }
}
