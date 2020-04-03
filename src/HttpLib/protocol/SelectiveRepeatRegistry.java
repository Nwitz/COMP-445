package HttpLib.protocol;

import java.util.ArrayList;
import java.util.Arrays;

class SelectiveRepeatRegistry {

    public enum SlotState {
        Free,
        Released,
        Requested
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
        Arrays.fill(_seq, SlotState.Free);
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

    /**
     * @param i Sequence number (Index) to release
     * @return
     */
    public boolean release(int i) {
        boolean notify = false;

        synchronized (this) {
            if (!inWindow(i)) return false;

            if (_seq[i] != SlotState.Requested)
                return false;

            // Marking
            _seq[i] = SlotState.Released;

            // Clearing trail for next cycle
            if (i == _base) {
                // Finding new base
                while (_base != _next && _seq[_base] == SlotState.Released) {
                    _seq[_base] = SlotState.Free;
                    _base = wrapIndex(_base + 1);
                }

                notify = true;
            }
        }

        // Notify the observers
        if (notify) {
            for (IPacketEventListener listener : _listeners) {
                listener.onCanRequest();
            }
        }

        return true;
    }

    /**
     * Check if the next sequence number is available based on the sequence state
     *
     * @return Whenever the sequence is available to be queried for a number.
     */
    public boolean available() {
        // Checking diff
        int diff = Math.abs(_next - _base);
        return diff < _windowSize;
    }

    /**
     * Try to get the next sequence number ready to be used.
     *
     * @return The sequence number if it was available, -1 otherwise
     */
    public synchronized int requestNext() {
        if (!available()) return -1;

        int next = _next;
        _seq[next] = SlotState.Requested;
        _next = wrapIndex(_next + 1);

        return next;
    }

    /**
     * Check if the index value is in the current active sequence number window.
     *
     * @param v Index value not wrapped yet.
     * @return If the index is considered in current active window in the sequence.
     */
    public synchronized boolean inWindow(int v) {
        int end = wrapIndex(_base + _windowSize);
        if (_base <= end)
            return (_base <= v && v < end);
        else
            return (0 <= v && v < end) || (_base <= v && v < _seq.length);
    }

    private int wrapIndex(int v) {
        while (v < 0) v += _seq.length;
        while (v > _seq.length - 1) v -= _seq.length;
        return v;
    }

}
