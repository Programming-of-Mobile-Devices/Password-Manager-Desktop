
package org.gmele.safe;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author gmele
 */
public class PasswordDialog
{

    JPanel Panel = new JPanel ();
    JLabel LbPass1;
    JLabel LbPass2;
    JLabel LbPass3;
    JLabel LbPass4;
    JLabel LbPass5;
    JPasswordField PfPass1;
    JPasswordField PfPass2;
    JPasswordField PfPass3;
    JPasswordField PfPass4;
    JTextField PfPass5;
    String Pass1;
    String Pass2;

    boolean Show ()
    {
        boolean ContFlag;
        int option;
        Pass1 = null;
        Pass2 = null;
        Panel = new JPanel (null);
        Panel.setPreferredSize (new Dimension (470, 160));
        LbPass1 = new JLabel ("1o Συνθηματικό:", SwingConstants.RIGHT);
        LbPass1.setBounds (10, 10, 200, 20);
        LbPass2 = new JLabel ("Επιβεβαίωση 1oυ Συνθηματικού:", SwingConstants.RIGHT);
        LbPass2.setBounds (10, 40, 200, 20);
        LbPass3 = new JLabel ("2o Συνθηματικό:", SwingConstants.RIGHT);
        LbPass3.setBounds (10, 70, 200, 20);
        LbPass4 = new JLabel ("Επιβεβαίωση 2oυ Συνθηματικού:", SwingConstants.RIGHT);
        LbPass4.setBounds (10, 100, 200, 20);
        LbPass5 = new JLabel ("Δοκιμή Πληκτρολογίου:", SwingConstants.RIGHT);
        LbPass5.setBounds (10, 130, 200, 20);
        PfPass1 = new JPasswordField ();
        PfPass1.setBounds (220, 10, 230, 20);
        PfPass2 = new JPasswordField ();
        PfPass2.setBounds (220, 40, 230, 20);
        PfPass3 = new JPasswordField ();
        PfPass3.setBounds (220, 70, 230, 20);
        PfPass4 = new JPasswordField ();
        PfPass4.setBounds (220, 100, 230, 20);
        PfPass5 = new JTextField ();
        PfPass5.setBounds (220, 130, 100, 20);
        
        Panel.add (LbPass1);
        Panel.add (PfPass1);
        Panel.add (LbPass2);
        Panel.add (PfPass2);
        Panel.add (LbPass3);
        Panel.add (PfPass3);
        Panel.add (LbPass4);
        Panel.add (PfPass4);
        Panel.add (LbPass5);
        Panel.add (PfPass5);
        String[] options = new String[]
        {
            "OK", "Άκυρο", "Αντιγραφή"
        };
        do
        {
            ContFlag = false;
            option = JOptionPane.showOptionDialog (null, Panel, "Συνθηματικά Εφαρμογής", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
            if (option == 2)
            {
                PfPass2.setText(String.valueOf (PfPass1.getPassword ()));
                PfPass4.setText(String.valueOf (PfPass3.getPassword ()));
                ContFlag = true;
            }
            if (option == 0) 
            {
                Pass1 = String.valueOf (PfPass1.getPassword ());
                String Tmp1 = String.valueOf (PfPass2.getPassword ());
                Pass2 = String.valueOf (PfPass3.getPassword ()).trim ();
                String Tmp2 = String.valueOf (PfPass4.getPassword ());
                if (Pass1.length () == 0 || Pass2.length () == 0 || Tmp1.length () == 0 || Tmp2.length () == 0)
                {
                    JOptionPane.showMessageDialog (null, "Το συνθηματικό δεν μπορεί να είναι κενό.", "Λάθος Συνθηματικό", 
                        JOptionPane.ERROR_MESSAGE);
                    ContFlag = true;
                }
                if (!Pass1.equals (Tmp1))
                {
                    JOptionPane.showMessageDialog (null, "Το 1o συνθηματικό δεν ταιριάζει με το 1ο συνθηματικό επιβεβαίωσης", 
                        "Λάθος Συνθηματικό", JOptionPane.ERROR_MESSAGE);
                    ContFlag = true;
                }
                if (!Pass2.equals (Tmp2))
                {
                    JOptionPane.showMessageDialog (null, "Το 2o συνθηματικό δεν ταιριάζει με το 2ο συνθηματικό επιβεβαίωσης", 
                        "Λάθος Συνθηματικό", JOptionPane.ERROR_MESSAGE);
                    ContFlag = true;
                }
            }
        }
        while (ContFlag);
        if (option == 0)
            return true;
        return false;
    }

}
