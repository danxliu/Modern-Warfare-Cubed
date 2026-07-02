package com.paneedah.weaponlib;

import com.paneedah.weaponlib.state.ManagedState;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

public enum WeaponState implements ManagedState<WeaponState> {

    DRAWING,
    READY,
    COMPOUND_REQUESTED,
    COMPOUND_EMTPY_REQUESTED,
    TACTICAL_RELOAD,
    COMPOUND_RELOAD,
    COMPOUND_RELOAD_EMPTY,

    COMPOUND_RELOAD_UNLOAD(null, COMPOUND_REQUESTED, null),
    COMPOUND_RELOAD_FINISH(null, COMPOUND_REQUESTED, null),
    COMPOUND_RELOAD_FINISHED(null, COMPOUND_REQUESTED, null),

    // COMPOUND_RELOAD(null, COMPOUND_REQUESTED, null),
    // COMPOUND_RELOAD_EMPTY(null, COMPOUND_EMTPY_REQUESTED, null),

    LOAD_REQUESTED,
    LOAD(null, LOAD_REQUESTED, null),
    LOAD_ITERATION,
    LOAD_ITERATION_COMPLETED,
    ALL_LOAD_ITERATIONS_COMPLETED, // Applies to iterated loads

    AWAIT_FURTHER_LOAD_INSTRUCTIONS,
    UNLOAD_PREPARING,
    UNLOAD_REQUESTED,
    UNLOAD(UNLOAD_PREPARING, UNLOAD_REQUESTED, READY),

    FIRING(9),
    RECOILED(10),
    PAUSED(10),
    EJECT_REQUIRED,
    EJECTING,

    // STOPPED,
    // EJECT_SPENT_ROUND_REQUIRED,
    // EJECTED_SPENT_ROUND,

    MODIFYING_REQUESTED(1),
    MODIFYING(2, null, MODIFYING_REQUESTED, null),

    NEXT_ATTACHMENT_REQUESTED,
    NEXT_ATTACHMENT(2, null, NEXT_ATTACHMENT_REQUESTED, null),

    ALERT,
    INSPECTING;

    private static final int DEFAULT_PRIORITY = 0;

    private final WeaponState preparingPhase;
    private final WeaponState permitRequestedPhase;
    private final WeaponState commitPhase;
    @Getter private final int priority;

    WeaponState() {
        this(null, null, null);
    }

    WeaponState(int priority) {
        this(priority, null, null, null);
    }

    WeaponState(WeaponState preparingPhase, WeaponState permitRequestedState, WeaponState transactionFinalState) {
        this(DEFAULT_PRIORITY, preparingPhase, permitRequestedState, transactionFinalState);
    }

    WeaponState(int priority, WeaponState preparingPhase, WeaponState permitRequestedState, WeaponState transactionFinalState) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.permitRequestedPhase = permitRequestedState;
        this.commitPhase = transactionFinalState;
        // This is required to have up-to-date state on server, e.g. preparing, requested;
        // Otherwise issues arise, e.g. item toss would not work correctly
    }
    @Override
    public WeaponState preparingPhase() {
        return preparingPhase;
    }

    @Override
    public WeaponState permitRequestedPhase() {
        return permitRequestedPhase;
    }

    @Override
    public WeaponState commitPhase() {
        return commitPhase;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        // No need to initialize anything, type registry will take care of everything
    }

    @Override
    public void write(ByteBuf byteBuf) {
        // No need to write anything, parent type registry should take care of it
    }
}

