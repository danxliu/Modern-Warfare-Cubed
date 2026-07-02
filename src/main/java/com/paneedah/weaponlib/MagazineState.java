package com.paneedah.weaponlib;

import com.paneedah.weaponlib.state.ManagedState;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

public enum MagazineState implements ManagedState<MagazineState> {

    READY,
    LOAD_REQUESTED,
    LOAD(null, LOAD_REQUESTED, null),
    UNLOAD_REQUESTED,
    UNLOAD(null, UNLOAD_REQUESTED, null);

    private static final int DEFAULT_PRIORITY = 0;

    private final MagazineState preparingPhase;

    private final MagazineState permitRequestedPhase;

    private final MagazineState commitPhase;

    @Getter private final int priority;

    MagazineState() {
        this(null, null, null);
    }

    MagazineState(int priority) {
        this(priority, null, null, null);
    }

//	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState) {
//		this(permitRequestedState, transactionFinalState);
//	}

    MagazineState(MagazineState preparingPhase, MagazineState permitRequestedState, MagazineState transactionFinalState) {
        this(DEFAULT_PRIORITY, preparingPhase, permitRequestedState, transactionFinalState);
    }

    MagazineState(int priority, MagazineState preparingPhase, MagazineState permitRequestedState, MagazineState transactionFinalState) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.permitRequestedPhase = permitRequestedState;
        this.commitPhase = transactionFinalState;
        //this is required to have up-to-date state on server, e.g. preparing, requested;
        // otherwise issus arise, e.g. item toss would not work correctly
    }
    @Override
    public MagazineState preparingPhase() {
        return preparingPhase;
    }

    @Override
    public MagazineState permitRequestedPhase() {
        return permitRequestedPhase;
    }


    @Override
    public MagazineState commitPhase() {
        return commitPhase;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        // not need to initialize anything, type registry will take care of everything
    }

    @Override
    public void write(ByteBuf byteBuf) {
        // not need to write anything, parent type registry should take care of it
    }
}
