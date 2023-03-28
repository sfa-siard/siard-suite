package ch.enterag.utils.uuid;

import java.nio.ByteBuffer;
import java.util.UUID;

/** little-endian UUID nonsense */
public class UUIDLE
{
  public static byte[] uuidToBytes(UUID uuid)
  {
    ByteBuffer bb = ByteBuffer.allocate(16);
    long lHigh = uuid.getMostSignificantBits();
    bb.putLong(lHigh);
    long lLow = uuid.getLeastSignificantBits();
    bb.putLong(lLow);
    byte[] buf = bb.array();
    byte[] bufOutput = new byte[16];
    // little-endian int
    for (int i = 0; i < 4; i++)
      bufOutput[i] = buf[3-i];
    // little-endian short
    for (int i = 0; i < 2; i++)
      bufOutput[4+i] = buf[4+1-i];
    // little-endian short
    for (int i = 0; i < 2; i++)
      bufOutput[6+i] = buf[6+1-i];
    // byte array
    for (int i = 8; i < 16; i++)
      bufOutput[i] = buf[i];
    return bufOutput;
  } /* uuidToBytes */
  
  public static UUID uuidFromBytes(byte[] bufInput, int iPos)
  {
    // endian-nonsense (this explains it: https://msdn.microsoft.com/en-us/library/fx22893a.aspx)
    byte[] buf = new byte[16];
    // little-endian int
    for (int i = 0; i < 4; i++)
      buf[i] = bufInput[iPos+3-i];
    // little-endian short
    for (int i = 0; i < 2; i++)
      buf[4+i] = bufInput[iPos+4+1-i];
    // little-endian short
    for (int i = 0; i < 2; i++)
      buf[6+i] = bufInput[iPos+6+1-i];
    // byte array
    for (int i = 8; i < 16; i++)
      buf[i] = bufInput[iPos+i];
    ByteBuffer bb = ByteBuffer.wrap(buf);
    long high = bb.getLong();
    long low = bb.getLong();
    UUID uuid = new UUID(high, low);
    return uuid;
  } /* uuidFromBytes */

  public static UUID uuidFromBytes(byte[] bufInput)
  {
    return uuidFromBytes(bufInput,0);
  } /* uuidFromBytes */
  
} /* LEUUID */
