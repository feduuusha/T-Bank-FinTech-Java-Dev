package org.tbank.fintech.command;

import java.util.concurrent.Callable;

public interface Command<V> extends Callable<V> {
    String getType();
}
