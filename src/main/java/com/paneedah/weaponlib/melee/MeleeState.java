package com.paneedah.weaponlib.melee;

import com.paneedah.weaponlib.state.ManagedState;
import io.netty.buffer.ByteBuf;

public enum MeleeState implements ManagedState<MeleeState> {

    READY,

    ATTACKING(9),

    ATTACKING_STABBING(9),

    HEAVY_ATTACKING(9),

    HEAVY_ATTACKING_STABBING(9),

    MODIFYING_REQUESTED(1),

    MODIFYING(2, null, MODIFYING_REQUESTED, null),

    NEXT_ATTACHMENT_REQUESTED,

    NEXT_ATTACHMENT(2, null, NEXT_ATTACHMENT_REQUESTED, null),

    ALERT;

    private static final int DEFAULT_PRIORITY = 0;

    private final MeleeState preparingPhase;

    private final MeleeState permitRequestedPhase;

    private final MeleeState commitPhase;

    private int priority = DEFAULT_PRIORITY;

    MeleeState() {
        this(null, null, null);
    }

    MeleeState(int priority) {
        this(priority, null, null, null);
    }

//	private WeaponState(WeaponState permitRequestedState, WeaponState transactionFinalState) {
//		this(permitRequestedState, transactionFinalState);
//	}

    MeleeState(MeleeState preparingPhase, MeleeState permitRequestedState, MeleeState transactionFinalState) {
        this(DEFAULT_PRIORITY, preparingPhase, permitRequestedState, transactionFinalState);
    }

    MeleeState(int priority, MeleeState preparingPhase, MeleeState permitRequestedState, MeleeState transactionFinalState) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.permitRequestedPhase = permitRequestedState;
        this.commitPhase = transactionFinalState;
        //this is required to have up-to-date state on server, e.g. preparing, requested;
        // otherwise issus arise, e.g. item toss would not work correctly
    }
    @Override
    public MeleeState preparingPhase() {
        return preparingPhase;
    }

    @Override
    public MeleeState permitRequestedPhase() {
        return permitRequestedPhase;
    }


    @Override
    public MeleeState commitPhase() {
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
