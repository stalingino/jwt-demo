package com.staling.jwt.demo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class User implements Serializable {

    public static enum UserState {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE");
        private String displayName;
        private UserState(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName(){
            return displayName;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotNull
	@Version
	private Integer version;

	@NotNull
	private String userId;

	private String firstName;
	private boolean activated = false;

	@Enumerated(EnumType.STRING)
	private UserState userState;
}
