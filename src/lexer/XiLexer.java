/* The following code was generated by JFlex 1.6.1 */

package lexer;

import static lexer.TokenType.*;
import org.apache.commons.text.StringEscapeUtils;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>src/lexer/lexer.flex</tt>
 */
public class XiLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int YYCHARLITERAL = 2;
  public static final int YYSTRING = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\7\1\2\1\62\1\63\1\1\22\0\1\7\1\37\1\15"+
    "\2\0\1\42\1\47\1\12\1\51\1\52\1\40\1\43\1\61\1\44"+
    "\1\0\1\10\1\13\3\16\4\5\2\4\1\57\1\60\1\45\1\46"+
    "\1\41\2\0\6\6\24\3\1\53\1\14\1\54\1\0\1\11\1\0"+
    "\1\36\1\34\2\6\1\22\1\24\1\33\1\26\1\23\2\3\1\27"+
    "\1\3\1\32\1\35\2\3\1\30\1\21\1\31\1\20\1\3\1\25"+
    "\1\17\2\3\1\55\1\50\1\56\7\0\1\62\u1fa2\0\1\62\1\62"+
    "\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\2\2\1\3\1\4\1\5\1\6\1\7"+
    "\1\4\1\10\11\3\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\1\32\1\33\1\34\2\35"+
    "\2\34\1\36\2\37\1\40\1\41\1\2\2\3\1\42"+
    "\7\3\1\43\1\0\1\44\1\45\1\46\1\47\15\50"+
    "\1\51\1\52\1\53\1\52\1\54\1\55\2\51\1\56"+
    "\1\57\1\60\1\61\1\62\1\63\1\3\1\64\6\3"+
    "\1\65\1\0\1\66\1\67\1\0\1\70\1\71\2\0"+
    "\1\72\1\73\1\74\1\75\1\76\1\52\2\0\1\77"+
    "\4\3\1\100\1\101\2\0\1\102\1\0\1\103\1\104"+
    "\2\3\1\105\2\0\1\106\1\107\1\0\1\110\1\111";

  private static int [] zzUnpackAction() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\64\0\150\0\234\0\320\0\234\0\u0104\0\u0138"+
    "\0\u016c\0\234\0\234\0\234\0\234\0\u01a0\0\u01d4\0\u0208"+
    "\0\u023c\0\u0270\0\u02a4\0\u02d8\0\u030c\0\u0340\0\u0374\0\u03a8"+
    "\0\u03dc\0\234\0\234\0\234\0\u0410\0\u0444\0\234\0\234"+
    "\0\234\0\234\0\234\0\234\0\234\0\234\0\234\0\234"+
    "\0\234\0\u0478\0\u04ac\0\234\0\234\0\u04e0\0\u0514\0\u0548"+
    "\0\234\0\u057c\0\234\0\u05b0\0\u05e4\0\u0618\0\u0104\0\u064c"+
    "\0\u0680\0\u06b4\0\u06e8\0\u071c\0\u0750\0\u0784\0\234\0\u07b8"+
    "\0\234\0\234\0\234\0\234\0\234\0\u07ec\0\u0820\0\u0854"+
    "\0\u0888\0\u08bc\0\u08f0\0\u0924\0\u0958\0\u098c\0\u09c0\0\u09f4"+
    "\0\u0a28\0\234\0\u0a5c\0\234\0\u0a90\0\234\0\234\0\u0ac4"+
    "\0\u0af8\0\234\0\234\0\234\0\234\0\234\0\u0104\0\u0b2c"+
    "\0\u0104\0\u0b60\0\u0b94\0\u0bc8\0\u0bfc\0\u0c30\0\u0c64\0\234"+
    "\0\u0c98\0\234\0\234\0\u07ec\0\234\0\234\0\u0ccc\0\u0d00"+
    "\0\234\0\234\0\234\0\234\0\234\0\234\0\u0d34\0\u0d68"+
    "\0\u0104\0\u0d9c\0\u0dd0\0\u0e04\0\u0e38\0\u0104\0\u0104\0\u0e6c"+
    "\0\u0ea0\0\234\0\u0ed4\0\u0104\0\u0104\0\u0f08\0\u0f3c\0\234"+
    "\0\u0f70\0\u0fa4\0\u0104\0\u0104\0\u0fd8\0\234\0\234";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\1\6\1\7\2\10\1\7\1\6\1\11"+
    "\1\12\1\13\1\14\1\4\1\15\1\10\1\7\1\16"+
    "\1\7\1\17\1\20\1\21\1\22\1\7\1\23\1\24"+
    "\1\25\2\7\1\26\2\7\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\4"+
    "\1\6\1\52\1\53\1\54\7\52\1\55\1\52\1\56"+
    "\47\52\1\57\1\60\1\61\11\57\1\62\1\63\46\57"+
    "\66\0\1\6\64\0\4\7\2\0\3\7\2\0\21\7"+
    "\31\0\2\10\5\0\1\10\2\0\1\10\55\0\1\64"+
    "\56\0\4\7\2\0\3\7\2\0\3\7\1\65\15\7"+
    "\30\0\4\7\2\0\3\7\2\0\11\7\1\66\7\7"+
    "\30\0\4\7\2\0\3\7\2\0\6\7\1\67\5\7"+
    "\1\70\4\7\30\0\4\7\2\0\3\7\2\0\20\7"+
    "\1\71\30\0\4\7\2\0\3\7\2\0\10\7\1\72"+
    "\10\7\30\0\4\7\2\0\3\7\2\0\4\7\1\73"+
    "\14\7\30\0\4\7\2\0\3\7\2\0\4\7\1\74"+
    "\14\7\30\0\4\7\2\0\3\7\2\0\12\7\1\75"+
    "\6\7\30\0\4\7\2\0\3\7\2\0\17\7\1\76"+
    "\1\7\73\0\1\77\56\0\1\100\70\0\1\101\63\0"+
    "\1\102\63\0\1\103\27\0\1\104\53\0\1\54\61\0"+
    "\1\105\2\0\2\105\1\106\4\105\1\107\1\110\1\111"+
    "\1\112\1\110\1\113\1\114\3\105\1\115\3\105\1\116"+
    "\1\117\1\120\1\105\1\121\25\105\2\0\1\57\2\0"+
    "\11\57\2\0\46\57\2\0\1\61\61\0\1\122\2\0"+
    "\2\122\1\123\4\122\1\124\1\125\1\126\1\127\1\125"+
    "\1\130\1\131\3\122\1\132\3\122\1\133\1\134\1\135"+
    "\1\122\1\136\25\122\2\0\1\64\1\5\1\6\61\64"+
    "\3\0\4\7\2\0\3\7\2\0\4\7\1\137\14\7"+
    "\30\0\4\7\2\0\3\7\2\0\3\7\1\140\15\7"+
    "\30\0\4\7\2\0\3\7\2\0\13\7\1\141\5\7"+
    "\30\0\4\7\2\0\3\7\2\0\11\7\1\142\7\7"+
    "\30\0\4\7\2\0\3\7\2\0\5\7\1\143\13\7"+
    "\30\0\4\7\2\0\3\7\2\0\14\7\1\144\4\7"+
    "\30\0\4\7\2\0\3\7\2\0\13\7\1\145\5\7"+
    "\30\0\4\7\2\0\3\7\2\0\2\7\1\146\16\7"+
    "\30\0\4\7\2\0\3\7\2\0\17\7\1\147\1\7"+
    "\66\0\1\150\27\0\1\151\4\0\1\152\1\151\2\0"+
    "\1\151\57\0\1\153\56\0\1\154\4\0\1\152\1\154"+
    "\2\0\1\154\57\0\1\155\63\0\1\156\55\0\3\157"+
    "\4\0\1\157\2\0\1\157\3\0\1\157\1\0\1\157"+
    "\7\0\1\157\1\0\1\157\31\0\3\160\4\0\1\160"+
    "\2\0\1\160\3\0\1\160\1\0\1\160\7\0\1\160"+
    "\1\0\1\160\37\0\1\161\63\0\1\162\63\0\1\163"+
    "\63\0\1\164\63\0\1\165\56\0\1\166\5\0\1\166"+
    "\2\0\1\166\52\0\1\123\5\0\1\123\2\0\1\123"+
    "\51\0\3\167\4\0\1\167\2\0\1\167\3\0\1\167"+
    "\1\0\1\167\7\0\1\167\1\0\1\167\31\0\3\170"+
    "\4\0\1\170\2\0\1\170\3\0\1\170\1\0\1\170"+
    "\7\0\1\170\1\0\1\170\30\0\4\7\2\0\3\7"+
    "\2\0\4\7\1\171\14\7\30\0\4\7\2\0\3\7"+
    "\2\0\3\7\1\172\15\7\30\0\4\7\2\0\3\7"+
    "\2\0\11\7\1\173\7\7\30\0\4\7\2\0\3\7"+
    "\2\0\15\7\1\174\3\7\30\0\4\7\2\0\3\7"+
    "\2\0\2\7\1\175\16\7\30\0\4\7\2\0\3\7"+
    "\2\0\4\7\1\176\14\7\30\0\4\7\2\0\3\7"+
    "\2\0\11\7\1\177\7\7\37\0\1\152\55\0\3\200"+
    "\4\0\1\200\2\0\1\200\3\0\1\200\1\0\1\200"+
    "\7\0\1\200\1\0\1\200\31\0\3\201\4\0\1\201"+
    "\2\0\1\201\3\0\1\201\1\0\1\201\7\0\1\201"+
    "\1\0\1\201\31\0\3\202\4\0\1\202\2\0\1\202"+
    "\3\0\1\202\1\0\1\202\7\0\1\202\1\0\1\202"+
    "\31\0\3\203\4\0\1\203\2\0\1\203\3\0\1\203"+
    "\1\0\1\203\7\0\1\203\1\0\1\203\30\0\4\7"+
    "\2\0\3\7\2\0\4\7\1\204\14\7\30\0\4\7"+
    "\2\0\3\7\2\0\4\7\1\205\14\7\30\0\4\7"+
    "\2\0\3\7\2\0\13\7\1\206\5\7\30\0\4\7"+
    "\2\0\3\7\2\0\12\7\1\207\6\7\37\0\1\210"+
    "\55\0\3\211\4\0\1\211\2\0\1\211\3\0\1\211"+
    "\1\0\1\211\7\0\1\211\1\0\1\211\31\0\3\212"+
    "\4\0\1\212\2\0\1\212\3\0\1\212\1\0\1\212"+
    "\7\0\1\212\1\0\1\212\30\0\4\7\2\0\3\7"+
    "\2\0\10\7\1\213\10\7\30\0\4\7\2\0\3\7"+
    "\2\0\14\7\1\214\4\7\31\0\3\215\4\0\1\215"+
    "\2\0\1\215\3\0\1\215\1\0\1\215\7\0\1\215"+
    "\1\0\1\215\31\0\3\216\4\0\1\216\2\0\1\216"+
    "\3\0\1\216\1\0\1\216\7\0\1\216\1\0\1\216"+
    "\37\0\1\217\51\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4108];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\1\1\1\11\3\1\4\11\14\1\3\11"+
    "\2\1\13\11\2\1\2\11\3\1\1\11\1\1\1\11"+
    "\13\1\1\11\1\0\5\11\14\1\1\11\1\1\1\11"+
    "\1\1\2\11\2\1\5\11\11\1\1\11\1\0\2\11"+
    "\1\0\2\11\2\0\6\11\2\0\7\1\2\0\1\11"+
    "\1\0\4\1\1\11\2\0\2\1\1\0\2\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
    private StringBuilder value = new StringBuilder();
    private StringBuilder literal = new StringBuilder();

    private int row() { return yyline + 1; }

    private int column() { return yycolumn + 1; }
    
    private int startColumn = 1;

    private Token tokenize(TokenType tt) throws Exception {
        switch (tt) {
            case USE:
            case IF:
            case WHILE:
            case ELSE:
            case RETURN:
            case LENGTH:
            case INT:
            case BOOL:
            case TRUE:
            case FALSE:
            case LNEG:
            case MULT:
            case HMULT:
            case DIV:
            case MOD:
            case ADD:
            case MINUS:
            case LTE:
            case LT:
            case GTE:
            case GT:
            case EQEQ:
            case EQ:
            case NEQ:
            case LAND:
            case LOR:
            case LPAREN:
            case RPAREN:
            case LBRACK:
            case RBRACK:
            case LBRACE:
            case RBRACE:
            case COLON:
            case SEMICOLON:
            case COMMA:
            case UNDERSCORE:
            case EOF:
                return new Token(tt, row(), column(), yytext());
            case ID:
                return new IDToken(row(), column(), yytext());
            case INTEGER:
                return new IntToken(row(), column(), yytext());
            default:
                throw new Exception("Unknown token type.");
        }
    }

    private Token tokenize(char c) throws Exception {
        yybegin(YYINITIAL);
        String literal = escape(stripQuote(yytext()), c);
        return new CharToken(row(), startColumn, literal, c);
    }

    private Token tokenize() {
        yybegin(YYINITIAL);
        return new StringToken(row(), startColumn, literal.toString(), value.toString());
    }

    private Token logError(int r, int c, String msg) throws Exception {
        throw new Exception(
            String.format("%d:%d error:%s", r, c, msg)
        );
    }

    private String escape(String source, char c) {
        if (!(c <= 0x1F || (0x7F <= c && c <= 0x9F))) {
            String s = Character.toString(c);
            return StringEscapeUtils.escapeJava(s);
        }
        return source;
    }

    private String stripQuote(String s) {
        return s.substring(0, s.length() - 1);
    }



  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public XiLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 174) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token nextToken() throws java.io.IOException, Exception {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
              {
                return tokenize(EOF);
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { logError(row(), column(), "Invalid syntax");
            }
          case 74: break;
          case 2: 
            { /* ignore */
            }
          case 75: break;
          case 3: 
            { return tokenize(ID);
            }
          case 76: break;
          case 4: 
            { return tokenize(INTEGER);
            }
          case 77: break;
          case 5: 
            { return tokenize(DIV);
            }
          case 78: break;
          case 6: 
            { return tokenize(UNDERSCORE);
            }
          case 79: break;
          case 7: 
            { startColumn = column();
                            yybegin(YYCHARLITERAL);
            }
          case 80: break;
          case 8: 
            { startColumn = column();
                            value.setLength(0);
                            literal.setLength(0);
                            yybegin(YYSTRING);
            }
          case 81: break;
          case 9: 
            { return tokenize(LNEG);
            }
          case 82: break;
          case 10: 
            { return tokenize(MULT);
            }
          case 83: break;
          case 11: 
            { return tokenize(GT);
            }
          case 84: break;
          case 12: 
            { return tokenize(MOD);
            }
          case 85: break;
          case 13: 
            { return tokenize(ADD);
            }
          case 86: break;
          case 14: 
            { return tokenize(MINUS);
            }
          case 87: break;
          case 15: 
            { return tokenize(LT);
            }
          case 88: break;
          case 16: 
            { return tokenize(EQ);
            }
          case 89: break;
          case 17: 
            { return tokenize(LAND);
            }
          case 90: break;
          case 18: 
            { return tokenize(LOR);
            }
          case 91: break;
          case 19: 
            { return tokenize(LPAREN);
            }
          case 92: break;
          case 20: 
            { return tokenize(RPAREN);
            }
          case 93: break;
          case 21: 
            { return tokenize(LBRACK);
            }
          case 94: break;
          case 22: 
            { return tokenize(RBRACK);
            }
          case 95: break;
          case 23: 
            { return tokenize(LBRACE);
            }
          case 96: break;
          case 24: 
            { return tokenize(RBRACE);
            }
          case 97: break;
          case 25: 
            { return tokenize(COLON);
            }
          case 98: break;
          case 26: 
            { return tokenize(SEMICOLON);
            }
          case 99: break;
          case 27: 
            { return tokenize(COMMA);
            }
          case 100: break;
          case 28: 
            { logError(row(), startColumn, "Invalid character literal");
            }
          case 101: break;
          case 29: 
            { logError(row(), startColumn, "Character literal not properly terminated");
            }
          case 102: break;
          case 30: 
            { value.append(yytext());
                            literal.append(yytext());
            }
          case 103: break;
          case 31: 
            { logError(row(), startColumn, "String literal not properly terminated");
            }
          case 104: break;
          case 32: 
            { logError(row(), startColumn, "Invalid string literal");
            }
          case 105: break;
          case 33: 
            { return tokenize();
            }
          case 106: break;
          case 34: 
            { return tokenize(IF);
            }
          case 107: break;
          case 35: 
            { return tokenize(NEQ);
            }
          case 108: break;
          case 36: 
            { return tokenize(GTE);
            }
          case 109: break;
          case 37: 
            { return tokenize(LTE);
            }
          case 110: break;
          case 38: 
            { return tokenize(EQEQ);
            }
          case 111: break;
          case 39: 
            { return tokenize(yytext().charAt(0));
            }
          case 112: break;
          case 40: 
            { logError(row(), startColumn, "Invalid escape sequence \'" + yytext() + "\'");
            }
          case 113: break;
          case 41: 
            { logError(row(), startColumn, "Invalid escape sequence \"" +  yytext() + "\"");
            }
          case 114: break;
          case 42: 
            { String s = yytext().substring(1, yylength());
                            char c = (char) Integer.parseInt(s, 8);
                            value.append(c);
                            literal.append(escape(yytext(), c));
            }
          case 115: break;
          case 43: 
            { value.append('\'');
                            literal.append("\\'");
            }
          case 116: break;
          case 44: 
            { value.append('\\');
                            literal.append("\\\\");
            }
          case 117: break;
          case 45: 
            { value.append('\"');
                            literal.append("\\\"");
            }
          case 118: break;
          case 46: 
            { value.append('\f');
                            literal.append("\\f");
            }
          case 119: break;
          case 47: 
            { value.append('\r');
                            literal.append("\\r");
            }
          case 120: break;
          case 48: 
            { value.append('\t');
                            literal.append("\\t");
            }
          case 121: break;
          case 49: 
            { value.append('\n');
                            literal.append("\\n");
            }
          case 122: break;
          case 50: 
            { value.append('\b');
                            literal.append("\\b");
            }
          case 123: break;
          case 51: 
            { return tokenize(USE);
            }
          case 124: break;
          case 52: 
            { return tokenize(INT);
            }
          case 125: break;
          case 53: 
            { return tokenize(HMULT);
            }
          case 126: break;
          case 54: 
            { String s = yytext().substring(1, yylength() - 1);
                            char c = (char) Integer.parseInt(s, 8);
                            return tokenize(c);
            }
          case 127: break;
          case 55: 
            { return tokenize('\'');
            }
          case 128: break;
          case 56: 
            { return tokenize('\\');
            }
          case 129: break;
          case 57: 
            { return tokenize('\"');
            }
          case 130: break;
          case 58: 
            { return tokenize('\f');
            }
          case 131: break;
          case 59: 
            { return tokenize('\r');
            }
          case 132: break;
          case 60: 
            { return tokenize('\t');
            }
          case 133: break;
          case 61: 
            { return tokenize('\n');
            }
          case 134: break;
          case 62: 
            { return tokenize('\b');
            }
          case 135: break;
          case 63: 
            { return tokenize(ELSE);
            }
          case 136: break;
          case 64: 
            { return tokenize(TRUE);
            }
          case 137: break;
          case 65: 
            { return tokenize(BOOL);
            }
          case 138: break;
          case 66: 
            { String s = yytext().substring(2, 4);
                            char c = (char) Integer.parseInt(s, 16);
                            value.append(c);
                            literal.append(escape(yytext(), c));
            }
          case 139: break;
          case 67: 
            { return tokenize(FALSE);
            }
          case 140: break;
          case 68: 
            { return tokenize(WHILE);
            }
          case 141: break;
          case 69: 
            { String s = yytext().substring(2, 4);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
            }
          case 142: break;
          case 70: 
            { return tokenize(LENGTH);
            }
          case 143: break;
          case 71: 
            { return tokenize(RETURN);
            }
          case 144: break;
          case 72: 
            { String s = yytext().substring(2, 6);
                            char c = (char) Integer.parseInt(s, 16);
                            value.append(c);
                            literal.append(escape(yytext(), c));
            }
          case 145: break;
          case 73: 
            { String s = yytext().substring(2, 6);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
            }
          case 146: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
