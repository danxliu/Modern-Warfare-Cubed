package com.paneedah.weaponlib.electronics;

import com.paneedah.mwc.network.TypeRegistry;
import com.paneedah.weaponlib.state.ManagedState;
import io.netty.buffer.ByteBuf;

public enum TabletState implements ManagedState<TabletState> {

    READY,

    MODIFYING_REQUESTED(1),

    MODIFYING(2, null, MODIFYING_REQUESTED, null),

    NEXT_ATTACHMENT_REQUESTED,

    NEXT_ATTACHMENT(2, null, NEXT_ATTACHMENT_REQUESTED, null),

    ALERT;

    private static final int DEFAULT_PRIORITY = 0;

    private final TabletState preparingPhase;

    private final TabletState permitRequestedPhase;

    private final TabletState commitPhase;

    private int priority = DEFAULT_PRIORITY;

    TabletState() {
        this(null, null, null);
    }

    TabletState(int priority) {
        this(priority, null, null, null);
    }

//	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState) {
//		this(permitRequestedState, transactionFinalState);
//	}

    TabletState(TabletState preparingPhase, TabletState permitRequestedState, TabletState transactionFinalState) {
        this(DEFAULT_PRIORITY, preparingPhase, permitRequestedState, transactionFinalState);
    }

    TabletState(int priority, TabletState preparingPhase, TabletState permitRequestedState, TabletState transactionFinalState) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.permitRequestedPhase = permitRequestedState;
        this.commitPhase = transactionFinalState;
        //this is required to have up-to-date state on server, e.g. preparing, requested;
        // otherwise issus arise, e.g. item toss would not work correctly
    }
    @Override
    public TabletState preparingPhase() {
        return preparingPhase;
    }

    @Override
    public TabletState permitRequestedPhase() {
        return permitRequestedPhase;
    }


    @Override
    public TabletState commitPhase() {
        return commitPhase;
    }

    public int getPriority() {
        return priority;
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
