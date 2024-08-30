/**
 * Dealing with bytes is more performant when dealing with input of long bit stream
 * Another implicit advantage of using byte as the unit of iteration is that we could apply the technique of memoization
 * To reverse bits for a byte, one could apply the same algorithm as we show in the above approach.
 *
 * def reverseByte(byte):
 *  return (byte * 0x0202020202 & 0x010884422010) % 1023
 *
 * read more here https://graphics.stanford.edu/~seander/bithacks.html#ReverseByteWith64BitsDiv
 */

#include <cstdint>
#include <map>

using namespace std;

class ByteByByte
{

private:
  map<uint32_t, uint32_t> cache;

public:
  uint32_t reverseByte(uint32_t byte)
  {
    if (cache.find(byte) != cache.end())
    {
      return cache[byte];
    }
    uint32_t value = (byte * 0x0202020202 & 0x010884422010) % 1023;
    cache.emplace(byte, value);
    return value;
  }

  uint32_t reverseBits(uint32_t n)
  {
    uint32_t ret = 0, power = 24;

    while (n != 0)
    {
      ret += reverseByte(n & 0xff) << power;
      n = n >> 8;
      power -= 8;
    }

    return ret;
  }
};