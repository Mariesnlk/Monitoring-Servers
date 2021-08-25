package com.serverMonitor.database.enteties.telegram;

import com.serverMonitor.database.enteties.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Data
@Entity
@NoArgsConstructor
@Table(name = "invitation_code")
public class InvitationCode extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CodeStatus status;

}
