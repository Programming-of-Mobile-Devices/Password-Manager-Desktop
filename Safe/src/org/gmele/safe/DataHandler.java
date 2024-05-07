package org.gmele.safe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
import org.gmele.general.crypto.Crypto;
import org.gmele.general.exceptions.GmException;

/**
 *
 * @author gmele
 */
public class DataHandler extends AbstractTableModel
{
    final String[] ColNames = {"ΙΔΙΟΚΤΗΤΗΣ", "ΓΕΝΙΚΗ ΚΑΤΗΓΟΡΙΑ", "ΣΥΣΤΗΜΑ", "ΑΝΤΙΚΕΙΜΕΝΟ", "ΛΟΓΑΡΙΑΣΜΟΣ", "ΟΝΟΜΑ ΧΡΗΣΤΗ", "ΣΥΝΘΗΜΑΤΙΚΟ",
        "ΣΗΜΕΙΩΣΕΙΣ"};
    ArrayList <KeyRec> KeyList;
    boolean Changed;
    KeyRec Copied;
       
    DataHandler ()
    {
        KeyList = new ArrayList ();
        KeyList.add (new KeyRec ());
        KeyList.add (new KeyRec ());
        KeyList.add (new KeyRec ());
        Changed = false;
    }
    
    boolean IsChanged ()
    {
        return Changed;
    }
    
    boolean LoadData (String Fn, String Pass1, String Pass2) 
    {       
        try 
        {
            String Ln;
            ArrayList <KeyRec> TmpList = new ArrayList ();
            Path path = Paths.get (Fn);
            byte[] Data = Files.readAllBytes (path);
            byte[] AsFile = Crypto.DecryptBuf (Data, Pass1, Pass2);
            BufferedReader Br = new BufferedReader (new InputStreamReader (new ByteArrayInputStream (AsFile)));
            while ((Ln = Br.readLine ()) != null)
            {
                Ln = Ln + " ";
                String[] Str = Ln.split ("\t");
                if (Str.length != 8)
                    return false;
                Str[7] = Str[7].trim ();
                TmpList.add (new KeyRec (Str));
            }
            Br.close ();
            KeyList = TmpList;
            fireTableDataChanged ();
            Changed = false;
            return true;
        }
        catch (IOException | GmException  | OutOfMemoryError E)
        {
            return false;
        }
    }
         
    boolean SaveData (String Fn, String Pass1, String Pass2)
    {
        
        try 
        {
            ByteArrayOutputStream Bos = new ByteArrayOutputStream ();
            PrintWriter Pw = new PrintWriter (Bos);
            for (KeyRec tmp: KeyList)
            {
                tmp.Fields[7] = tmp.Fields[7].trim ();
                tmp.NL2CR ();
                String Ln = String.join ("\t", tmp.Fields);
                Pw.println (Ln);
                tmp.CR2NL ();
            }
            Pw.flush ();
            byte[] AsFile = Bos.toByteArray ();
            Pw.close ();
            byte[] Data = Crypto.EncryptBuf (AsFile, Pass1, Pass2);
            Path path = Paths.get (Fn);
            Files.write (path, Data);
            Changed = false;
            return true;
        }
        catch (IOException | GmException e)
        {
            return false;
        }
        
    }
    
    void InsertRow (int Row)
    {
        KeyRec NR = new KeyRec ();
        KeyList.add (Row, NR);
        fireTableDataChanged ();
        Changed = true;
    }
    
    void InsertRows (int Row, int Num)
    {
        int i;
        for (i = 1; i <= Num; i++)
        {
            KeyRec NR = new KeyRec ();
            KeyList.add (Row, NR);
        }
        fireTableDataChanged ();
        Changed = true;
        
    }
    
    void DeleteRow (int Row)
    {
        KeyList.remove (Row);
        fireTableDataChanged ();
        Changed = true;
    }
    
    void MoveUp (int Row)
    {
        Collections.swap (KeyList, Row, Row - 1);
        fireTableDataChanged ();
        Changed = true;
    }
    
    void MoveDown (int Row)
    {
        Collections.swap (KeyList, Row, Row + 1);
        fireTableDataChanged ();
        Changed = true;
    }
    
    void CopyRow (int Row)
    {
        Copied = new KeyRec ();
        KeyRec tbc = KeyList.get (Row);
        for (int i = 0; i < 8; i++)
            Copied.SetField (i, tbc.GetField (i));
    }
    
    void PasteRow (int Row)
    {
        KeyRec tbp = KeyList.get (Row);
        for (int i = 0; i < 8; i++)
            tbp.SetField (i, Copied.GetField (i));
        //fireTableStructureChanged();
        fireTableDataChanged ();
        Changed = true;
    }
    
    void ClearRow (int Row)
    {
        KeyRec tmp = KeyList.get (Row);
        for (int i = 0; i < 8; i++)
            tmp.SetField (i, "");
        fireTableDataChanged ();
        Changed = true;
    }
                 
    public static void main (String[] args)
    {
        DataHandler DH = new DataHandler ();
        MainFrame MF = new MainFrame (DH);
    }

    @Override
    public int getRowCount ()
    {
        return KeyList.size ();
    }

    @Override
    public int getColumnCount ()
    {
        return 8;
    }

    @Override
    public String getColumnName (int columnIndex)
    {
        return ColNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass (int columnIndex)
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable (int rowIndex, int columnIndex)
    {
        return true;
    }

    @Override
    public Object getValueAt (int rowIndex, int columnIndex)
    {
        return KeyList.get (rowIndex).Fields[columnIndex];
    }

    @Override
    public void setValueAt (Object aValue, int rowIndex, int columnIndex)
    {
        String NV = (String) aValue;
        if (!NV.equals (KeyList.get (rowIndex).GetField (columnIndex)))
        {
            KeyList.get (rowIndex).SetField (columnIndex, NV);
            Changed = true;
        }
    }
    
}

class KeyRec
{
    String[] Fields;
    
    KeyRec ()
    {
        Fields = new String[8];
        for (int i = 0; i < 8; i++)
            Fields[i] = "";
    }
    
    KeyRec (String Line)
    {
        Fields = Line.split ("\t");
        Fields[7] = Fields[7].trim ();
        CR2NL ();
    }
    
    KeyRec (String[] F)
    {
        Fields = F;
        CR2NL ();
    }
    
    void SetField (int NoF, String Val)
    {
        Fields[NoF] = Val.replace ("\\r", "\n");
    }
    
    String GetField (int NoF)
    {
        return Fields[NoF];
    }
    
    void CR2NL ()
    {
        for (int i = 0; i < 8; i++)
        {
            Fields[i] = Fields[i].replace ("\\r", "\n");
        }
    }
    
    void NL2CR ()
    {
        for (int i = 0; i < 8; i++)
            Fields[i] = Fields[i].replace ("\n", "\\r");
    }
}