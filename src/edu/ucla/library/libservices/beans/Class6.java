package edu.ucla.library.libservices.beans;

public class Class6
{
  private static final int COPY_INT = 251;
  private static final char COPY = '©';
  private static final String COPY_HTML = "&#169;";

  public Class6()
  {
  }

  public static void main( String[] args )
  {
    String imprint;
    char copy;
    imprint = "Cambridge : Cambridge University Press, 2011, ©2011.";
    System.out.println( "original string = " + imprint );
    System.out.println( "modified string = " + 
                        imprint.replaceAll( String.valueOf( COPY ), 
                                            COPY_HTML ) );
    copy = '©';
    System.out.println( "\ninteger of © = " + 
                        Character.getNumericValue( copy ) );
                        
  }
}
