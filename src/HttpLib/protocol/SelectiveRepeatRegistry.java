package HttpLib.protocol;

import java.util.ArrayList;
import java.util.Arrays;

class SelectiveRepeatRegistry {

    public enum SlotState {
        Ready,
        Confirmed,
        Unconfirmed
    }

    private ArrayList<IPacketEventListener> _listeners = new ArrayList<IPacketEventListener>();
    private short _windowSize;

    private SlotState[] _seq;
    private int _base = 0;
    private int _next = 0;

    public SelectiveRepeatRegistry(short windowSize) {
        if (windowSize < 0)
            throw new IllegalArgumentException("WindowSize needs to be positive.");

        _windowSize = windowSize;
        _seq = new SlotState[_windowSize * 2];
        Arrays.fill(_seq, SlotState.Ready);
    }

    public short getWindowSize() {
        return _windowSize;
    }

    public int getBase() {
        return _base;
    }

    public void addListener(IPacketEventListener listener) {
        _listeners.add(listener);
    }

    public boolean confirm(int i) {
        System.out.println("Confirming: "+i);
        boolean notify = false;

        synchronized (this) {
            if (!inWindow(i)) return false;

            int index = i;

            if (_seq[index] != SlotState.Unconfirmed)
                return false;

            // Marking
            _seq[index] = SlotState.Confirmed;

            // Clearing trail for next cycle
            if (i == _base) {
                // Finding new base
                while(_base != _next && _seq[_base] == SlotState.Confirmed ){
                    _seq[_base] = SlotState.Ready;
                    _base = wrapIndex(_base + 1);
                }

                notify = true;
            }
        }

        // Notify the observers
        if(notify){
            for (IPacketEventListener listener : _listeners) {
                listener.onCanRequest();
            }
        }

        return true;
    }

    public boolean canSend() {
        // Checking diff
        int diff = Math.abs(_next - _base);
        return diff < _windowSize;
    }

    public synchronized int requestNextSeqNumber() {
        if (!canSend()) return -1;

        int next = _next;
        _seq[next] = SlotState.Unconfirmed;
        _next = wrapIndex(_next + 1);

        return next;
    }

    public synchronized boolean inWindow(int v) {
        // Not wraping in that case
        return (v >= _base && v < _base + _windowSize );
    }

    private int wrapIndex(int v) {
        while (v < 0) v += _seq.length;
        while (v > _seq.length - 1) v -= _seq.length;
        return v;
    }

}
