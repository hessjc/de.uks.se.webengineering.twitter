package de.uks.webengineering.twitter.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
public final class FormatHelper
{
   public static boolean isUTF8Interpreted(String input)
   {
      return isUTF8Interpreted(input, "Windows-1252");
   }

   public static boolean isUTF8Interpreted(String input, String encoding)
   {

      CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
      CharsetEncoder encoder = Charset.forName(encoding).newEncoder();
      ByteBuffer tmp;
      try
      {
         tmp = encoder.encode(CharBuffer.wrap(input));
      }

      catch (CharacterCodingException e)
      {
         return false;
      }

      try
      {
         decoder.decode(tmp);
         return true;
      }
      catch (CharacterCodingException e)
      {
         return false;
      }
   }

   public static int charLength(byte[] bytes) {
      int charCount = 0, expectedLen;

      for (int i = 0; i < bytes.length; i++) {
         charCount++;
         // Lead byte analysis
         if      ((bytes[i] & 0b10000000) == 0b00000000) continue;
         else if ((bytes[i] & 0b11100000) == 0b11000000) expectedLen = 2;
         else if ((bytes[i] & 0b11110000) == 0b11100000) expectedLen = 3;
         else if ((bytes[i] & 0b11111000) == 0b11110000) expectedLen = 4;
         else if ((bytes[i] & 0b11111100) == 0b11111000) expectedLen = 5;
         else if ((bytes[i] & 0b11111110) == 0b11111100) expectedLen = 6;
         else    return -1;

         // Count trailing bytes
         while (--expectedLen > 0) {
            if (++i >= bytes.length) {
               return -1;
            }
            if ((bytes[i] & 0b11000000) != 0b10000000) {
               return -1;
            }
         }
      }
      return charCount;
   }
}