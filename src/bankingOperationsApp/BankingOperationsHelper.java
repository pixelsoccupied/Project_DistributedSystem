package bankingOperationsApp;


/**
* bankingOperationsApp/BankingOperationsHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from BankingOperations
* Tuesday, November 21, 2017 1:53:47 o'clock AM EST
*/

abstract public class BankingOperationsHelper
{
  private static String  _id = "IDL:bankingOperationsApp/BankingOperations:1.0";

  public static void insert (org.omg.CORBA.Any a, bankingOperationsApp.BankingOperations that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static bankingOperationsApp.BankingOperations extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (bankingOperationsApp.BankingOperationsHelper.id (), "BankingOperations");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static bankingOperationsApp.BankingOperations read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_BankingOperationsStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, bankingOperationsApp.BankingOperations value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static bankingOperationsApp.BankingOperations narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof bankingOperationsApp.BankingOperations)
      return (bankingOperationsApp.BankingOperations)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      bankingOperationsApp._BankingOperationsStub stub = new bankingOperationsApp._BankingOperationsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static bankingOperationsApp.BankingOperations unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof bankingOperationsApp.BankingOperations)
      return (bankingOperationsApp.BankingOperations)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      bankingOperationsApp._BankingOperationsStub stub = new bankingOperationsApp._BankingOperationsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
