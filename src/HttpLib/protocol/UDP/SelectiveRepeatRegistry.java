package HttpLib.protocol.UDP;

import java.util.ArrayList;
import java.util.HashMap;

class SelectiveRepeatRegistry {

    enum SlotState {
        Released,
        Requested
    }

    private ArrayList<IPacketEventListener> _listeners = new ArrayList<IPacketEventListener>();
    private int _windowSize;
    public static final int MAX_VALID = Integer.MAX_VALUE / 2;

    // Active sequence window storage
    private HashMap<Integer, SlotState> _seq = new HashMap<>();
    private int _base = 0;
    private int _next = 0;

    public SelectiveRepeatRegistry() {
        // Based on TCP 32-bit sequence number convention
        this(MAX_VALID);
    }

    public SelectiveRepeatRegistry(int windowSize) {
        if (windowSize < 0 || windowSize > MAX_VALID)
            throw new IllegalArgumentException("WindowSize needs to be in range [0," + (MAX_VALID) + "].");

        _windowSize = windowSize;
    }

    /**
     * Change window size
     * WARNING: This reset the sequence states and previously requested IDs should be invalided.
     *
     * @param size New sequence windows size to use [0,Integer.MAX_VALUE / 2]
     */
    public void setWindowSize(int size) {
        if (size < 0 || size > MAX_VALID)
            throw new IllegalArgumentException("WindowSize needs to be in range [0," + (MAX_VALID) + "].");

        _windowSize = size;

        // Reset needed
        sync(_base);
    }

    public int getWindowSize() {
        return _windowSize;
    }

    public int getBase() {
        return _base;
    }

    public void addListener(IPacketEventListener listener) {
        _listeners.add(listener);
    }

    /**
     * Changes the active window base. Reset states of previously requested numbers.
     * WARNING: This reset the sequence states and previously requested IDs should be invalided.
     *
     * @param newBase New window base to sync to [0,Integer.MAX_VALUE]
     */
    public synchronized void sync(int newBase) {
        if (newBase < 0)
            throw new IllegalArgumentException("Invalid sequence base sync.");

        // Reset at given base
        _base = newBase;
        _next = _base;
        _seq = new HashMap<>();

        // Notify observers
        for (IPacketEventListener listener : _listeners) {
            listener.onBaseSync(_base);
        }
    }

    /**
     * Given an sequence ID #, release it and update sequence's window accordingly.
     *
     * @param i Sequence number (Index) to release
     * @return Whether the release worked. False if invalid sequence number or number not previously requested.
     */
    public boolean release(int i) {
        boolean notify = false;


        synchronized (this) {
            int initialBase = _base;
            if (!inWindow(i)) return false;

//            if (_seq.get(i) != SlotState.Requested)
//                return false;

            // Mark for cleaning
            _seq.put(i, SlotState.Released);

            // Clearing trail for next cycle
            SlotState baseState = _seq.get(_base);
            if (i == _base) {
                // Finding new base
                while (_seq.get(_base) == SlotState.Released) {
                    _seq.remove(_base);
                    _base = unsignedWrap(_base + 1);
//                    baseState = _seq.get(_base);
                }

                notify = true;
            }

            // Notify the observers
            if (notify) {
                for (IPacketEventListener listener : _listeners) {
                    listener.onWindowShift(initialBase, _base);
                }
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
     * @return The unsigned sequence number if available, -1 otherwise.
     */
    public synchronized int requestNext() {
        if (!available()) return -1;

        int next = _next;
        _seq.put(next, SlotState.Requested);
        _next = unsignedWrap(_next + 1);

        return next;
    }

    /**
     * Check if the index value is in the current active sequence number window.
     *
     * @param v Index value not wrapped yet.
     * @return If the index is considered in current active window in the sequence.
     */
    public synchronized boolean inWindow(int v) {
        int end = unsignedWrap(_base + _windowSize);
        if (_base <= end)
            return (_base <= v && v < end);
        else
            return (0 <= v && v < end) || (_base <= v);
    }

    public int unsignedWrap(int v) {
        if (v < 0) {
            v += Integer.MAX_VALUE;
            v++;
        }
        return v;
    }

}
