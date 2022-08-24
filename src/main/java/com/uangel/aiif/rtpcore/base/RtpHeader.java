package com.uangel.aiif.rtpcore.base;

public class RtpHeader {

    public static final int FIXED_HEADER_SIZE = 12;
    /**
     * Current supported RTP version
     */
    public static final int VERSION = 2;
    private static final int VERSION_AND_CC_OFFSET = 0;
    private static final int MARKER_AND_PAYLOADTYPE_OFFSET = 1;
    private static final int SEQUENCE_NUMBER_OFFSET = 2;
    private static final int TIMESTAMP_OFFSET = 4;
    private static final int SSRC_OFFSET = 8;
    private static final int EXT_HEADER_SIZE = 4;
    private static final int MIN_PAYLOAD_TYPE = 0;
    private static final int MAX_PAYLOAD_TYPE = 127;

    private int rtpPosition;
    private byte[] data;

    public RtpHeader( ) {
        // Do nothing
    }

    public RtpHeader(byte[] data, int offset) {
        this.rtpPosition = offset;
        this.data = data;
        if (data.length < (FIXED_HEADER_SIZE + rtpPosition) ||
                getVersion() != VERSION ||
                getPayloadType() < MIN_PAYLOAD_TYPE ||
                getPayloadType() > MAX_PAYLOAD_TYPE
        ) {
            throw new RuntimeException("Invalid Rtp Header.");
        }
    }

    public boolean isSimpleRTPMessage (byte[] data, int rtpPosition) {
        if (data.length < (FIXED_HEADER_SIZE + rtpPosition) ||
                ((data[VERSION_AND_CC_OFFSET + rtpPosition] & 0xC0) >> 6) != VERSION) {
            return false;
        }

        int csrcCount = data[VERSION_AND_CC_OFFSET + rtpPosition] & 0x0F;
        int payloadLength = data.length - rtpPosition - (FIXED_HEADER_SIZE + 4 * csrcCount);
        int payloadType = data[MARKER_AND_PAYLOADTYPE_OFFSET + rtpPosition] & 0x7F;

        return payloadLength > 0 &&
                payloadType <= MAX_PAYLOAD_TYPE;
    }

    /**
     * Verion field.
     * <p>
     * This field identifies the version of RTP. The version defined by
     * this specification is two (2). (The value 1 is used by the first
     * draft version of RTP and the value 0 is used by the protocol
     * initially implemented in the "vat" audio tool.)
     *
     * @return the version value.
     */
    public int getVersion ( ) {
        return (data[VERSION_AND_CC_OFFSET + rtpPosition] & 0xC0) >> 6;
    }

    /**
     * Padding indicator.
     * <p>
     * If the padding bit is set, the packet contains one or more
     * additional padding octets at the end which are not part of the
     * payload. The last octet of the padding contains a count of how
     * many padding octets should be ignored. Padding may be needed by
     * some encryption algorithms with fixed block sizes or for
     * carrying several RTP packets in a lower-layer protocol data
     * unit.
     *
     * @return true if padding bit set.
     */
    public boolean hasPadding ( ) {
        return (data[VERSION_AND_CC_OFFSET + rtpPosition] & 0x20) == 0x020;
    }

    /**
     * Extension indicator.
     * <p>
     * If the extension bit is set, the fixed header is followed by
     * exactly one header extension.
     *
     * @return true if extension bit set.
     */
    public boolean hasExtensions ( ) {
        return (data[VERSION_AND_CC_OFFSET + rtpPosition] & 0x10) == 0x010;
    }

    /**
     * Returns the number of CSRC identifiers currently included in this packet.
     *
     * @return the CSRC count for this <tt>RawPacket</tt>.
     */
    public int getCsrcCount ( ) {
        return data[VERSION_AND_CC_OFFSET + rtpPosition] & 0x0F;
    }

    /**
     * Marker bit.
     * <p>
     * The interpretation of the marker is defined by a profile. It is
     * intended to allow significant events such as frame boundaries to
     * be marked in the packet stream. A profile may define additional
     * marker bits or specify that there is no marker bit by changing
     * the number of bits in the payload types field
     *
     * @return true if marker set.
     */
    public boolean getMarker ( ) {
        return (data[MARKER_AND_PAYLOADTYPE_OFFSET + rtpPosition] & 0x80) == 0x80;
    }

    /**
     * Payload types.
     * <p>
     * This field identifies the format of the RTP payload and
     * determines its interpretation by the application. A profile
     * specifies a default static mapping of payload types codes to
     * payload formats. Additional payload types codes may be defined
     * dynamically through non-RTP means
     *
     * @return integer value of payload types.
     */
    public int getPayloadType ( ) {
        return (data[MARKER_AND_PAYLOADTYPE_OFFSET + rtpPosition] & 0x7F);
    }

    /**
     * Sequence number field.
     * <p>
     * The sequence number increments by one for each RTP data packet
     * sent, and may be used by the receiver to detect packet loss and
     * to restore packet sequence. The initial value of the sequence
     * number is random (unpredictable) to make known-plaintext attacks
     * on encryption more difficult, even if the source itself does not
     * encrypt, because the packets may flow through a translator that
     * does.
     *
     * @return the sequence number value.
     */
    public int getSeqNumber ( ) {
        return ((data[SEQUENCE_NUMBER_OFFSET + rtpPosition] & 0xFF) << 8) |
                (data[SEQUENCE_NUMBER_OFFSET + rtpPosition + 1] & 0xFF);
    }

    /**
     * Timestamp field.
     * <p>
     * The timestamp reflects the sampling instant of the first octet
     * in the RTP data packet. The sampling instant must be derived
     * from a clock that increments monotonically and linearly in time
     * to allow synchronization and jitter calculations.
     * The resolution of the clock must be sufficient for the
     * desired synchronization accuracy and for measuring packet
     * arrival jitter (one tick per video frame is typically not
     * sufficient).  The clock frequency is dependent on the format of
     * data carried as payload and is specified statically in the
     * profile or payload format specification that defines the format,
     * or may be specified dynamically for payload formats defined
     * through non-RTP means. If RTP packets are generated
     * periodically, the nominal sampling instant as determined from
     * the sampling clock is to be used, not a reading of the system
     * clock. As an example, for fixed-rate audio the timestamp clock
     * would likely increment by one for each sampling period.  If an
     * audio application reads blocks covering 160 sampling periods
     * from the input device, the timestamp would be increased by 160
     * for each such block, regardless of whether the block is
     * transmitted in a packet or dropped as silent.
     * <p>
     * The initial value of the timestamp is random, as for the sequence
     * number. Several consecutive RTP packets may have equal timestamps if
     * they are (logically) generated at once, e.g., belong to the same
     * video frame. Consecutive RTP packets may contain timestamps that are
     * not monotonic if the data is not transmitted in the order it was
     * sampled, as in the case of MPEG interpolated video frames. (The
     * sequence numbers of the packets as transmitted will still be
     * monotonic.)
     *
     * @return timestamp value
     */
    public long getTimestamp ( ) {
        return ((long) (data[TIMESTAMP_OFFSET + rtpPosition] & 0xFF) << 24) |
                ((long) (data[TIMESTAMP_OFFSET + rtpPosition + 1] & 0xFF) << 16) |
                ((long) (data[TIMESTAMP_OFFSET + rtpPosition + 2] & 0xFF) << 8) |
                ((long) (data[TIMESTAMP_OFFSET + rtpPosition + 3] & 0xFF));
    }

    /**
     * Synchronization source field.
     * <p>
     * The SSRC field identifies the synchronization source. This
     * identifier is chosen randomly, with the intent that no two
     * synchronization sources within the same RTP session will have
     * the same SSRC identifier. Although the
     * probability of multiple sources choosing the same identifier is
     * low, all RTP implementations must be prepared to detect and
     * resolve collisions.  Section 8 describes the probability of
     * collision along with a mechanism for resolving collisions and
     * detecting RTP-level forwarding loops based on the uniqueness of
     * the SSRC identifier. If a source changes its source transport
     * address, it must also choose a new SSRC identifier to avoid
     * being interpreted as a looped source.
     *
     * @return the sysncronization source
     */
    public long getSyncSource ( ) {
        return ((long) (data[SSRC_OFFSET + rtpPosition] & 0xFF) << 24) |
                ((long) (data[SSRC_OFFSET + rtpPosition + 1] & 0xFF) << 16) |
                ((long) (data[SSRC_OFFSET + rtpPosition + 2] & 0xFF) << 8) |
                ((long) (data[SSRC_OFFSET + rtpPosition + 3] & 0xFF));
    }

    /**
     * Get RTP header length from a RTP packet
     *
     * @return RTP header length from source RTP packet
     */
    public int getHeaderLength ( ) {
        if (hasExtensions())
            return FIXED_HEADER_SIZE + 4 * getCsrcCount() + EXT_HEADER_SIZE + getExtensionLength();
        else
            return FIXED_HEADER_SIZE + 4 * getCsrcCount();
    }

    /**
     * Get RTP payload length from a RTP packet
     *
     * @return RTP payload length from source RTP packet
     */
    public int getPayloadLength ( ) {
        return data.length - rtpPosition - getHeaderLength();
    }

    /**
     * Get RTP payload position from a RTP packet
     *
     * @return RTP payload postion from source RTP packet
     */
    public int getPayloadPosition ( ) {
        return getHeaderLength() + rtpPosition;
    }


    /**
     * Returns the length of the extensions currently added to this packet.
     *
     * @return the length of the extensions currently added to this packet.
     */
    public int getExtensionLength ( ) {
        if (!hasExtensions())
            return 0;

        //the extension length comes after the RTP header, the CSRC list, and
        //after two bytes in the extension header called "defined by profile"
        int extLenIndex = FIXED_HEADER_SIZE + getCsrcCount() * 4 + 2;
        return ((data[extLenIndex + rtpPosition] << 8) | data[extLenIndex + rtpPosition + 1] * 4);
    }

    public byte[] getData ( ) {
        return data;
    }

}
