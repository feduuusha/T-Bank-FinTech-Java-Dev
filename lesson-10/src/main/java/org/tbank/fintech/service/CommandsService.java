package org.tbank.fintech.service;

public interface CommandsService {
    <T> T invokeCommandOfType(String type);
}
