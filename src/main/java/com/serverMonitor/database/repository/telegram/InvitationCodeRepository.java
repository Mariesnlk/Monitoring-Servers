package com.serverMonitor.database.repository.telegram;

import com.serverMonitor.database.enteties.telegram.InvitationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitationCodeRepository extends CrudRepository<InvitationCode, String> {

    Optional<InvitationCode> findByName(String name);

}

